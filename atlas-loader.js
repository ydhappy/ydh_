(() => {
  'use strict';

  const atlas = window.YDH_ATLAS;
  if (!atlas?.tiles) return;

  const state = {
    selected: atlas.tiles.image,
    format: 'svg',
    checked: [],
    error: ''
  };

  function candidateList() {
    const tiles = atlas.tiles;
    const formats = tiles.preferredFormats || ['webp', 'png', 'svg'];
    const byFormat = {
      webp: tiles.imageWebp,
      png: tiles.imagePng,
      svg: tiles.image
    };
    return formats.map((format) => ({ format, url: byFormat[format] })).filter((item) => item.url);
  }

  function probeImage(url) {
    return new Promise((resolve) => {
      const image = new Image();
      image.onload = () => resolve(true);
      image.onerror = () => resolve(false);
      image.src = `${url}${url.includes('?') ? '&' : '?'}v=${atlas.version || 1}`;
    });
  }

  async function selectBestAtlas() {
    const candidates = candidateList();
    for (const candidate of candidates) {
      const ok = await probeImage(candidate.url);
      state.checked.push({ ...candidate, ok });
      if (ok) {
        state.selected = candidate.url;
        state.format = candidate.format;
        atlas.tiles.imageActive = candidate.url;
        atlas.tiles.activeFormat = candidate.format;
        publish();
        renderDebugPanel();
        return candidate;
      }
    }
    state.error = 'No atlas image could be loaded.';
    atlas.tiles.imageActive = atlas.tiles.image;
    atlas.tiles.activeFormat = 'svg';
    publish();
    renderDebugPanel();
    return null;
  }

  function publish() {
    window.dispatchEvent(new CustomEvent('ydh-atlas-ready', {
      detail: {
        image: atlas.tiles.imageActive || atlas.tiles.image,
        format: atlas.tiles.activeFormat || 'svg',
        checked: [...state.checked],
        error: state.error
      }
    }));
  }

  function injectStyles() {
    if (document.getElementById('atlasLoaderStyles')) return;
    const style = document.createElement('style');
    style.id = 'atlasLoaderStyles';
    style.textContent = `
      .atlas-debug-panel{margin:14px 0 22px;padding:18px;border-radius:20px;background:rgba(0,0,0,.2);border:1px solid rgba(105,221,160,.22)}
      .atlas-debug-head{display:flex;justify-content:space-between;gap:14px;align-items:flex-start;margin-bottom:12px}.atlas-debug-head h3{margin:0 0 6px;color:#eef4ff}.atlas-debug-head p{margin:0;color:var(--muted);line-height:1.55}.atlas-debug-badge{padding:9px 12px;border-radius:999px;background:rgba(105,221,160,.12);color:#69dda0;font-weight:900;white-space:nowrap}.atlas-debug-list{display:grid;gap:6px;color:var(--muted);font-size:.82rem;line-height:1.55}.atlas-debug-list span{display:inline-flex;gap:8px;align-items:center}.atlas-debug-error{color:#ff7b7b}@media(max-width:820px){.atlas-debug-head{flex-direction:column}.atlas-debug-badge{width:100%;box-sizing:border-box}}
    `;
    document.head.appendChild(style);
  }

  function renderDebugPanel() {
    if (!document.body) return;
    injectStyles();
    let panel = document.getElementById('atlasDebugPanel');
    if (!panel) {
      panel = document.createElement('section');
      panel.id = 'atlasDebugPanel';
      panel.className = 'atlas-debug-panel';
      const assets = document.getElementById('assets');
      if (assets?.parentNode) assets.parentNode.insertBefore(panel, assets.nextSibling);
      else document.querySelector('main')?.appendChild(panel);
    }

    const checked = state.checked.length
      ? state.checked.map((item) => `<span>${item.ok ? '✅' : '⬜'} ${escapeHtml(item.format)} · ${escapeHtml(item.url)}</span>`).join('')
      : '<span>검사 대기 중</span>';

    panel.innerHTML = `
      <div class="atlas-debug-head">
        <div>
          <p class="eyebrow">ATLAS DEBUG</p>
          <h3>타일 atlas 로더</h3>
          <p>WebP → PNG → SVG 순서로 사용 가능한 atlas를 선택합니다.</p>
        </div>
        <div class="atlas-debug-badge">${escapeHtml((atlas.tiles.activeFormat || 'svg').toUpperCase())}</div>
      </div>
      <div class="atlas-debug-list ${state.error ? 'atlas-debug-error' : ''}">
        <span>active: ${escapeHtml(atlas.tiles.imageActive || atlas.tiles.image)}</span>
        ${checked}
        ${state.error ? `<span>${escapeHtml(state.error)}</span>` : ''}
      </div>
    `;
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
    renderDebugPanel();
    selectBestAtlas();
    window.YDH_ATLAS_LOADER = { state, selectBestAtlas, renderDebugPanel };
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
