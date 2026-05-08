(() => {
  'use strict';

  const mapsData = window.YDH_MAPS;
  const entities = window.YDH_ENTITIES;
  if (!mapsData || !entities) return;

  let opened = localStorage.getItem('ydh-gm-console-open') === '1';

  function $(id) { return document.getElementById(id); }

  function currentMapTitle() {
    return $('mapTitle')?.textContent?.trim() || '';
  }

  function currentMap() {
    const title = currentMapTitle();
    return mapsData.maps.find((map) => map.name === title) || mapsData.maps[0];
  }

  function parsePosition() {
    const text = $('mapPosition')?.textContent || '';
    const x = Number(text.match(/X:(\d+)/)?.[1] || 0);
    const y = Number(text.match(/Y:(\d+)/)?.[1] || 0);
    const dir = Number(text.match(/DIR:(\d+)/)?.[1] || 0);
    const step = Number(text.match(/STEP:(\d+)/)?.[1] || 0);
    return { x, y, dir, step };
  }

  function tileAt(map, x, y) {
    const code = map?.rows?.[y]?.[x] || '?';
    const type = mapsData.tileTypes[code] || { name: '알 수 없음', passable: false, encounter: 0 };
    return { code, ...type };
  }

  function spawnTable(mapId) {
    return entities.spawnTables?.[mapId] || { monsters: [], npcs: [] };
  }

  function entityNames(ids, source) {
    return (ids || []).map((id) => source?.[id]?.name || id);
  }

  function countTiles(map) {
    const counts = {};
    (map.rows || []).forEach((row) => [...row].forEach((code) => { counts[code] = (counts[code] || 0) + 1; }));
    return counts;
  }

  function create() {
    const toggle = document.createElement('button');
    toggle.className = 'gm-console-toggle';
    toggle.type = 'button';
    toggle.id = 'gmConsoleToggle';
    toggle.textContent = 'GM MAP';
    document.body.appendChild(toggle);

    const panel = document.createElement('aside');
    panel.className = `gm-console${opened ? ' open' : ''}`;
    panel.id = 'gmConsole';
    panel.setAttribute('aria-label', 'GM 맵 스폰 관리 콘솔');
    document.body.appendChild(panel);

    toggle.addEventListener('click', () => {
      opened = !opened;
      localStorage.setItem('ydh-gm-console-open', opened ? '1' : '0');
      panel.classList.toggle('open', opened);
      render();
    });

    render();
    setInterval(render, 900);
    window.addEventListener('ydh-player-moved', render);
    window.addEventListener('ydh-map-event', render);
  }

  function render() {
    const panel = $('gmConsole');
    if (!panel) return;
    const map = currentMap();
    const pos = parsePosition();
    const tile = tileAt(map, pos.x, pos.y);
    const table = spawnTable(map.id);
    const monsters = entityNames(table.monsters, entities.monsters);
    const npcs = entityNames(table.npcs, entities.npcs);
    const counts = countTiles(map);
    const passable = Object.entries(counts).reduce((sum, [code, count]) => sum + ((mapsData.tileTypes[code]?.passable ? count : 0)), 0);
    const blocked = Object.entries(counts).reduce((sum, [code, count]) => sum + ((mapsData.tileTypes[code]?.passable ? 0 : count)), 0);

    panel.innerHTML = `
      <h2>GM 맵/스폰 콘솔</h2>
      <p>현재 플레이 화면의 맵 상태를 읽는 개발/운영 확인 패널입니다.</p>
      <div class="gm-console-grid">
        <div class="gm-console-card"><small>현재 맵</small><strong>${map.name}</strong></div>
        <div class="gm-console-card"><small>맵 ID</small><strong>${map.id}</strong></div>
        <div class="gm-console-card"><small>좌표</small><strong>X:${pos.x} / Y:${pos.y}</strong></div>
        <div class="gm-console-card"><small>방향/스텝</small><strong>DIR:${pos.dir} / STEP:${pos.step}</strong></div>
        <div class="gm-console-card"><small>현재 타일</small><strong>${tile.code} · ${tile.name}</strong></div>
        <div class="gm-console-card"><small>이동/조우</small><strong>${tile.passable ? '<span class="good">PASS</span>' : '<span class="danger">BLOCK</span>'} · ${Math.round((tile.encounter || 0) * 100)}%</strong></div>
      </div>
      <div class="gm-console-section">
        <h3>스폰 후보</h3>
        <div class="gm-console-list">
          ${monsters.map((name) => `<span><b>몬스터</b>${name}</span>`).join('') || '<span><b>몬스터</b>없음</span>'}
          ${npcs.map((name) => `<span><b>NPC</b>${name}</span>`).join('') || '<span><b>NPC</b>없음</span>'}
        </div>
      </div>
      <div class="gm-console-section">
        <h3>타일 통계</h3>
        <div class="gm-console-list">
          <span><b>전체 크기</b>${map.rows?.[0]?.length || 0} x ${map.rows?.length || 0}</span>
          <span><b>이동 가능</b>${passable}</span>
          <span><b>이동 불가</b>${blocked}</span>
          ${Object.entries(counts).map(([code, count]) => `<span><b>${code} · ${mapsData.tileTypes[code]?.name || '?'}</b>${count}</span>`).join('')}
        </div>
      </div>
      <div class="gm-console-actions">
        <button type="button" id="gmCopyMapInfo">맵 정보 복사</button>
        <button type="button" id="gmHighlightSpawns">스폰 타일 강조</button>
        <button type="button" id="gmClearHighlight">강조 해제</button>
      </div>
    `;

    $('gmCopyMapInfo')?.addEventListener('click', () => copyMapInfo(map, pos, tile, monsters, npcs));
    $('gmHighlightSpawns')?.addEventListener('click', highlightSpawns);
    $('gmClearHighlight')?.addEventListener('click', clearHighlight);
  }

  function copyMapInfo(map, pos, tile, monsters, npcs) {
    const text = [
      `map=${map.name} (${map.id})`,
      `position=X:${pos.x},Y:${pos.y},DIR:${pos.dir},STEP:${pos.step}`,
      `tile=${tile.code}/${tile.name}/passable=${tile.passable}/encounter=${tile.encounter || 0}`,
      `monsters=${monsters.join(', ')}`,
      `npcs=${npcs.join(', ')}`
    ].join('\n');
    navigator.clipboard?.writeText(text);
    window.dispatchEvent(new CustomEvent('ydh-map-event', { detail: { message: 'GM 콘솔: 맵 정보 복사 완료' } }));
  }

  function highlightSpawns() {
    document.querySelectorAll('.map-tile.tile-M,.map-tile.tile-N,.map-tile.tile-P').forEach((tile) => {
      tile.style.outline = '3px solid rgba(247,200,95,.92)';
      tile.style.zIndex = '8';
    });
  }

  function clearHighlight() {
    document.querySelectorAll('.map-tile').forEach((tile) => {
      tile.style.outline = '';
      tile.style.zIndex = '';
    });
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', create);
  else create();
})();
