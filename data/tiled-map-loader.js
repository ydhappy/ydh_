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

  function objectLayers(tiledMap) {
    return (tiledMap.layers || []).filter((layer) => layer.type === 'objectgroup');
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

  function objectKind(object) {
    return String(propValue(object.properties || [], 'ydhKind', object.type || object.name || 'marker')).toLowerCase();
  }

  function convertObjects(tiledMap) {
    const tilewidth = tiledMap.tilewidth || 64;
    const tileheight = tiledMap.tileheight || 64;
    const placements = [];

    objectLayers(tiledMap).forEach((layer) => {
      (layer.objects || []).forEach((object) => {
        const kind = objectKind(object);
        const x = Math.max(0, Math.floor((object.x || 0) / tilewidth));
        const rawY = object.gid ? (object.y || 0) - tileheight : (object.y || 0);
        const y = Math.max(0, Math.floor(rawY / tileheight));
        placements.push({
          id: String(propValue(object.properties || [], 'ydhId', object.name || `${kind}-${object.id}`)),
          name: String(propValue(object.properties || [], 'ydhName', object.name || kind)),
          kind,
          x,
          y,
          layer: layer.name,
          entityId: propValue(object.properties || [], 'ydhEntityId', ''),
          targetMapId: propValue(object.properties || [], 'ydhTargetMapId', ''),
          targetMapIndex: propValue(object.properties || [], 'ydhTargetMapIndex', ''),
          dialogue: propValue(object.properties || [], 'ydhDialogue', ''),
          raw: {
            id: object.id,
            name: object.name || '',
            type: object.type || '',
            x: object.x || 0,
            y: object.y || 0,
            width: object.width || 0,
            height: object.height || 0
          }
        });
      });
    });

    return placements;
  }

  function placementTileCode(kind) {
    const normalized = String(kind || '').toLowerCase();
    if (normalized === 'npc') return 'N';
    if (normalized === 'monster' || normalized === 'mob') return 'M';
    if (normalized === 'portal' || normalized === 'teleport') return 'P';
    return '';
  }

  function applyPlacementsToRows(rows, placements = []) {
    const matrix = rows.map((row) => [...row]);
    const applied = [];
    const skipped = [];

    placements.forEach((placement) => {
      const code = placementTileCode(placement.kind);
      if (!code) {
        skipped.push({ ...placement, reason: 'unsupported-kind' });
        return;
      }
      if (!matrix[placement.y] || matrix[placement.y][placement.x] === undefined) {
        skipped.push({ ...placement, reason: 'out-of-range' });
        return;
      }
      const before = matrix[placement.y][placement.x];
      matrix[placement.y][placement.x] = code;
      applied.push({ ...placement, code, before });
    });

    return {
      rows: matrix.map((row) => row.join('')),
      applied,
      skipped
    };
  }

  function placementSummary(placements = []) {
    return placements.reduce((summary, placement) => {
      summary[placement.kind] = (summary[placement.kind] || 0) + 1;
      return summary;
    }, {});
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
    const placements = convertObjects(tiledMap);
    const baseRows = convertRows(tiledMap, layer, gidToCode);
    const placementRows = applyPlacementsToRows(baseRows, placements);

    return {
      id,
      name,
      description,
      start: { x: startX, y: startY },
      portalTo,
      source: 'tiled-json',
      sourceUrl: options.sourceUrl || '',
      tiled: {
        version: tiledMap.version || tiledMap.tiledversion || 'unknown',
        tilewidth: tiledMap.tilewidth || 64,
        tileheight: tiledMap.tileheight || 64,
        objectLayers: objectLayers(tiledMap).map((objectLayer) => objectLayer.name),
        placementSummary: placementSummary(placements),
        appliedPlacements: placementRows.applied.length,
        skippedPlacements: placementRows.skipped.length
      },
      placements,
      appliedPlacements: placementRows.applied,
      skippedPlacements: placementRows.skipped,
      baseRows,
      rows: placementRows.rows
    };
  }

  async function loadFromUrl(url, options = {}) {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`Tiled map fetch failed: HTTP ${response.status}`);
    const tiledMap = await response.json();
    return convert(tiledMap, { ...options, sourceUrl: url });
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
    buildGidCodeMap,
    convertObjects,
    objectLayers,
    placementSummary,
    placementTileCode,
    applyPlacementsToRows
  };
})();
