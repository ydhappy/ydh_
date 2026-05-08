(() => {
  'use strict';

  const schema = window.YDH_SAVE_SCHEMA;
  if (!schema) return;

  const STATUS_KEY = 'ydh-server-sync-status-v1';

  function readJson(key) {
    try {
      return JSON.parse(localStorage.getItem(key) || 'null');
    } catch {
      return null;
    }
  }

  function nowIso() {
    return new Date().toISOString();
  }

  function buildSnapshot() {
    const keys = schema.localStorageKeys;
    return {
      schemaVersion: schema.version,
      app: schema.app,
      generatedAt: nowIso(),
      client: {
        userAgent: navigator.userAgent,
        language: navigator.language,
        timezone: Intl.DateTimeFormat().resolvedOptions().timeZone || 'unknown'
      },
      saves: {
        character: readJson(keys.character),
        map: readJson(keys.map),
        chapterQuests: readJson(keys.chapterQuests),
        codexUnlocks: readJson(keys.codexUnlocks),
        gmConsoleOpen: localStorage.getItem(keys.gmConsoleOpen)
      }
    };
  }

  function saveStatus(status) {
    localStorage.setItem(STATUS_KEY, JSON.stringify({ ...status, updatedAt: nowIso() }));
    window.dispatchEvent(new CustomEvent('ydh-server-sync-status', { detail: status }));
  }

  function loadStatus() {
    return readJson(STATUS_KEY) || { mode: 'local-only', lastSyncAt: null, lastError: null };
  }

  async function pushSnapshot(options = {}) {
    const endpoint = options.endpoint || schema.api.endpoints.snapshot;
    const baseUrl = options.baseUrl || schema.api.baseUrl;
    const url = `${baseUrl}${endpoint.startsWith('/') ? endpoint.replace(/^\/api/, '') : `/${endpoint}`}`;
    const snapshot = buildSnapshot();

    try {
      saveStatus({ mode: 'syncing', lastSyncAt: null, lastError: null });
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(snapshot)
      });
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const result = await response.json().catch(() => ({ ok: true }));
      saveStatus({ mode: 'synced', lastSyncAt: nowIso(), lastError: null, result });
      return { ok: true, result, snapshot };
    } catch (error) {
      saveStatus({ mode: 'local-only', lastSyncAt: null, lastError: error.message });
      return { ok: false, error: error.message, snapshot };
    }
  }

  function exportSnapshot() {
    const snapshot = buildSnapshot();
    const blob = new Blob([JSON.stringify(snapshot, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `ydh-chronicle-save-${Date.now()}.json`;
    a.click();
    URL.revokeObjectURL(url);
    saveStatus({ mode: 'exported', lastSyncAt: nowIso(), lastError: null });
    return snapshot;
  }

  window.YDH_SERVER_SYNC = {
    buildSnapshot,
    pushSnapshot,
    exportSnapshot,
    loadStatus,
    saveStatus
  };
})();
