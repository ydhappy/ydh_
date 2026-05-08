(() => {
  'use strict';

  const MAP_SAVE_KEY = 'ydh-chronicle-map-v1';
  const data = window.YDH_MAPS;
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
      if (saved && data.maps[saved.mapIndex]) return saved;
    } catch (error) {
      console.warn('Map save load failed:', error);
    }
    const start = data.maps[0].start;
    return { mapIndex: 0, x: start.x, y: start.y, steps: 0 };
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

  function render() {
    const map = currentMap();
    refs.title.textContent = map.name;
    refs.desc.textContent = map.description;
    refs.pos.textContent = `X:${state.x} / Y:${state.y} / STEP:${state.steps}`;
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
        if (tile.marker) cell.dataset.marker = tile.marker;
        if (state.x === x && state.y === y) cell.dataset.player = '🧝';
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
    const dx = Math.abs(x - state.x);
    const dy = Math.abs(y - state.y);
    if (dx + dy !== 1) {
      setEvent('인접한 타일로만 이동할 수 있습니다.', 'warn');
      return;
    }
    move(x - state.x, y - state.y);
  }

  function move(dx, dy) {
    const nx = state.x + dx;
    const ny = state.y + dy;
    const tile = getTile(nx, ny);
    if (!tile) return setEvent('맵 바깥으로 이동할 수 없습니다.', 'warn');
    if (!tile.passable) return setEvent(`${tile.name} 타일은 이동 불가입니다.`, 'warn');

    state.x = nx;
    state.y = ny;
    state.steps += 1;
    handleTile(tile);
    render();
  }

  function handleTile(tile) {
    if (tile.code === 'P') {
      const map = currentMap();
      state.mapIndex = map.portalTo ?? ((state.mapIndex + 1) % data.maps.length);
      const next = currentMap();
      state.x = next.start.x;
      state.y = next.start.y;
      setEvent(`${next.name}(으)로 이동했습니다.`, 'good');
      notifyGame(`포탈 이동: ${next.name}`);
      return;
    }

    if (tile.code === 'N') {
      setEvent('NPC: 사냥터는 위험합니다. 물약과 마나를 관리하세요.', 'good');
      notifyGame('NPC 안내를 받았습니다. 퀘스트 동선이 갱신되었습니다.');
      return;
    }

    const chance = tile.encounter || 0;
    if (Math.random() < chance) {
      setEvent(`${tile.name}에서 몬스터와 조우했습니다. 전투 화면에서 공격하세요.`, 'danger');
      notifyGame('맵 조우 이벤트 발생! 기본 공격 또는 스킬을 사용하세요.');
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
  setEvent('방향키 또는 WASD로 맵을 이동하세요.', 'good');
  render();
})();
