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

  const status = { registered: [], loaded: [], duplicates: [], failed: [], validation: [], custom: [] };

  function maps() { return window.YDH_MAPS?.maps || []; }
  function tileTypes() { return window.YDH_MAPS?.tileTypes || {}; }

  function log(message, type = 'info') {
    window.dispatchEvent(new CustomEvent('ydh-map-event', { detail: { message: `Tiled: ${message}`, type } }));
    if (type === 'error') console.warn(`[YDH Tiled] ${message}`);
    else console.info?.(`[YDH Tiled] ${message}`);
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
    const validation = validateMap(map);
    status.validation = status.validation.filter((item) => item.id !== validation.id);
    status.validation.push(validation);
    if (!validation.ok) {
      status.failed.push({ url: map.sourceUrl || origin, error: validation.errors.join(', '), map });
      return { ok: false, reason: validation.errors.join(', '), map };
    }
    const existed = maps().some((item) => item.id === map.id);
    if (existed) {
      if (!status.duplicates.some((item) => item.id === map.id)) status.duplicates.push({ id: map.id, name: map.name, origin });
      return { ok: true, existed: true, map };
    }
    window.YDH_MAPS.maps.push(map);
    status.registered.push({ id: map.id, name: map.name, origin });
    return { ok: true, existed: false, map };
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
    custom.forEach((item) => appendMap({ ...item.map, source: 'tiled-json', sourceUrl: 'localStorage' }, 'localStorage'));
  }

  function convertPastedJson(text) {
    const parsed = JSON.parse(text);
    const loader = window.YDH_TILED_MAP_LOADER;
    if (!loader) throw new Error('YDH_TILED_MAP_LOADER not ready');
    const map = loader.convert(parsed, { id: propValue(parsed.properties || [], 'ydhId', `custom-tiled-${Date.now()}`) });
    map.source = 'tiled-json';
    map.sourceUrl = 'pasted-json';
    return map;
  }

  function saveCustomMap(map) {
    const list = readCustomMaps().filter((item) => item.id !== map.id);
    list.unshift({ id: map.id, name: map.name, savedAt: new Date().toISOString(), map });
    writeCustomMaps(list);
    status.custom = list.map((item) => ({ id: item.id, name: item.name, savedAt: item.savedAt }));
  }

  function publish() {
    window.YDH_TILED_BOOTSTRAP = { registered: [...status.registered], duplicates: [...status.duplicates], failed: [...status.failed], validation: [...status.validation], custom: [...status.custom], urls: [...(registry.urls || [])], loadedAt: new Date().toISOString() };
    window.dispatchEvent(new CustomEvent('ydh-tiled-maps-loaded', { detail: window.YDH_TILED_BOOTSTRAP }));
  }

  function injectStyles() {
    if (document.getElementById('tiledMapManagerStyles')) return;
    const style = document.createElement('style');
    style.id = 'tiledMapManagerStyles';
    style.textContent = '.tiled-manager-panel{margin:22px 0;padding:24px}.tiled-manager-head{display:flex;justify-content:space-between;gap:18px;align-items:flex-start;margin-bottom:16px}.tiled-manager-head h2{margin:0 0 10px}.tiled-manager-head p{margin:0;color:var(--muted);line-height:1.65}.tiled-manager-badge{min-width:150px;padding:12px 14px;border-radius:18px;background:rgba(115,167,255,.1);border:1px solid rgba(115,167,255,.24);color:#9fc5ff;font-weight:900;text-align:center}.tiled-import-box{display:grid;gap:8px;margin-bottom:14px}.tiled-import-box textarea{min-height:120px;border-radius:16px;border:1px solid var(--line);background:rgba(0,0,0,.24);color:#eef4ff;padding:12px;resize:vertical}.tiled-import-box button,.tiled-map-actions button{border:1px solid var(--line);border-radius:999px;background:rgba(255,255,255,.06);color:var(--muted);padding:8px 11px;font-weight:900;cursor:pointer}.tiled-import-box button:hover,.tiled-map-actions button:hover{background:var(--gold);color:#1b1207}.tiled-map-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:10px}.tiled-map-card{padding:14px;border-radius:18px;background:rgba(0,0,0,.22);border:1px solid rgba(255,255,255,.08)}.tiled-map-card.tiled{border-color:rgba(105,221,160,.22)}.tiled-map-card strong{display:block;color:#eef4ff;margin-bottom:5px}.tiled-map-card small{display:block;color:var(--muted);font-size:.76rem;line-height:1.45}.tiled-map-actions{display:flex;gap:8px;flex-wrap:wrap;margin-top:10px}.tiled-validation{margin-top:14px;padding:12px;border-radius:16px;background:rgba(0,0,0,.18);border:1px solid rgba(255,255,255,.07);color:var(--muted);font-size:.8rem;line-height:1.6}.tiled-validation b{color:#eef4ff}.tiled-ok{color:#69dda0}.tiled-warn{color:#f7c85f}.tiled-error{color:#ff7b7b}@media(max-width:820px){.tiled-manager-head{flex-direction:column}.tiled-manager-badge{width:100%;text-align:left}.tiled-map-grid{grid-template-columns:1fr}.tiled-map-actions button{flex:1}}';
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
    section.innerHTML = `<div class="tiled-manager-head"><div><p class="eyebrow">TILED MAP MANAGER</p><h2>Tiled JSON 맵 선택/검증</h2><p>여러 Tiled JSON URL과 붙여넣기 import 맵을 기존 맵 구조에 추가합니다.</p></div><div class="tiled-manager-badge">전체 ${allMaps.length}개<br />Tiled ${tiledCount}개</div></div><div class="tiled-import-box"><textarea id="tiledJsonPaste" placeholder="Tiled JSON 내용을 여기에 붙여넣고 등록하세요."></textarea><button type="button" id="importTiledJson">붙여넣기 JSON 등록</button></div><div class="tiled-map-grid">${allMaps.map((map, index) => renderMapCard(map, index)).join('')}</div><div class="tiled-validation">${renderValidation()}</div>`;
    section.querySelectorAll('[data-select-map-index]').forEach((button) => button.addEventListener('click', () => selectMap(Number(button.dataset.selectMapIndex))));
    section.querySelector('#importTiledJson')?.addEventListener('click', () => importFromTextarea(section.querySelector('#tiledJsonPaste')));
  }

  function renderMapCard(map, index) {
    const source = map.source === 'tiled-json' ? `Tiled JSON${map.sourceUrl ? ` · ${map.sourceUrl}` : ''}` : '기본 문자맵';
    return `<article class="tiled-map-card ${map.source === 'tiled-json' ? 'tiled' : ''}"><strong>${escapeHtml(map.name)}</strong><small>${escapeHtml(source)} · ${escapeHtml(map.id)} · ${map.rows?.[0]?.length || 0}x${map.rows?.length || 0}</small><small>시작 X:${map.start?.x ?? 0} Y:${map.start?.y ?? 0} · 포탈:${map.portalTo ?? '-'}</small><div class="tiled-map-actions"><button type="button" data-select-map-index="${index}">이 맵으로 이동</button></div></article>`;
  }

  function renderValidation() {
    const lines = [`<b>검증</b> ${status.validation.length}개 맵 검사 / custom ${status.custom.length}개`];
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
      saveCustomMap(map);
      log(`붙여넣기 맵 등록: ${map.name}`);
      publish();
      renderManager();
    } catch (error) {
      status.failed.push({ url: 'pasted-json', error: error.message });
      log(`붙여넣기 등록 실패 - ${error.message}`, 'error');
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
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
