(() => {
  'use strict';

  const canvas = document.getElementById('world');
  const ctx = canvas.getContext('2d');
  const mini = document.getElementById('minimap');
  const mctx = mini.getContext('2d');
  const saveKey = 'ydh-mobile-mmorpg-v1';
  const tile = 48;

  const ui = {
    hpBar: document.getElementById('hpBar'), mpBar: document.getElementById('mpBar'), xpBar: document.getElementById('xpBar'),
    hpText: document.getElementById('hpText'), mpText: document.getElementById('mpText'), xpText: document.getElementById('xpText'),
    classMark: document.getElementById('classMark'), charName: document.getElementById('charName'), zoneName: document.getElementById('zoneName'),
    questText: document.getElementById('questText'), chatLog: document.getElementById('chatLog'), inventoryList: document.getElementById('inventoryList'),
    menuModal: document.getElementById('menuModal'), menuBtn: document.getElementById('menuBtn'), closeMenu: document.getElementById('closeMenu'), resetBtn: document.getElementById('resetBtn'),
    joy: document.getElementById('joy'), stick: document.getElementById('stick'), attackBtn: document.getElementById('attackBtn')
  };

  const classes = {
    knight: { label: '검은 기사', mark: 'K', hp: 140, mp: 50, atk: 18, def: 10, color: '#f7c85f', range: 58 },
    wizard: { label: '마법사', mark: 'W', hp: 92, mp: 140, atk: 26, def: 4, color: '#73a7ff', range: 150 },
    ranger: { label: '요정 궁수', mark: 'E', hp: 108, mp: 88, atk: 22, def: 6, color: '#69dda0', range: 170 },
    rogue: { label: '다크 로그', mark: 'R', hp: 104, mp: 72, atk: 24, def: 5, color: '#ad82ff', range: 54 }
  };

  const map = [
    '################################',
    '#....f....f..............f.....#',
    '#..#####..........~~~~.........#',
    '#..#...#....m.....~~~~....m....#',
    '#..#...#..........~~~~.........#',
    '#..##.##....n..............f...#',
    '#...............######.........#',
    '#..m.....f......#....#.........#',
    '#...............#....#....n....#',
    '#.....~~~~......##..##.........#',
    '#.....~~~~.....................#',
    '#...........m...........f......#',
    '#..n.............~~~~..........#',
    '#........f.......~~~~....m.....#',
    '#..............p...............#',
    '#..######......................#',
    '#..#....#.........m............#',
    '#..#....#..................f...#',
    '#..######....n.................#',
    '#.................~~~~.........#',
    '#......m..........~~~~.........#',
    '#...........f............m.....#',
    '#..............................#',
    '################################'
  ];
  const mapH = map.length;
  const mapW = map[0].length;

  const spawnPoints = [];
  const npcPoints = [];
  const portalPoints = [];
  const forestPoints = [];
  for (let y = 0; y < mapH; y++) for (let x = 0; x < mapW; x++) {
    const c = map[y][x];
    if (c === 'm') spawnPoints.push({ x: x * tile + tile / 2, y: y * tile + tile / 2 });
    if (c === 'n') npcPoints.push({ x: x * tile + tile / 2, y: y * tile + tile / 2 });
    if (c === 'p') portalPoints.push({ x: x * tile + tile / 2, y: y * tile + tile / 2 });
    if (c === 'f') forestPoints.push({ x: x * tile + tile / 2, y: y * tile + tile / 2 });
  }

  const baseState = () => {
    const c = classes.knight;
    return { classId: 'knight', name: c.label, lv: 1, xp: 0, gold: 0, hp: c.hp, mp: c.mp, maxHp: c.hp, maxMp: c.mp, atk: c.atk, def: c.def, x: tile * 14, y: tile * 14, dirX: 0, dirY: 1, quest: { wolf: 0, need: 3 }, inventory: ['초보자 검', '회복 물약'], lastSave: Date.now() };
  };

  const state = Object.assign(baseState(), JSON.parse(localStorage.getItem(saveKey) || '{}'));
  const camera = { x: 0, y: 0 };
  const input = { x: 0, y: 0, active: false };
  const effects = [];
  const npcs = npcPoints.map((p, i) => ({ ...p, id: i, name: ['경비병', '대장장이', '사제', '전령'][i % 4], talk: ['마을 밖 늑대가 늘었습니다.', '장비는 곧 강화됩니다.', '빛이 당신을 지킵니다.', '왕실 의뢰가 도착했습니다.'][i % 4] }));
  const monsters = spawnPoints.map((p, i) => makeMonster(i, p.x, p.y));
  let last = performance.now();

  function makeMonster(i, x, y) {
    const types = [
      { name: '그림자 늑대', hp: 70, atk: 9, color: '#7d6bff', kind: 'wolf' },
      { name: '해골 전사', hp: 92, atk: 12, color: '#d7dce8', kind: 'skeleton' },
      { name: '오크 광전사', hp: 132, atk: 18, color: '#69dda0', kind: 'orc' },
      { name: '언데드 마법사', hp: 86, atk: 20, color: '#ad82ff', kind: 'mage' }
    ];
    const t = types[i % types.length];
    return { id: i, ...t, x, y, homeX: x, homeY: y, hp: t.hp, maxHp: t.hp, alive: true, respawn: 0, hitCd: 0, wander: Math.random() * 10 };
  }

  function save() {
    state.lastSave = Date.now();
    localStorage.setItem(saveKey, JSON.stringify(state));
  }

  function resize() {
    const dpr = Math.max(1, Math.min(2, window.devicePixelRatio || 1));
    canvas.width = Math.floor(innerWidth * dpr);
    canvas.height = Math.floor(innerHeight * dpr);
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  }

  function blocked(px, py) {
    const x = Math.floor(px / tile), y = Math.floor(py / tile);
    const c = map[y]?.[x];
    return !c || c === '#' || c === '~';
  }

  function dist(a, b) { return Math.hypot(a.x - b.x, a.y - b.y); }

  function log(text) {
    const p = document.createElement('p');
    p.textContent = text;
    ui.chatLog.appendChild(p);
    while (ui.chatLog.children.length > 8) ui.chatLog.removeChild(ui.chatLog.firstChild);
  }

  function setClass(classId) {
    const c = classes[classId] || classes.knight;
    state.classId = classId;
    state.name = c.label;
    state.maxHp = c.hp + (state.lv - 1) * 10;
    state.maxMp = c.mp + (state.lv - 1) * 6;
    state.atk = c.atk + (state.lv - 1) * 3;
    state.def = c.def + (state.lv - 1);
    state.hp = Math.min(state.maxHp, state.hp || state.maxHp);
    state.mp = Math.min(state.maxMp, state.mp || state.maxMp);
    log(`${c.label} 클래스로 전환`);
    save();
    updateUi();
  }

  function updateUi() {
    const c = classes[state.classId] || classes.knight;
    ui.charName.textContent = `${state.name} Lv.${state.lv}`;
    ui.classMark.textContent = c.mark;
    ui.hpBar.style.width = `${Math.max(0, state.hp / state.maxHp * 100)}%`;
    ui.mpBar.style.width = `${Math.max(0, state.mp / state.maxMp * 100)}%`;
    ui.xpBar.style.width = `${Math.max(0, state.xp)}%`;
    ui.hpText.textContent = `${Math.round(state.hp)}/${state.maxHp}`;
    ui.mpText.textContent = `${Math.round(state.mp)}/${state.maxMp}`;
    ui.xpText.textContent = `${Math.round(state.xp)}%`;
    ui.questText.textContent = `늑대 3마리 처치 ${state.quest.wolf}/${state.quest.need}`;
    ui.inventoryList.innerHTML = state.inventory.map((item) => `<li>${escapeHtml(item)}</li>`).join('');
  }

  function attack(skill = 'basic') {
    const c = classes[state.classId] || classes.knight;
    const costs = { basic: 0, powerSlash: 8, fireBurst: 18, healLight: 20, guardAura: 14 };
    const cost = costs[skill] || 0;
    if (state.mp < cost) { log('MP가 부족합니다.'); return; }
    if (skill === 'healLight') {
      state.mp -= cost;
      state.hp = Math.min(state.maxHp, state.hp + 46 + state.lv * 4);
      effects.push({ x: state.x, y: state.y, t: 0, color: '#69dda0', label: 'HEAL' });
      log('성역의 빛으로 회복했습니다.');
      updateUi(); save(); return;
    }
    if (skill === 'guardAura') {
      state.mp -= cost;
      state.def += 1;
      effects.push({ x: state.x, y: state.y, t: 0, color: '#f7c85f', label: 'GUARD' });
      log('황금 수호: 방어력 +1');
      updateUi(); save(); return;
    }
    const target = monsters.filter((m) => m.alive && dist(state, m) <= c.range + 26).sort((a, b) => dist(state, a) - dist(state, b))[0];
    if (!target) { log('사거리 안에 몬스터가 없습니다.'); return; }
    state.mp -= cost;
    const bonus = skill === 'fireBurst' ? 24 : skill === 'powerSlash' ? 14 : 0;
    const dmg = Math.max(1, state.atk + bonus + Math.floor(Math.random() * 9));
    target.hp -= dmg;
    target.hitCd = 0.2;
    effects.push({ x: target.x, y: target.y, t: 0, color: skill === 'fireBurst' ? '#ff6b6b' : '#f7c85f', label: String(dmg) });
    if (target.hp <= 0) killMonster(target);
    updateUi(); save();
  }

  function killMonster(m) {
    m.alive = false;
    m.respawn = 5 + Math.random() * 4;
    state.gold += 8 + Math.floor(Math.random() * 18);
    state.xp += 18;
    if (m.kind === 'wolf') state.quest.wolf = Math.min(state.quest.need, state.quest.wolf + 1);
    if (Math.random() < 0.18) state.inventory.push([`${m.name}의 전리품`, '회복 물약', '마나 주문서', '희미한 보석'][Math.floor(Math.random() * 4)]);
    log(`${m.name} 처치`);
    if (state.xp >= 100) {
      state.xp -= 100;
      state.lv++;
      const c = classes[state.classId] || classes.knight;
      state.maxHp = c.hp + (state.lv - 1) * 10;
      state.maxMp = c.mp + (state.lv - 1) * 6;
      state.atk = c.atk + (state.lv - 1) * 3;
      state.def = c.def + (state.lv - 1);
      state.hp = state.maxHp;
      state.mp = state.maxMp;
      log(`레벨업! Lv.${state.lv}`);
    }
    if (state.quest.wolf >= state.quest.need) log('퀘스트 완료: 전초기지 경비병에게 보고하세요.');
  }

  function interact() {
    const npc = npcs.find((n) => dist(state, n) < 70);
    if (npc) {
      log(`${npc.name}: ${npc.talk}`);
      if (state.quest.wolf >= state.quest.need && !state.quest.done) {
        state.quest.done = true;
        state.gold += 120;
        state.inventory.push('수습 모험가 훈장');
        log('퀘스트 보상: 120골드, 수습 모험가 훈장');
      }
      updateUi(); save(); return true;
    }
    const portal = portalPoints.find((p) => Math.hypot(state.x - p.x, state.y - p.y) < 70);
    if (portal) {
      state.x = tile * 4; state.y = tile * 4;
      log('포탈 이동: 전초기지 입구'); save(); return true;
    }
    return false;
  }

  function update(dt) {
    const speed = 150;
    if (input.x || input.y) {
      const len = Math.hypot(input.x, input.y) || 1;
      const dx = input.x / len, dy = input.y / len;
      const nx = state.x + dx * speed * dt;
      const ny = state.y + dy * speed * dt;
      if (!blocked(nx, state.y)) state.x = nx;
      if (!blocked(state.x, ny)) state.y = ny;
      state.dirX = dx; state.dirY = dy;
    }
    for (const m of monsters) {
      if (!m.alive) {
        m.respawn -= dt;
        if (m.respawn <= 0) { m.alive = true; m.hp = m.maxHp; m.x = m.homeX; m.y = m.homeY; }
        continue;
      }
      m.hitCd = Math.max(0, m.hitCd - dt);
      const d = dist(state, m);
      if (d < 280) {
        const dx = (state.x - m.x) / d, dy = (state.y - m.y) / d;
        if (d > 42) { m.x += dx * 74 * dt; m.y += dy * 74 * dt; }
        else if (Math.random() < dt * 1.2) {
          const dmg = Math.max(1, m.atk - state.def + Math.floor(Math.random() * 5));
          state.hp -= dmg;
          effects.push({ x: state.x, y: state.y, t: 0, color: '#ff6b6b', label: String(dmg) });
          if (state.hp <= 0) { state.hp = state.maxHp; state.mp = state.maxMp; state.x = tile * 14; state.y = tile * 14; log('전투불능: 전초기지에서 부활'); }
          updateUi(); save();
        }
      } else {
        m.wander += dt;
        m.x += Math.cos(m.wander + m.id) * 8 * dt;
        m.y += Math.sin(m.wander * 0.8 + m.id) * 8 * dt;
      }
    }
    for (const e of effects) e.t += dt;
    for (let i = effects.length - 1; i >= 0; i--) if (effects[i].t > 0.8) effects.splice(i, 1);
    camera.x = state.x - innerWidth / 2;
    camera.y = state.y - innerHeight / 2;
    camera.x = Math.max(0, Math.min(camera.x, mapW * tile - innerWidth));
    camera.y = Math.max(0, Math.min(camera.y, mapH * tile - innerHeight));
  }

  function drawTile(x, y, c) {
    const sx = x * tile - camera.x, sy = y * tile - camera.y;
    if (sx < -tile || sy < -tile || sx > innerWidth + tile || sy > innerHeight + tile) return;
    ctx.fillStyle = c === '#' ? '#303746' : c === '~' ? '#17345b' : c === 'f' ? '#1d4c32' : c === 'p' ? '#33205c' : '#243b2b';
    ctx.fillRect(sx, sy, tile, tile);
    ctx.strokeStyle = 'rgba(255,255,255,.035)'; ctx.strokeRect(sx, sy, tile, tile);
    if (c === 'f') { ctx.fillStyle = '#69dda0'; ctx.beginPath(); ctx.arc(sx + 24, sy + 24, 12, 0, Math.PI * 2); ctx.fill(); }
    if (c === 'p') { ctx.strokeStyle = '#ad82ff'; ctx.lineWidth = 4; ctx.beginPath(); ctx.arc(sx + 24, sy + 24, 16 + Math.sin(performance.now() / 220) * 3, 0, Math.PI * 2); ctx.stroke(); }
  }

  function drawUnit(x, y, color, label, radius = 18) {
    const sx = x - camera.x, sy = y - camera.y;
    ctx.fillStyle = 'rgba(0,0,0,.28)'; ctx.beginPath(); ctx.ellipse(sx, sy + radius, radius * 1.1, radius * 0.34, 0, 0, Math.PI * 2); ctx.fill();
    ctx.fillStyle = color; ctx.beginPath(); ctx.arc(sx, sy, radius, 0, Math.PI * 2); ctx.fill();
    ctx.fillStyle = '#07101a'; ctx.font = 'bold 13px system-ui'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle'; ctx.fillText(label.slice(0, 1), sx, sy);
  }

  function draw() {
    ctx.clearRect(0, 0, innerWidth, innerHeight);
    for (let y = 0; y < mapH; y++) for (let x = 0; x < mapW; x++) drawTile(x, y, map[y][x]);
    for (const n of npcs) drawUnit(n.x, n.y, '#f7c85f', n.name, 16);
    for (const m of monsters) if (m.alive) {
      drawUnit(m.x, m.y, m.hitCd ? '#ffffff' : m.color, m.name, m.kind === 'orc' ? 23 : 18);
      const sx = m.x - camera.x - 22, sy = m.y - camera.y - 32;
      ctx.fillStyle = 'rgba(0,0,0,.5)'; ctx.fillRect(sx, sy, 44, 5);
      ctx.fillStyle = '#ff6b6b'; ctx.fillRect(sx, sy, 44 * Math.max(0, m.hp / m.maxHp), 5);
    }
    const pc = classes[state.classId] || classes.knight;
    drawUnit(state.x, state.y, pc.color, pc.mark, 21);
    for (const e of effects) {
      const sx = e.x - camera.x, sy = e.y - camera.y - e.t * 48;
      ctx.globalAlpha = 1 - e.t / 0.8;
      ctx.fillStyle = e.color; ctx.font = 'bold 20px system-ui'; ctx.textAlign = 'center'; ctx.fillText(e.label, sx, sy);
      ctx.globalAlpha = 1;
    }
    drawMini();
  }

  function drawMini() {
    mctx.clearRect(0, 0, 132, 132);
    const sx = 132 / mapW, sy = 132 / mapH;
    for (let y = 0; y < mapH; y++) for (let x = 0; x < mapW; x++) {
      const c = map[y][x];
      mctx.fillStyle = c === '#' ? '#303746' : c === '~' ? '#17345b' : c === 'p' ? '#ad82ff' : '#24452d';
      mctx.fillRect(x * sx, y * sy, Math.ceil(sx), Math.ceil(sy));
    }
    mctx.fillStyle = '#f7c85f'; mctx.beginPath(); mctx.arc(state.x / tile * sx, state.y / tile * sy, 3, 0, Math.PI * 2); mctx.fill();
  }

  function loop(now) {
    const dt = Math.min(0.05, (now - last) / 1000); last = now;
    update(dt); draw(); requestAnimationFrame(loop);
  }

  function joyPoint(ev) {
    const r = ui.joy.getBoundingClientRect();
    const p = ev.touches ? ev.touches[0] : ev;
    const cx = r.left + r.width / 2, cy = r.top + r.height / 2;
    const dx = p.clientX - cx, dy = p.clientY - cy;
    const max = r.width * 0.33;
    const len = Math.min(max, Math.hypot(dx, dy));
    const a = Math.atan2(dy, dx);
    input.x = Math.cos(a) * (len / max);
    input.y = Math.sin(a) * (len / max);
    ui.stick.style.left = `${r.width / 2 - ui.stick.offsetWidth / 2 + Math.cos(a) * len}px`;
    ui.stick.style.top = `${r.height / 2 - ui.stick.offsetHeight / 2 + Math.sin(a) * len}px`;
  }
  function joyEnd() { input.x = 0; input.y = 0; ui.stick.style.left = ''; ui.stick.style.top = ''; }

  ui.joy.addEventListener('touchstart', joyPoint, { passive: false }); ui.joy.addEventListener('touchmove', joyPoint, { passive: false }); ui.joy.addEventListener('touchend', joyEnd);
  ui.joy.addEventListener('pointerdown', (e) => { ui.joy.setPointerCapture(e.pointerId); joyPoint(e); }); ui.joy.addEventListener('pointermove', joyPoint); ui.joy.addEventListener('pointerup', joyEnd);
  ui.attackBtn.addEventListener('click', () => { if (!interact()) attack('basic'); });
  document.querySelectorAll('[data-skill]').forEach((b) => b.addEventListener('click', () => attack(b.dataset.skill)));
  ui.menuBtn.addEventListener('click', () => ui.menuModal.classList.remove('hidden'));
  ui.closeMenu.addEventListener('click', () => ui.menuModal.classList.add('hidden'));
  ui.resetBtn.addEventListener('click', () => { localStorage.removeItem(saveKey); location.reload(); });
  document.querySelectorAll('[data-class]').forEach((b) => b.addEventListener('click', () => setClass(b.dataset.class)));
  addEventListener('keydown', (e) => { const k = e.key.toLowerCase(); input.x = (k === 'a' || k === 'arrowleft') ? -1 : (k === 'd' || k === 'arrowright') ? 1 : input.x; input.y = (k === 'w' || k === 'arrowup') ? -1 : (k === 's' || k === 'arrowdown') ? 1 : input.y; if (k === ' ') attack('basic'); });
  addEventListener('keyup', () => { input.x = 0; input.y = 0; });
  addEventListener('resize', resize);

  function escapeHtml(v) { return String(v).replace(/[&<>"']/g, (m) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[m])); }

  resize(); setClass(state.classId || 'knight'); updateUi(); log('YDH MMORPG 접속 완료'); requestAnimationFrame(loop);
})();
