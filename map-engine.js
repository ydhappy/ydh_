(() => {
  'use strict';

  const MAP_SAVE_KEY = 'ydh-chronicle-map-v1';
  const data = window.YDH_MAPS;
  const entities = window.YDH_ENTITIES || {};
  const pickEntityForMap = window.YDH_pickEntityForMap || (() => null);
  const getDirection16 = window.YDH_getDirection16 || ((dx, dy) => {
    if (dx === 0 && dy === 0) return 12;
    const angle = (Math.atan2(-dy, dx) * 180 / Math.PI + 360) % 360;
    return Math.round(angle / 22.5) % 16;
  });

  if (!data) {
    console.warn('YDH_MAPS data not found. Map engine skipped.');
    return;
  }

  const $ = (id) => document.getElementById(id);
  const refs = {
    grid: $('tileMap'),
    title: $('mapTitle'),
    desc: $('mapDesc'),
    pos: $('mapPosition'),
    event: $('mapEvent'),
    north: $('moveNorth'),
    south: $('moveSouth'),
    west: $('moveWest'),
    east: $('moveEast')
  };

  if (!refs.grid) return;

  let state = loadState();

  function loadState() {
    try {
      const saved = JSON.parse(localStorage.getItem(MAP_SAVE_KEY) || 'null');
      if (saved && data.maps[saved.mapIndex]) {
        return {
          direction: entities.player?.defaultDirection ?? 12,
          lastTarget: null,
          ...saved
        };
      }
    } catch (error) {
      console.warn('Map save load failed:', error);
    }
    const start = data.maps[0].start;
    return { mapIndex: 0, x: start.x, y: start.y, steps: 0, direction: entities.player?.defaultDirection ?? 12, lastTarget: null };
  }

  function saveState() {
    localStorage.setItem(MAP_SAVE_KEY, JSON.stringify(state));
  }

  function currentMap() {
    return data.maps[state.mapIndex % data.maps.length];
  }

  function getTile(x, y) {
    const map = currentMap();
    if (!map.rows[y] || x < 0 || x >= map.rows[y].length) return null;
    const code = map.rows[y][x];
    return { code, ...(data.tileTypes[code] || data.tileTypes.G) };
  }

  function getMapEntity(type, x, y) {
    const map = currentMap();
    const picked = pickEntityForMap(map.id, type, x, y);
    if (picked) return picked;
    return type === 'npc' ? entities.npc : entities.monster;
  }

  function spriteBackground(entity, direction) {
    const frameWidth = entity.frameWidth || 64;
    return {
      image: `url(${entity.sheet})`,
      position: `-${direction * frameWidth}px 0px`
    };
  }

  function appendEntitySprite(cell, entity, direction, label) {
    if (!entity?.sheet) return;
    cell.classList.add('has-entity', `entity-${entity.role || entity.id}`);
    const sprite = document.createElement('span');
    sprite.className = `entity-sprite ${entity.role || entity.id}`;
    const bg = spriteBackground(entity, direction);
    sprite.style.backgroundImage = bg.image;
    sprite.style.backgroundPosition = bg.position;
    sprite.title = `${entity.name} · ${direction}/15`;
    cell.appendChild(sprite);

    if (label) {
      const plate = document.createElement('span');
      plate.className = 'entity-nameplate';
      plate.textContent = label;
      cell.appendChild(plate);
    }
  }

  function nearestDirectionToPlayer(x, y) {
    return getDirection16(state.x - x, state.y - y);
  }

  function renderTileEntity(cell, code, x, y) {
    if (state.x === x && state.y === y) {
      appendEntitySprite(cell, entities.player, state.direction, entities.player?.name || 'Player');
      return;
    }

    if (code === 'M') {
      const monster = getMapEntity('monster', x, y);
      appendEntitySprite(cell, monster, nearestDirectionToPlayer(x, y), monster?.name || 'Monster');
      return;
    }

    if (code === 'N') {
      const npc = getMapEntity('npc', x, y);
      appendEntitySprite(cell, npc, nearestDirectionToPlayer(x, y), npc?.name || 'NPC');
      return;
    }

    if (code === 'P') {
      cell.classList.add('entity-portal');
    }
  }

  function render() {
    const map = currentMap();
    refs.title.textContent = map.name;
    refs.desc.textContent = map.description;
    refs.pos.textContent = `X:${state.x} / Y:${state.y} / DIR:${state.direction} / STEP:${state.steps}`;
    refs.grid.style.gridTemplateColumns = `repeat(${map.rows[0].length}, minmax(0, 1fr))`;
    refs.grid.innerHTML = '';

    map.rows.forEach((row, y) => {
      [...row].forEach((code, x) => {
        const tile = data.tileTypes[code] || data.tileTypes.G;
        const cell = document.createElement('button');
        cell.type = 'button';
        cell.className = `map-tile tile-${code}${tile.passable ? '' : ' blocked'}${state.x === x && state.y === y ? ' player-here' : ''}`;
        cell.style.backgroundImage = `url(${tile.asset})`;
        cell.title = `${tile.name} (${x}, ${y})`;
        cell.setAttribute('aria-label', `${tile.name} ${x}, ${y}`);
        renderTileEntity(cell, code, x, y);
        cell.addEventListener('click', () => tryMoveTo(x, y));
        refs.grid.appendChild(cell);
      });
    });
    saveState();
  }

  function setEvent(message, type = '') {
    refs.event.textContent = message;
    refs.event.className = `map-event ${type}`;
  }

  function tryMoveTo(x, y) {
    const dx = x - state.x;
    const dy = y - state.y;
    if (Math.abs(dx) + Math.abs(dy) !== 1) {
      setEvent('인접한 타일로만 이동할 수 있습니다.', 'warn');
      return;
    }
    move(dx, dy);
  }

  function move(dx, dy) {
    const nx = state.x + dx;
    const ny = state.y + dy;
    const tile = getTile(nx, ny);
    state.direction = getDirection16(dx, dy);

    if (!tile) {
      render();
      return setEvent('맵 바깥으로 이동할 수 없습니다.', 'warn');
    }
    if (!tile.passable) {
      render();
      return setEvent(`${tile.name} 타일은 이동 불가입니다.`, 'warn');
    }

    state.x = nx;
    state.y = ny;
    state.steps += 1;
    handleTile(tile, nx, ny);
    render();
  }

  function handleTile(tile, x, y) {
    if (tile.code === 'P') {
      const map = currentMap();
      state.mapIndex = map.portalTo ?? ((state.mapIndex + 1) % data.maps.length);
      if (!data.maps[state.mapIndex]) state.mapIndex = 0;
      const next = currentMap();
      state.x = next.start.x;
      state.y = next.start.y;
      state.direction = entities.player?.defaultDirection ?? 12;
      state.lastTarget = null;
      setEvent(`${next.name}(으)로 이동했습니다.`, 'good');
      notifyGame(`포탈 이동: ${next.name}`);
      return;
    }

    if (tile.code === 'N') {
      const npc = getMapEntity('npc', x, y);
      state.lastTarget = { type: 'npc', id: npc.id, name: npc.name };
      setEvent(`${npc.name}: ${npc.dialogue || '어서 오세요.'}`, 'good');
      notifyGame(`${npc.name} 대화: ${npc.dialogue || '어서 오세요.'}`);
      return;
    }

    const chance = tile.encounter || 0;
    if (Math.random() < chance) {
      const monster = getMapEntity('monster', x, y);
      state.lastTarget = { type: 'monster', id: monster.id, name: monster.name };
      setEvent(`${monster.name} 조우! 전투 화면에서 공격하세요.`, 'danger');
      notifyGame(`맵 조우 이벤트: ${monster.name} 출현`);
      const attackButton = document.getElementById('attackBtn');
      attackButton?.focus();
      return;
    }

    setEvent(`${tile.name} 타일로 이동했습니다.`, '');
  }

  function notifyGame(message) {
    window.dispatchEvent(new CustomEvent('ydh-map-event', { detail: { message } }));
  }

  function bind() {
    refs.north?.addEventListener('click', () => move(0, -1));
    refs.south?.addEventListener('click', () => move(0, 1));
    refs.west?.addEventListener('click', () => move(-1, 0));
    refs.east?.addEventListener('click', () => move(1, 0));

    document.addEventListener('keydown', (event) => {
      const tag = document.activeElement?.tagName?.toLowerCase();
      if (tag === 'input' || tag === 'textarea') return;
      const keyMap = {
        ArrowUp: [0, -1],
        ArrowDown: [0, 1],
        ArrowLeft: [-1, 0],
        ArrowRight: [1, 0],
        w: [0, -1],
        s: [0, 1],
        a: [-1, 0],
        d: [1, 0]
      };
      const dir = keyMap[event.key];
      if (!dir) return;
      event.preventDefault();
      move(dir[0], dir[1]);
    });
  }

  bind();
  setEvent('방향키, WASD, 터치 버튼으로 16방향 스프라이트를 확인하세요.', 'good');
  render();
})();
