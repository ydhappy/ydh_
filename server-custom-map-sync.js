(() => {
  'use strict';

  const PREF_KEY = 'ydh-server-custom-map-auto-sync-v1';
  const registry = window.YDH_TILED_MAP_REGISTRY || {
    localStorageKey: 'ydh-tiled-custom-maps-v1',
    maxCustomMaps: 10
  };

  const state = {
    enabled: localStorage.getItem(PREF_KEY) === '1',
    syncing: false,
    lastResult: null,
    error: ''
  };

  function log(message, type = 'info') {
    window.dispatchEvent(new CustomEvent('ydh-map-event', {
      detail: { message: `ServerMapSync: ${message}`, type }
    }));
    if (type === 'error') console.warn(`[YDH ServerMapSync] ${message}`);
    else console.info?.(`[YDH ServerMapSync] ${message}`);
  }

  function maps() {
    return window.YDH_MAPS?.maps || [];
  }

  function readJson(key, fallback = null) {
    try {
      return JSON.parse(localStorage.getItem(key) || 'null') ?? fallback;
    } catch {
      return fallback;
    }
  }

  function currentScope() {
    const keys = window.YDH_SAVE_SCHEMA?.localStorageKeys || {};
    const account = readJson(keys.accountProfile || 'ydh-account-profile-v1', null);
    const selected = readJson(keys.selectedCharacter || 'ydh-selected-character-v1', null);
    return {
      accountId: selected?.accountId || account?.accountId || '',
      characterId: selected?.characterId || ''
    };
  }

  function scopeQuery() {
    const scope = currentScope();
    const query = new URLSearchParams();
    if (scope.accountId) query.set('accountId', scope.accountId);
    if (scope.characterId) query.set('characterId', scope.characterId);
    const text = query.toString();
    return text ? `?${text}` : '';
  }

  function scopeLabel() {
    const scope = currentScope();
    return `${scope.accountId || 'global'} / ${scope.characterId || 'global'}`;
  }

  function readCustomMaps() {
    try {
      const list = JSON.parse(localStorage.getItem(registry.localStorageKey) || '[]');
      return Array.isArray(list) ? list : [];
    } catch {
      return [];
    }
  }

  function writeCustomMaps(list) {
    const max = Number(registry.maxCustomMaps || 10);
    localStorage.setItem(registry.localStorageKey, JSON.stringify(list.slice(0, max)));
  }

  function saveLocalCustomMap(map) {
    const list = readCustomMaps().filter((item) => item.id !== map.id);
    list.unshift({ id: map.id, name: map.name, savedAt: new Date().toISOString(), map });
    writeCustomMaps(list);
  }

  function validateMap(map) {
    const errors = [];
    if (!map || typeof map !== 'object') errors.push('map object missing');
    if (!map?.id) errors.push('map.id missing');
    if (!map?.name) errors.push('map.name missing');
    if (!Array.isArray(map?.rows) || !map.rows.length) errors.push('map.rows missing');
    return { ok: errors.length === 0, errors };
  }

  function appendMap(map) {
    const validation = validateMap(map);
    if (!validation.ok) return { ok: false, reason: validation.errors.join(', ') };
    if (!window.YDH_MAPS?.maps) return { ok: false, reason: 'YDH_MAPS.maps not ready' };

    const scope = currentScope();
    const normalized = {
      ...map,
      accountId: map.accountId || scope.accountId || 'global',
      characterId: map.characterId || scope.characterId || 'global',
      source: map.source || 'tiled-json',
      sourceUrl: map.sourceUrl || 'server-auto-sync'
    };

    const exists = maps().some((item) => item.id === normalized.id);
    saveLocalCustomMap(normalized);
    if (!exists) window.YDH_MAPS.maps.push(normalized);
    return { ok: true, existed: exists, map: normalized };
  }

  async function fetchJson(url, options) {
    const response = await fetch(url, options);
    const result = await response.json().catch(() => ({}));
    if (!response.ok || result.ok === false) throw new Error(result.error || `HTTP ${response.status}`);
    return result;
  }

  async function fetchServerMap(id) {
    const result = await fetchJson(`/api/maps/custom/${encodeURIComponent(id)}${scopeQuery()}`);
    return result.map?.map || result.map;
  }

  async function listServerMaps() {
    const result = await fetchJson(`/api/maps/custom${scopeQuery()}`);
    return result.maps || [];
  }

  async function syncNow(reason = 'manual') {
    if (state.syncing) return state.lastResult;
    state.syncing = true;
    state.error = '';
    render();

    const summary = {
      reason,
      scope: currentScope(),
      found: 0,
      imported: 0,
      updatedLocal: 0,
      skipped: 0,
      failed: 0,
      errors: [],
      at: new Date().toISOString()
    };

    try {
      const serverMaps = await listServerMaps();
      summary.found = serverMaps.length;

      for (const item of serverMaps) {
        try {
          const map = await fetchServerMap(item.id);
          const result = appendMap({ ...map, source: 'tiled-json', sourceUrl: 'server-auto-sync' });
          if (!result.ok) throw new Error(result.reason);
          if (result.existed) summary.updatedLocal += 1;
          else summary.imported += 1;
        } catch (error) {
          summary.failed += 1;
          summary.errors.push(`${item.id}: ${error.message}`);
        }
      }

      state.lastResult = summary;
      if (summary.failed) {
        state.error = summary.errors.join(' / ');
        log(`동기화 일부 실패: ${summary.failed}건`, 'error');
      } else {
        log(`동기화 완료: 신규 ${summary.imported}, 갱신 ${summary.updatedLocal}`);
      }

      window.dispatchEvent(new CustomEvent('ydh-server-custom-maps-synced', { detail: summary }));
    } catch (error) {
      summary.failed += 1;
      summary.errors.push(error.message);
      state.error = error.message;
      state.lastResult = summary;
      log(`동기화 실패 - ${error.message}`, 'error');
    } finally {
      state.syncing = false;
      render();
    }

    return state.lastResult;
  }

  function setEnabled(enabled) {
    state.enabled = !!enabled;
    localStorage.setItem(PREF_KEY, state.enabled ? '1' : '0');
    log(state.enabled ? '자동 동기화 ON' : '자동 동기화 OFF');
    render();
    if (state.enabled) syncNow('enabled');
  }

  function injectStyles() {
    if (document.getElementById('serverCustomMapSyncStyles')) return;
    const style = document.createElement('style');
    style.id = 'serverCustomMapSyncStyles';
    style.textContent = `
      .server-map-sync-panel{margin:14px 0 22px;padding:18px;border-radius:20px;background:rgba(0,0,0,.2);border:1px solid rgba(115,167,255,.22)}
      .server-map-sync-head{display:flex;justify-content:space-between;gap:14px;align-items:flex-start;margin-bottom:12px}
      .server-map-sync-head h3{margin:0 0 6px;color:#eef4ff}.server-map-sync-head p{margin:0;color:var(--muted);line-height:1.55}
      .server-map-sync-badge{padding:9px 12px;border-radius:999px;background:rgba(115,167,255,.12);color:#9fc5ff;font-weight:900;white-space:nowrap}
      .server-map-sync-actions{display:flex;gap:9px;flex-wrap:wrap;align-items:center}.server-map-sync-actions button{border:1px solid var(--line);border-radius:999px;background:rgba(255,255,255,.06);color:var(--muted);padding:8px 12px;font-weight:900;cursor:pointer}.server-map-sync-actions button:hover{background:var(--gold);color:#1b1207}.server-map-sync-actions label{display:flex;gap:8px;align-items:center;color:var(--muted);font-weight:900}.server-map-sync-status{margin-top:10px;color:var(--muted);font-size:.82rem;line-height:1.55}.server-map-sync-error{color:#ff7b7b}@media(max-width:820px){.server-map-sync-head{flex-direction:column}.server-map-sync-badge{width:100%;box-sizing:border-box}.server-map-sync-actions button{flex:1}}
    `;
    document.head.appendChild(style);
  }

  function resultText() {
    if (state.syncing) return '동기화 중입니다.';
    if (!state.lastResult) return `현재 범위: ${scopeLabel()} · 아직 동기화 기록이 없습니다.`;
    const r = state.lastResult;
    return `범위 ${scopeLabel()} · 최근 ${r.at} · 서버 ${r.found}개 · 신규 ${r.imported} · 로컬갱신 ${r.updatedLocal} · 실패 ${r.failed}`;
  }

  function render() {
    if (!document.body) return;
    injectStyles();

    let panel = document.getElementById('serverCustomMapSync');
    if (!panel) {
      panel = document.createElement('section');
      panel.id = 'serverCustomMapSync';
      panel.className = 'server-map-sync-panel';
      const tiledManager = document.getElementById('tiled-manager');
      if (tiledManager?.parentNode) tiledManager.parentNode.insertBefore(panel, tiledManager.nextSibling);
      else document.querySelector('main')?.appendChild(panel);
    }

    panel.innerHTML = `
      <div class="server-map-sync-head">
        <div>
          <p class="eyebrow">SERVER CUSTOM MAP SYNC</p>
          <h3>서버 custom map 자동 동기화</h3>
          <p>현재 계정/캐릭터 범위의 서버 custom map을 클라이언트 맵 목록과 localStorage custom map으로 가져옵니다.</p>
        </div>
        <div class="server-map-sync-badge">${state.enabled ? 'AUTO ON' : 'AUTO OFF'}</div>
      </div>
      <div class="server-map-sync-actions">
        <label><input type="checkbox" id="serverMapAutoSyncToggle" ${state.enabled ? 'checked' : ''} /> 자동 동기화</label>
        <button type="button" id="serverMapSyncNow" ${state.syncing ? 'disabled' : ''}>지금 동기화</button>
      </div>
      <div class="server-map-sync-status ${state.error ? 'server-map-sync-error' : ''}">${escapeHtml(state.error || resultText())}</div>
    `;

    panel.querySelector('#serverMapAutoSyncToggle')?.addEventListener('change', (event) => setEnabled(event.target.checked));
    panel.querySelector('#serverMapSyncNow')?.addEventListener('click', () => syncNow('manual'));
  }

  function escapeHtml(value) {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function boot() {
    render();
    window.YDH_SERVER_CUSTOM_MAP_SYNC = { syncNow, setEnabled, state, currentScope };
    window.addEventListener('ydh-tiled-maps-loaded', render);
    window.addEventListener('ydh-character-selected', () => {
      render();
      if (state.enabled) syncNow('character-selected');
    });
    if (state.enabled) syncNow('boot');
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
