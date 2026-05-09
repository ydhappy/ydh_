(() => {
  'use strict';

  const resources = window.YDH_RESOURCES || { groups: {}, qualityPresets: {}, preload: {} };
  const PREF_KEY = 'ydh-resource-quality-v1';
  const state = {
    quality: localStorage.getItem(PREF_KEY) || 'balanced',
    loaded: [],
    failed: [],
    total: 0,
    startedAt: Date.now()
  };

  function allImages() {
    const critical = resources.preload?.criticalImages || [];
    const opportunistic = resources.preload?.opportunisticImages || [];
    return Array.from(new Set([...critical, ...opportunistic]));
  }

  function applyQuality(mode) {
    const safe = resources.qualityPresets?.[mode] ? mode : 'balanced';
    state.quality = safe;
    localStorage.setItem(PREF_KEY, safe);
    document.documentElement.classList.remove('quality-low', 'quality-balanced', 'quality-high');
    document.documentElement.classList.add(`quality-${safe}`);
    document.documentElement.style.setProperty('--motion-scale', String(resources.qualityPresets?.[safe]?.animationScale || 1));
    window.dispatchEvent(new CustomEvent('ydh-resource-quality-changed', { detail: { quality: safe, preset: resources.qualityPresets?.[safe] } }));
    renderPanel();
  }

  function preloadImage(url) {
    return new Promise((resolve) => {
      const image = new Image();
      image.onload = () => resolve({ url, ok: true });
      image.onerror = () => resolve({ url, ok: false });
      image.src = `${url}${url.includes('?') ? '&' : '?'}rv=${resources.version || 1}`;
    });
  }

  async function preloadResources() {
    const images = allImages();
    state.total = images.length;
    renderPanel();
    for (const url of images) {
      const result = await preloadImage(url);
      if (result.ok) state.loaded.push(url);
      else state.failed.push(url);
      renderPanel();
    }
    window.dispatchEvent(new CustomEvent('ydh-resources-preloaded', {
      detail: { loaded: [...state.loaded], failed: [...state.failed], total: state.total }
    }));
  }

  function ensureHeroCrest() {
    const brand = document.querySelector('.brand');
    if (!brand || document.querySelector('.brand-crest-img')) return;
    const crest = document.createElement('img');
    crest.className = 'brand-crest-img';
    crest.src = 'assets/ui/ydh-crest.svg';
    crest.alt = '';
    crest.width = 28;
    crest.height = 28;
    crest.style.cssText = 'width:28px;height:28px;margin-left:-6px;filter:drop-shadow(0 6px 12px rgba(0,0,0,.35));';
    brand.appendChild(crest);
  }

  function enhancePanels() {
    document.querySelectorAll('.panel').forEach((panel) => {
      if (panel.dataset.polished) return;
      panel.dataset.polished = '1';
      panel.addEventListener('pointermove', (event) => {
        if (state.quality === 'low') return;
        const rect = panel.getBoundingClientRect();
        const x = ((event.clientX - rect.left) / rect.width) * 100;
        const y = ((event.clientY - rect.top) / rect.height) * 100;
        panel.style.setProperty('--hover-x', `${x}%`);
        panel.style.setProperty('--hover-y', `${y}%`);
      });
    });
  }

  function renderPanel() {
    if (!document.body) return;
    let panel = document.getElementById('resourceQualityPanel');
    if (!panel) {
      panel = document.createElement('aside');
      panel.id = 'resourceQualityPanel';
      panel.className = 'resource-quality-panel collapsed';
      document.body.appendChild(panel);
    }

    const percent = state.total ? Math.round((state.loaded.length + state.failed.length) / state.total * 100) : 0;
    const modes = Object.entries(resources.qualityPresets || {}).map(([key, preset]) => {
      return `<button type="button" data-quality="${escapeHtml(key)}" class="${state.quality === key ? 'active' : ''}">${escapeHtml(preset.label || key)}</button>`;
    }).join('');
    const list = [...state.loaded.slice(-4).map((url) => `<span><b>OK</b><em>${escapeHtml(shortName(url))}</em></span>`), ...state.failed.slice(-4).map((url) => `<span><b>FAIL</b><em>${escapeHtml(shortName(url))}</em></span>`)].join('') || '<span><b>WAIT</b><em>preload 대기</em></span>';

    panel.innerHTML = `
      <div class="resource-quality-head">
        <strong>리소스 품질</strong>
        <button type="button" id="resourceQualityToggle">${panel.classList.contains('collapsed') ? '열기' : '접기'}</button>
      </div>
      <div class="resource-quality-body">
        <div class="resource-quality-modes">${modes}</div>
        <div class="resource-quality-meter"><i style="width:${percent}%"></i></div>
        <small>preload ${state.loaded.length}/${state.total} · fail ${state.failed.length}</small>
        <div class="resource-quality-list">${list}</div>
      </div>
    `;

    panel.querySelector('#resourceQualityToggle')?.addEventListener('click', () => {
      panel.classList.toggle('collapsed');
      renderPanel();
    });
    panel.querySelectorAll('[data-quality]').forEach((button) => {
      button.addEventListener('click', () => applyQuality(button.dataset.quality));
    });
  }

  function shortName(url) {
    return String(url).split('/').slice(-2).join('/');
  }

  function escapeHtml(value) {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function boot() {
    applyQuality(state.quality);
    ensureHeroCrest();
    enhancePanels();
    renderPanel();
    preloadResources();
    window.addEventListener('ydh-map-rendered', enhancePanels);
    window.addEventListener('ydh-tiled-maps-loaded', enhancePanels);
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();

  window.YDH_UI_ENHANCEMENTS = { state, applyQuality, preloadResources, renderPanel };
})();
