(() => {
  'use strict';

  const SAMPLE_MAPS = [
    'data/tiled/moon-gate-sample.json'
  ];

  function log(message, type = 'info') {
    window.dispatchEvent(new CustomEvent('ydh-map-event', {
      detail: { message: `Tiled: ${message}`, type }
    }));
    if (type === 'error') console.warn(`[YDH Tiled] ${message}`);
  }

  async function loadOne(url) {
    const loader = window.YDH_TILED_MAP_LOADER;
    if (!loader || !window.YDH_MAPS?.maps) {
      throw new Error('YDH_TILED_MAP_LOADER or YDH_MAPS is not ready');
    }

    const map = await loader.loadFromUrl(url);
    const existed = window.YDH_MAPS.maps.some((item) => item.id === map.id);
    loader.appendToYdhMaps(map);
    return { map, existed };
  }

  async function loadAll() {
    const results = [];
    for (const url of SAMPLE_MAPS) {
      try {
        const result = await loadOne(url);
        results.push(result);
        log(result.existed ? `이미 등록됨: ${result.map.name}` : `맵 추가됨: ${result.map.name}`);
      } catch (error) {
        log(`${url} 로드 실패 - ${error.message}`, 'error');
      }
    }

    window.YDH_TILED_BOOTSTRAP = {
      loaded: results.map((item) => item.map),
      urls: [...SAMPLE_MAPS],
      loadedAt: new Date().toISOString()
    };

    window.dispatchEvent(new CustomEvent('ydh-tiled-maps-loaded', {
      detail: window.YDH_TILED_BOOTSTRAP
    }));
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', loadAll);
  } else {
    loadAll();
  }
})();
