window.YDH_TILED_MAP_LOADER = (() => {
  'use strict';

  function propValue(properties = [], name, fallback = undefined) {
    const found = properties.find((item) => item.name === name);
    return found ? found.value : fallback;
  }

  function buildGidCodeMap(tiledMap) {
    const gidToCode = new Map();
    (tiledMap.tilesets || []).forEach((tileset) => {
      const firstgid = tileset.firstgid || 1;
      (tileset.tiles || []).forEach((tile) => {
        const code = propValue(tile.properties || [], 'ydhCode', 'G');
        gidToCode.set(firstgid + tile.id, code);
      });
    });
    return gidToCode;
  }

  function findTileLayer(tiledMap, layerName = 'Ground') {
    const layers = tiledMap.layers || [];
    return layers.find((layer) => layer.type === 'tilelayer' && layer.name === layerName)
      || layers.find((layer) => layer.type === 'tilelayer');
  }

  function convertRows(tiledMap, tileLayer, gidToCode) {
    const width = tileLayer.width || tiledMap.width;
    const height = tileLayer.height || tiledMap.height;
    const data = tileLayer.data || [];
    const rows = [];

    for (let y = 0; y < height; y += 1) {
      let row = '';
      for (let x = 0; x < width; x += 1) {
        const gid = data[y * width + x] || 0;
        row += gidToCode.get(gid) || 'G';
      }
      rows.push(row);
    }
    return rows;
  }

  function convert(tiledMap, options = {}) {
    if (!tiledMap || tiledMap.type !== 'map') {
      throw new Error('Invalid Tiled map JSON: type must be map');
    }

    const layer = findTileLayer(tiledMap, options.layerName || 'Ground');
    if (!layer) throw new Error('Tiled map has no tilelayer');

    const gidToCode = buildGidCodeMap(tiledMap);
    const id = propValue(tiledMap.properties || [], 'ydhId', options.id || 'tiled-map');
    const name = propValue(tiledMap.properties || [], 'ydhName', options.name || id);
    const description = propValue(tiledMap.properties || [], 'ydhDescription', options.description || 'Tiled JSON imported map.');
    const startX = Number(propValue(tiledMap.properties || [], 'ydhStartX', options.startX ?? 1));
    const startY = Number(propValue(tiledMap.properties || [], 'ydhStartY', options.startY ?? 1));
    const portalTo = Number(propValue(tiledMap.properties || [], 'ydhPortalTo', options.portalTo ?? 0));

    return {
      id,
      name,
      description,
      start: { x: startX, y: startY },
      portalTo,
      source: 'tiled-json',
      rows: convertRows(tiledMap, layer, gidToCode)
    };
  }

  async function loadFromUrl(url, options = {}) {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`Tiled map fetch failed: HTTP ${response.status}`);
    const tiledMap = await response.json();
    return convert(tiledMap, options);
  }

  function appendToYdhMaps(map) {
    if (!window.YDH_MAPS?.maps) throw new Error('YDH_MAPS.maps not found');
    const exists = window.YDH_MAPS.maps.some((item) => item.id === map.id);
    if (!exists) window.YDH_MAPS.maps.push(map);
    return map;
  }

  return {
    convert,
    loadFromUrl,
    appendToYdhMaps,
    propValue,
    buildGidCodeMap
  };
})();
