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

  function writeJson(key, value) {
    if (value === undefined || value === null) localStorage.removeItem(key);
    else localStorage.setItem(key, JSON.stringify(value));
  }

  function nowIso() {
    return new Date().toISOString();
  }

  function endpointUrl(endpoint) {
    const baseUrl = schema.api.baseUrl || '/api';
    return `${baseUrl}${endpoint.startsWith('/') ? endpoint.replace(/^\/api/, '') : `/${endpoint}`}`;
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
      account: readJson(keys.accountProfile),
      selectedCharacter: readJson(keys.selectedCharacter),
      characterSlots: readJson(keys.characterSlots) || [],
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
    return readJson(STATUS_KEY) || { mode: 'local-only', lastSyncAt: null, lastRestoreAt: null, lastError: null };
  }

  async function pushSnapshot(options = {}) {
    const endpoint = options.endpoint || schema.api.endpoints.snapshot;
    const url = endpointUrl(endpoint);
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

  async function listServerSaves() {
    try {
      saveStatus({ ...loadStatus(), mode: 'listing', lastError: null });
      const response = await fetch(endpointUrl('/api/save/list'));
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const result = await response.json();
      saveStatus({ ...loadStatus(), mode: 'listed', lastError: null, listCount: result.saves?.length || 0 });
      return { ok: true, saves: result.saves || [] };
    } catch (error) {
      saveStatus({ ...loadStatus(), mode: 'local-only', lastError: error.message });
      return { ok: false, error: error.message, saves: [] };
    }
  }

  async function restoreLatestSnapshot(options = {}) {
    try {
      saveStatus({ ...loadStatus(), mode: 'restoring', lastError: null });
      const response = await fetch(endpointUrl(schema.api.endpoints.restore));
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const result = await response.json();
      const record = result.save;
      const snapshot = record?.snapshot;
      if (!snapshot?.saves) throw new Error('Invalid restore payload');
      applySnapshotToLocalStorage(snapshot);
      saveStatus({ ...loadStatus(), mode: 'restored', lastRestoreAt: nowIso(), lastError: null, restoredId: record.id });
      if (options.reload) window.location.reload();
      return { ok: true, record, snapshot };
    } catch (error) {
      saveStatus({ ...loadStatus(), mode: 'local-only', lastError: error.message });
      return { ok: false, error: error.message };
    }
  }

  async function restoreSnapshotById(id, options = {}) {
    try {
      if (!id) throw new Error('Save id is required');
      saveStatus({ ...loadStatus(), mode: 'restoring-selected', lastError: null });
      const response = await fetch(endpointUrl(`/api/save/${encodeURIComponent(id)}`));
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const result = await response.json();
      const record = result.save;
      const snapshot = record?.snapshot;
      if (!snapshot?.saves) throw new Error('Invalid restore payload');
      applySnapshotToLocalStorage(snapshot);
      saveStatus({ ...loadStatus(), mode: 'restored-selected', lastRestoreAt: nowIso(), lastError: null, restoredId: record.id });
      if (options.reload) window.location.reload();
      return { ok: true, record, snapshot };
    } catch (error) {
      saveStatus({ ...loadStatus(), mode: 'local-only', lastError: error.message });
      return { ok: false, error: error.message };
    }
  }

  function applySnapshotToLocalStorage(snapshot) {
    const keys = schema.localStorageKeys;
    const saves = snapshot?.saves || {};
    writeJson(keys.accountProfile, snapshot.account);
    writeJson(keys.selectedCharacter, snapshot.selectedCharacter);
    writeJson(keys.characterSlots, snapshot.characterSlots || []);
    writeJson(keys.character, saves.character);
    writeJson(keys.map, saves.map);
    writeJson(keys.chapterQuests, saves.chapterQuests);
    writeJson(keys.codexUnlocks, saves.codexUnlocks);
    if (saves.gmConsoleOpen === undefined || saves.gmConsoleOpen === null) localStorage.removeItem(keys.gmConsoleOpen);
    else localStorage.setItem(keys.gmConsoleOpen, saves.gmConsoleOpen);
    window.dispatchEvent(new CustomEvent('ydh-server-restore-applied', { detail: { snapshot } }));
    return true;
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
    saveStatus({ ...loadStatus(), mode: 'exported', lastSyncAt: nowIso(), lastError: null });
    return snapshot;
  }

  window.YDH_SERVER_SYNC = {
    buildSnapshot,
    pushSnapshot,
    listServerSaves,
    restoreLatestSnapshot,
    restoreSnapshotById,
    applySnapshotToLocalStorage,
    exportSnapshot,
    loadStatus,
    saveStatus
  };
})();
