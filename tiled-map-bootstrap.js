(() => {
  'use strict';

  const MAP_SAVE_KEY = 'ydh-chronicle-map-v1';
  const registry = window.YDH_TILED_MAP_REGISTRY || { urls: ['data/tiled/moon-gate-sample.json'], localStorageKey: 'ydh-tiled-custom-maps-v1', maxCustomMaps: 10 };

  const INLINE_TILED_MAPS = [
    {
      id: 'moon-gate-yard',
      name: '달문 광장',
      description: 'Tiled JSON에서 변환된 소형 테스트 맵입니다.',
      start: { x: 1, y: 4 },
      portalTo: 0,
      source: 'tiled-json',
      sourceUrl: 'inline-fallback',
      rows: ['TTTTTTTT', 'TSSSSMPT', 'TSTRSSST', 'TSMRSMST', 'TRRRRRST', 'TTTTTTTT']
    }
  ];

  const status = { registered: [], loaded: [], duplicates: [], failed: [], validation: [], custom: [], server: [] };

  function maps() { return window.YDH_MAPS?.maps || []; }
  function tileTypes() { return window.YDH_MAPS?.tileTypes || {}; }

  function log(message, type = 'info') {
    window.dispatchEvent(new CustomEvent('ydh-map-event', { detail: { message: `Tiled: ${message}`, type } }));
    if (type === 'error') console.warn(`[YDH Tiled] ${message}`);
    else console.info?.(`[YDH Tiled] ${message}`);
  }

  function readJson(key, fallback = null) {
    try { return JSON.parse(localStorage.getItem(key) || 'null') ?? fallback; }
    catch { return fallback; }
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

  function scopedMap(map) {
    const scope = currentScope();
    return {
      ...map,
      accountId: map.accountId || scope.accountId || 'global',
      characterId: map.characterId || scope.characterId || 'global'
    };
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
    localStorage.setItem(registry.localStorageKey, JSON.stringify(list.slice(0, registry.maxCustomMaps || 10)));
  }

  function propValue(properties = [], name, fallback = undefined) {
    const found = properties.find((item) => item.name === name);
    return found ? found.value : fallback;
  }

  function validateMap(map) {
    const errors = [];
    const warnings = [];
    if (!map.id) errors.push('id 누락');
    if (!map.name) errors.push('name 누락');
    if (!Array.isArray(map.rows) || !map.rows.length) errors.push('rows 누락');
    const width = map.rows?.[0]?.length || 0;
    map.rows?.forEach((row, index) => {
      if (row.length !== width) errors.push(`row ${index} 폭 불일치`);
      [...row].forEach((code) => { if (!tileTypes()[code]) errors.push(`알 수 없는 타일 코드: ${code}`); });
    });
    const sx = Number(map.start?.x ?? 0);
    const sy = Number(map.start?.y ?? 0);
    const startCode = map.rows?.[sy]?.[sx];
    if (!startCode) warnings.push('시작 좌표가 맵 범위 밖입니다.');
    else if (!tileTypes()[startCode]?.passable) warnings.push(`시작 타일이 이동 불가입니다: ${startCode}`);
    return { id: map.id || 'unknown', name: map.name || 'unknown', source: map.source || 'manual', ok: errors.length === 0, errors, warnings, width, height: map.rows?.length || 0 };
  }

  function appendMap(map, origin = 'runtime') {
    if (!window.YDH_MAPS?.maps) return { ok: false, reason: 'YDH_MAPS.maps not ready', map };
    const normalized = scopedMap(map);
    const validation = validateMap(normalized);
    status.validation = status.validation.filter((item) => item.id !== validation.id);
    status.validation.push(validation);
    if (!validation.ok) {
      status.failed.push({ url: normalized.sourceUrl || origin, error: validation.errors.join(', '), map: normalized });
      return { ok: false, reason: validation.errors.join(', '), map: normalized };
    }
    const existed = maps().some((item) => item.id === normalized.id);
    if (existed) {
      if (!status.duplicates.some((item) => item.id === normalized.id)) status.duplicates.push({ id: normalized.id, name: normalized.name, origin });
      return { ok: true, existed: true, map: normalized };
    }
    window.YDH_MAPS.maps.push(normalized);
    status.registered.push({ id: normalized.id, name: normalized.name, origin });
    return { ok: true, existed: false, map: normalized };
  }

  function registerInlineMaps() {
    INLINE_TILED_MAPS.forEach((map) => {
      const result = appendMap(map, 'inline-fallback');
      if (result.ok && !result.existed) log(`동기 등록됨: ${map.name}`);
    });
    publish();
  }

  async function loadUrl(url) {
    const loader = window.YDH_TILED_MAP_LOADER;
    if (!loader) throw new Error('YDH_TILED_MAP_LOADER not ready');
    const map = await loader.loadFromUrl(url);
    map.sourceUrl = url;
    return appendMap(map, url);
  }

  async function loadRegistryUrls() {
    const urls = Array.from(new Set(registry.urls || []));
    for (const url of urls) {
      try {
        const result = await loadUrl(url);
        if (!result.ok) log(`${url} 검증 실패 - ${result.reason}`, 'error');
        else log(result.existed ? `이미 등록됨: ${result.map.name}` : `맵 추가됨: ${result.map.name}`);
      } catch (error) {
        status.failed.push({ url, error: error.message });
        log(`${url} 로드 실패 - ${error.message}`, 'error');
      }
    }
  }

  function loadCustomMaps() {
    const custom = readCustomMaps();
    status.custom = custom.map((item) => ({ id: item.id, name: item.name, savedAt: item.savedAt }));
    custom.forEach((item) => appendMap({ ...item.map, source: 'tiled-json', sourceUrl: item.map?.sourceUrl || 'localStorage' }, 'localStorage'));
  }

  function convertPastedJson(text) {
    const parsed = JSON.parse(text);
    const loader = window.YDH_TILED_MAP_LOADER;
    if (!loader) throw new Error('YDH_TILED_MAP_LOADER not ready');
    const map = loader.convert(parsed, { id: propValue(parsed.properties || [], 'ydhId', `custom-tiled-${Date.now()}`) });
    map.source = 'tiled-json';
    map.sourceUrl = 'pasted-json';
    return scopedMap(map);
  }

  function saveCustomMap(map) {
    const normalized = scopedMap(map);
    const list = readCustomMaps().filter((item) => item.id !== normalized.id);
    list.unshift({ id: normalized.id, name: normalized.name, savedAt: new Date().toISOString(), map: normalized });
    writeCustomMaps(list);
    status.custom = list.map((item) => ({ id: item.id, name: item.name, savedAt: item.savedAt }));
  }

  function removeCustomMap(id) {
    const list = readCustomMaps().filter((item) => item.id !== id);
    writeCustomMaps(list);
    status.custom = list.map((item) => ({ id: item.id, name: item.name, savedAt: item.savedAt }));
    window.YDH_MAPS.maps = maps().filter((map) => !(map.id === id && map.sourceUrl === 'localStorage'));
  }

  function customRecordFor(id) {
    return readCustomMaps().find((item) => item.id === id) || null;
  }

  function exportMap(map) {
    const blob = new Blob([JSON.stringify(map, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = `${map.id || 'ydh-custom-map'}.json`;
    anchor.click();
    URL.revokeObjectURL(url);
    log(`맵 내보내기: ${map.name}`);
  }

  async function saveMapToServer(map) {
    const scoped = scopedMap(map);
    const response = await fetch(`/api/maps/custom${scopeQuery()}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...currentScope(), map: scoped })
    });
    const result = await response.json().catch(() => ({}));
    if (!response.ok || !result.ok) throw new Error(result.error || `HTTP ${response.status}`);
    status.server = status.server.filter((item) => item.id !== scoped.id);
    status.server.unshift({ id: scoped.id, name: scoped.name, accountId: scoped.accountId, characterId: scoped.characterId, savedAt: result.map?.updatedAt || new Date().toISOString() });
    return result;
  }

  async function listServerMaps() {
    const response = await fetch(`/api/maps/custom${scopeQuery()}`);
    const result = await response.json().catch(() => ({}));
    if (!response.ok || !result.ok) throw new Error(result.error || `HTTP ${response.status}`);
    status.server = result.maps || [];
    return result.maps || [];
  }

  function publish() {
    window.YDH_TILED_BOOTSTRAP = { registered: [...status.registered], duplicates: [...status.duplicates], failed: [...status.failed], validation: [...status.validation], custom: [...status.custom], server: [...status.server], urls: [...(registry.urls || [])], loadedAt: new Date().toISOString(), scope: currentScope() };
    window.dispatchEvent(new CustomEvent('ydh-tiled-maps-loaded', { detail: window.YDH_TILED_BOOTSTRAP }));
  }

  function injectStyles() {
    if (document.getElementById('tiledMapManagerStyles')) return;
    const style = document.createElement('style');
    style.id = 'tiledMapManagerStyles';
    style.textContent = '.tiled-manager-panel{margin:22px 0;padding:24px}.tiled-manager-head{display:flex;justify-content:space-between;gap:18px;align-items:flex-start;margin-bottom:16px}.tiled-manager-head h2{margin:0 0 10px}.tiled-manager-head p{margin:0;color:var(--muted);line-height:1.65}.tiled-manager-badge{min-width:150px;padding:12px 14px;border-radius:18px;background:rgba(115,167,255,.1);border:1px solid rgba(115,167,255,.24);color:#9fc5ff;font-weight:900;text-align:center}.tiled-import-box{display:grid;gap:8px;margin-bottom:14px}.tiled-import-box textarea{min-height:120px;border-radius:16px;border:1px solid var(--line);background:rgba(0,0,0,.24);color:#eef4ff;padding:12px;resize:vertical}.tiled-import-actions{display:flex;gap:8px;flex-wrap:wrap}.tiled-import-box button,.tiled-map-actions button{border:1px solid var(--line);border-radius:999px;background:rgba(255,255,255,.06);color:var(--muted);padding:8px 11px;font-weight:900;cursor:pointer}.tiled-import-box button:hover,.tiled-map-actions button:hover{background:var(--gold);color:#1b1207}.tiled-map-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:10px}.tiled-map-card{padding:14px;border-radius:18px;background:rgba(0,0,0,.22);border:1px solid rgba(255,255,255,.08)}.tiled-map-card.tiled{border-color:rgba(105,221,160,.22)}.tiled-map-card.custom{border-color:rgba(247,200,95,.24)}.tiled-map-card strong{display:block;color:#eef4ff;margin-bottom:5px}.tiled-map-card small{display:block;color:var(--muted);font-size:.76rem;line-height:1.45}.tiled-map-actions{display:flex;gap:8px;flex-wrap:wrap;margin-top:10px}.tiled-validation{margin-top:14px;padding:12px;border-radius:16px;background:rgba(0,0,0,.18);border:1px solid rgba(255,255,255,.07);color:var(--muted);font-size:.8rem;line-height:1.6}.tiled-validation b{color:#eef4ff}.tiled-ok{color:#69dda0}.tiled-warn{color:#f7c85f}.tiled-error{color:#ff7b7b}@media(max-width:820px){.tiled-manager-head{flex-direction:column}.tiled-manager-badge{width:100%;text-align:left}.tiled-map-grid{grid-template-columns:1fr}.tiled-map-actions button,.tiled-import-actions button{flex:1}}';
    document.head.appendChild(style);
  }

  function renderManager() {
    if (!document.body) return;
    injectStyles();
    let section = document.getElementById('tiled-manager');
    if (!section) {
      section = document.createElement('section');
      section.className = 'panel tiled-manager-panel';
      section.id = 'tiled-manager';
      const assets = document.getElementById('assets');
      if (assets?.parentNode) assets.parentNode.insertBefore(section, assets.nextSibling);
      else document.querySelector('main')?.appendChild(section);
    }
    const allMaps = maps();
    const tiledCount = allMaps.filter((map) => map.source === 'tiled-json').length;
    const scope = currentScope();
    section.innerHTML = `<div class="tiled-manager-head"><div><p class="eyebrow">TILED MAP MANAGER</p><h2>Tiled JSON 맵 선택/검증</h2><p>custom map을 localStorage, JSON 파일, 서버 API로 관리합니다. Scope: ${escapeHtml(scope.accountId || 'global')} / ${escapeHtml(scope.characterId || 'global')}</p></div><div class="tiled-manager-badge">전체 ${allMaps.length}개<br />Tiled ${tiledCount}개</div></div><div class="tiled-import-box"><textarea id="tiledJsonPaste" placeholder="Tiled JSON 내용을 여기에 붙여넣고 등록하세요."></textarea><div class="tiled-import-actions"><button type="button" id="importTiledJson">붙여넣기 JSON 등록</button><button type="button" id="refreshServerMaps">서버 맵 목록</button></div></div><div class="tiled-map-grid">${allMaps.map((map, index) => renderMapCard(map, index)).join('')}</div><div class="tiled-validation">${renderValidation()}</div>`;
    section.querySelectorAll('[data-select-map-index]').forEach((button) => button.addEventListener('click', () => selectMap(Number(button.dataset.selectMapIndex))));
    section.querySelectorAll('[data-export-map-id]').forEach((button) => button.addEventListener('click', () => exportById(button.dataset.exportMapId)));
    section.querySelectorAll('[data-delete-custom-map-id]').forEach((button) => button.addEventListener('click', () => deleteById(button.dataset.deleteCustomMapId)));
    section.querySelectorAll('[data-save-server-map-id]').forEach((button) => button.addEventListener('click', () => saveByIdToServer(button.dataset.saveServerMapId)));
    section.querySelector('#importTiledJson')?.addEventListener('click', () => importFromTextarea(section.querySelector('#tiledJsonPaste')));
    section.querySelector('#refreshServerMaps')?.addEventListener('click', refreshServerMaps);
  }

  function renderMapCard(map, index) {
    const isCustom = !!customRecordFor(map.id);
    const source = map.source === 'tiled-json' ? `Tiled JSON${map.sourceUrl ? ` · ${map.sourceUrl}` : ''}` : '기본 문자맵';
    const server = status.server.find((item) => item.id === map.id);
    return `<article class="tiled-map-card ${map.source === 'tiled-json' ? 'tiled' : ''} ${isCustom ? 'custom' : ''}"><strong>${escapeHtml(map.name)}</strong><small>${escapeHtml(source)} · ${escapeHtml(map.id)} · ${map.rows?.[0]?.length || 0}x${map.rows?.length || 0}</small><small>Scope ${escapeHtml(map.accountId || 'global')} / ${escapeHtml(map.characterId || 'global')} · 시작 X:${map.start?.x ?? 0} Y:${map.start?.y ?? 0}${server ? ` · 서버저장 ${escapeHtml(server.updatedAt || server.savedAt || '')}` : ''}</small><div class="tiled-map-actions"><button type="button" data-select-map-index="${index}">이 맵으로 이동</button><button type="button" data-export-map-id="${escapeHtml(map.id)}">내보내기</button>${isCustom ? `<button type="button" data-delete-custom-map-id="${escapeHtml(map.id)}">삭제</button><button type="button" data-save-server-map-id="${escapeHtml(map.id)}">서버저장</button>` : ''}</div></article>`;
  }

  function renderValidation() {
    const lines = [`<b>검증</b> ${status.validation.length}개 맵 검사 / custom ${status.custom.length}개 / server ${status.server.length}개`];
    status.validation.forEach((item) => {
      const cls = item.ok ? 'tiled-ok' : 'tiled-error';
      const msg = item.ok ? 'OK' : item.errors.join(', ');
      const warn = item.warnings.length ? ` <span class="tiled-warn">경고: ${escapeHtml(item.warnings.join(', '))}</span>` : '';
      lines.push(`<span class="${cls}">${escapeHtml(item.name)}: ${escapeHtml(msg)}</span>${warn}`);
    });
    if (status.duplicates.length) lines.push(`<span class="tiled-warn">중복: ${escapeHtml(status.duplicates.map((item) => item.id).join(', '))}</span>`);
    if (status.failed.length) lines.push(`<span class="tiled-error">실패: ${escapeHtml(status.failed.map((item) => `${item.url} ${item.error}`).join(' / '))}</span>`);
    return lines.join('<br />');
  }

  function importFromTextarea(textarea) {
    try {
      const map = convertPastedJson(textarea?.value || '');
      const result = appendMap(map, 'pasted-json');
      if (!result.ok) throw new Error(result.reason);
      saveCustomMap(result.map);
      log(`붙여넣기 맵 등록: ${result.map.name}`);
      publish();
      renderManager();
    } catch (error) {
      status.failed.push({ url: 'pasted-json', error: error.message });
      log(`붙여넣기 등록 실패 - ${error.message}`, 'error');
      publish();
      renderManager();
    }
  }

  function mapById(id) {
    return maps().find((map) => map.id === id) || null;
  }

  function exportById(id) {
    const map = mapById(id);
    if (map) exportMap(map);
  }

  function deleteById(id) {
    removeCustomMap(id);
    log(`custom map 삭제: ${id}`);
    publish();
    renderManager();
  }

  async function saveByIdToServer(id) {
    const map = mapById(id);
    if (!map) return;
    try {
      await saveMapToServer(map);
      log(`서버 저장 완료: ${map.name}`);
      publish();
      renderManager();
    } catch (error) {
      status.failed.push({ url: 'server-save', error: error.message });
      log(`서버 저장 실패 - ${error.message}`, 'error');
      publish();
      renderManager();
    }
  }

  async function refreshServerMaps() {
    try {
      await listServerMaps();
      log('서버 custom map 목록 갱신 완료');
      publish();
      renderManager();
    } catch (error) {
      status.failed.push({ url: 'server-list', error: error.message });
      log(`서버 맵 목록 실패 - ${error.message}`, 'error');
      publish();
      renderManager();
    }
  }

  function selectMap(index) {
    const map = maps()[index];
    if (!map) return;
    const start = map.start || { x: 0, y: 0 };
    localStorage.setItem(MAP_SAVE_KEY, JSON.stringify({ mapIndex: index, x: start.x, y: start.y, steps: 0, direction: 12, lastTarget: null }));
    window.dispatchEvent(new CustomEvent('ydh-map-event', { detail: { message: `맵 선택: ${map.name}` } }));
    window.location.reload();
  }

  function escapeHtml(value) {
    return String(value ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
  }

  async function boot() {
    registerInlineMaps();
    loadCustomMaps();
    renderManager();
    await loadRegistryUrls();
    publish();
    renderManager();
    window.addEventListener('ydh-character-selected', renderManager);
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
