(() => {
  'use strict';

  const SAVE_KEY = 'ydh-chronicle-save-v1';
  const lore = window.YDH_LORE_CONTENT || {};

  const baseZones = [
    { name: '말하는 섬 해안', monsters: [['그림자 늑대', '🐺', 55, 8], ['해골 정찰병', '💀', 62, 9]] },
    { name: '은빛 숲', monsters: [['고블린 약탈자', '👺', 74, 11], ['독버섯 정령', '🍄', 82, 12]] },
    { name: '버려진 광산', monsters: [['동굴 박쥐', '🦇', 92, 14], ['광산 골렘', '🪨', 120, 16]] },
    { name: '붉은 사막', monsters: [['모래 암살자', '🦂', 138, 19], ['불꽃 임프', '🔥', 148, 20]] },
    { name: '용의 계곡', monsters: [['와이번', '🐉', 190, 25], ['드레이크', '🐲', 230, 29]] },
    { name: '오만의 탑 입구', monsters: [['타락 기사', '🗡️', 280, 35], ['고대 리치', '🧙', 320, 38]] },
    { name: '혼돈의 성채', monsters: [['심연 파수꾼', '👁️', 390, 44], ['카오스 비스트', '👹', 430, 48]] },
    { name: '군주의 방', monsters: [['발록의 그림자', '👿', 560, 58], ['봉인된 마룡', '🐲', 720, 68]] }
  ];

  const gradeSprite = { normal: '🌘', elite: '🜏', boss: '🌑' };
  const loreMonsters = (lore.monsters || []).map((monster) => [
    monster.name,
    gradeSprite[monster.grade] || '🌘',
    monster.hp || 120,
    monster.atk || 18,
    monster.story || ''
  ]);

  const loreZones = (lore.maps || []).map((map, index) => {
    const start = index % Math.max(1, loreMonsters.length);
    const monsters = loreMonsters.length
      ? [loreMonsters[start], loreMonsters[(start + 1) % loreMonsters.length]]
      : [['달그림자 추적자', '🌘', 95, 14]];
    return { name: map.name, lore: map.story, monsters };
  });

  const zones = [...baseZones, ...loreZones];

  const baseSkills = [
    { id: 'power', name: '파워 스트라이크', icon: '⚔️', mp: 6, cd: 1500, scale: 1.65, text: 'MP 6 · 강타' },
    { id: 'fire', name: '파이어 볼트', icon: '🔥', mp: 10, cd: 2600, scale: 2.2, text: 'MP 10 · 화염' },
    { id: 'heal', name: '힐', icon: '✨', mp: 12, cd: 5000, heal: 38, text: 'MP 12 · 회복' },
    { id: 'shield', name: '아이언 스킨', icon: '🛡️', mp: 9, cd: 6500, buff: 3, text: 'MP 9 · 방어' },
    { id: 'storm', name: '라이트닝 스톰', icon: '⚡', mp: 18, cd: 9000, scale: 3.1, text: 'MP 18 · 필살' }
  ];

  const loreSkillLabel = { damage: '달마법', drain: '흡혈 표식', guard: '결계', ultimate: '심연기' };
  const loreSkills = (lore.skills || []).map((skill) => ({
    ...skill,
    lore: true,
    text: `MP ${skill.mp} · ${loreSkillLabel[skill.type] || '소설 스킬'}`
  }));

  const skills = [...baseSkills, ...loreSkills];

  const defaultState = () => ({
    level: 1,
    exp: 0,
    expToNext: 80,
    hp: 100,
    maxHp: 100,
    mp: 40,
    maxMp: 40,
    atk: 12,
    def: 4,
    gold: 0,
    wave: 1,
    zone: 0,
    kills: 0,
    questKills: 0,
    bestLevel: 1,
    bestWave: 1,
    totalKills: 0,
    inventory: ['낡은 검', '수련자 갑옷', '빨간 물약 x3'],
    cooldowns: {},
    shieldTurns: 0
  });

  let state = loadState();
  let monster = createMonster();
  let busy = false;

  const $ = (id) => document.getElementById(id);
  const refs = {
    level: $('level'), hpText: $('hpText'), hpBar: $('hpBar'), mpText: $('mpText'), mpBar: $('mpBar'),
    expText: $('expText'), expBar: $('expBar'), atkStat: $('atkStat'), defStat: $('defStat'), goldStat: $('goldStat'),
    waveStat: $('waveStat'), inventoryList: $('inventoryList'), zoneName: $('zoneName'), monsterSprite: $('monsterSprite'),
    monsterName: $('monsterName'), monsterTitle: $('monsterTitle'), monsterHpText: $('monsterHpText'), monsterHpBar: $('monsterHpBar'),
    skillGrid: $('skillGrid'), logList: $('logList'), attackBtn: $('attackBtn'), potionBtn: $('potionBtn'), restBtn: $('restBtn'),
    nextZoneBtn: $('nextZoneBtn'), impact: $('impact'), playerUnit: $('playerUnit'), monsterUnit: $('monsterUnit'),
    questText: $('questText'), questBar: $('questBar'), bestLevel: $('bestLevel'), bestWave: $('bestWave'), totalKills: $('totalKills'),
    resetSaveTop: $('resetSaveTop')
  };

  function loadState() {
    try {
      const saved = JSON.parse(localStorage.getItem(SAVE_KEY) || 'null');
      return { ...defaultState(), ...(saved || {}) };
    } catch (error) {
      console.warn('Save load failed:', error);
      return defaultState();
    }
  }

  function saveState() {
    state.bestLevel = Math.max(state.bestLevel, state.level);
    state.bestWave = Math.max(state.bestWave, state.wave);
    localStorage.setItem(SAVE_KEY, JSON.stringify(state));
  }

  function clamp(value, min, max) {
    return Math.max(min, Math.min(max, value));
  }

  function randomOf(list) {
    return list[Math.floor(Math.random() * list.length)];
  }

  function createMonster() {
    const zone = zones[state.zone % zones.length];
    const base = randomOf(zone.monsters);
    const growth = 1 + state.wave * 0.16 + state.level * 0.05;
    const maxHp = Math.round(base[2] * growth);
    return {
      name: base[0],
      sprite: base[1],
      story: base[4] || zone.lore || '',
      maxHp,
      hp: maxHp,
      atk: Math.round(base[3] * (1 + state.wave * 0.08))
    };
  }

  function damageRange(base) {
    const variance = Math.random() * 0.28 + 0.86;
    return Math.max(1, Math.round(base * variance));
  }

  function playerDamage(scale = 1) {
    return damageRange((state.atk + state.level * 2) * scale);
  }

  function monsterDamage() {
    const shieldBonus = state.shieldTurns > 0 ? 3 : 0;
    const raw = damageRange(monster.atk - state.def - shieldBonus + state.wave * 0.4);
    return Math.max(1, raw);
  }

  function render() {
    refs.level.textContent = state.level;
    refs.hpText.textContent = `${state.hp} / ${state.maxHp}`;
    refs.hpBar.style.width = `${(state.hp / state.maxHp) * 100}%`;
    refs.mpText.textContent = `${state.mp} / ${state.maxMp}`;
    refs.mpBar.style.width = `${(state.mp / state.maxMp) * 100}%`;
    refs.expText.textContent = `${state.exp} / ${state.expToNext}`;
    refs.expBar.style.width = `${(state.exp / state.expToNext) * 100}%`;
    refs.atkStat.textContent = state.atk;
    refs.defStat.textContent = state.def + (state.shieldTurns > 0 ? ` +${state.shieldTurns}` : '');
    refs.goldStat.textContent = state.gold;
    refs.waveStat.textContent = state.wave;
    refs.zoneName.textContent = zones[state.zone % zones.length].name;

    refs.monsterSprite.textContent = monster.sprite;
    refs.monsterName.textContent = monster.name;
    refs.monsterTitle.textContent = monster.name;
    refs.monsterHpText.textContent = `${monster.hp} / ${monster.maxHp}`;
    refs.monsterHpBar.style.width = `${(monster.hp / monster.maxHp) * 100}%`;

    refs.inventoryList.innerHTML = state.inventory.slice(-8).map((item) => `<li>${item}</li>`).join('');
    refs.questText.textContent = `몬스터 3마리 처치 (${state.questKills}/3)`;
    refs.questBar.style.width = `${Math.min(100, (state.questKills / 3) * 100)}%`;
    refs.bestLevel.textContent = state.bestLevel;
    refs.bestWave.textContent = state.bestWave;
    refs.totalKills.textContent = state.totalKills;

    renderSkills();
    saveState();
  }

  function renderSkills() {
    const now = Date.now();
    refs.skillGrid.innerHTML = '';
    const template = $('skillTemplate');
    skills.forEach((skill, index) => {
      const node = template.content.firstElementChild.cloneNode(true);
      const remain = Math.max(0, (state.cooldowns[skill.id] || 0) - now);
      node.querySelector('.skill-icon').textContent = skill.icon;
      node.querySelector('.skill-name').textContent = skill.name;
      node.querySelector('.skill-meta').textContent = remain > 0 ? `쿨타임 ${(remain / 1000).toFixed(1)}초` : skill.text;
      node.querySelector('.cooldown-mask').style.transform = `scaleY(${remain > 0 ? remain / skill.cd : 0})`;
      node.title = skill.story || skill.text;
      node.dataset.hotkey = String(index + 1);
      node.disabled = remain > 0 || state.mp < skill.mp || busy;
      node.addEventListener('click', () => useSkill(skill));
      refs.skillGrid.appendChild(node);
    });
  }

  function addLog(message, type = '') {
    const li = document.createElement('li');
    li.className = type;
    li.innerHTML = `<time>${new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}</time><span>${message}</span>`;
    refs.logList.prepend(li);
    while (refs.logList.children.length > 12) refs.logList.lastElementChild.remove();
  }

  function flashImpact(text, type = '') {
    refs.impact.textContent = text;
    refs.impact.className = `impact show ${type}`;
    setTimeout(() => refs.impact.className = 'impact', 550);
  }

  function shake(element) {
    element.classList.remove('shake');
    void element.offsetWidth;
    element.classList.add('shake');
  }

  async function performTurn(action) {
    if (busy || state.hp <= 0) return;
    busy = true;
    render();
    await action();
    if (monster.hp > 0 && state.hp > 0) {
      await delay(360);
      const dmg = monsterDamage();
      state.hp = clamp(state.hp - dmg, 0, state.maxHp);
      if (state.shieldTurns > 0) state.shieldTurns -= 1;
      shake(refs.playerUnit);
      addLog(`${monster.name} 반격! ${dmg} 피해`, 'danger-text');
      if (state.hp <= 0) gameOver();
    }
    state.mp = clamp(state.mp + 2, 0, state.maxMp);
    busy = false;
    render();
  }

  function delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  function basicAttack() {
    return performTurn(async () => {
      const dmg = playerDamage(1);
      monster.hp = clamp(monster.hp - dmg, 0, monster.maxHp);
      shake(refs.monsterUnit);
      flashImpact(`-${dmg}`, 'slash');
      addLog(`기본 공격으로 ${dmg} 피해`);
      if (monster.hp <= 0) defeatMonster();
    });
  }

  function useSkill(skill) {
    const now = Date.now();
    if (state.mp < skill.mp) return addLog('MP가 부족합니다.', 'warn-text');
    if ((state.cooldowns[skill.id] || 0) > now) return addLog('아직 스킬 쿨타임입니다.', 'warn-text');

    return performTurn(async () => {
      state.mp -= skill.mp;
      state.cooldowns[skill.id] = now + skill.cd;

      if (skill.buff) {
        state.shieldTurns = Math.max(state.shieldTurns, skill.buff);
        flashImpact('DEF+', 'guard');
        addLog(`${skill.name} 사용. ${skill.buff}턴 방어 강화`, 'good-text');
        if (!skill.scale) return;
      }

      if (skill.scale) {
        const dmg = playerDamage(skill.scale || 1);
        monster.hp = clamp(monster.hp - dmg, 0, monster.maxHp);
        shake(refs.monsterUnit);
        flashImpact(`-${dmg}`, skill.id);
        addLog(`${skill.name} 명중! ${dmg} 피해${skill.lore ? ` · ${skill.story}` : ''}`, 'good-text');

        if (skill.heal) {
          const amount = Math.round(skill.heal + state.level * 2);
          state.hp = clamp(state.hp + amount, 0, state.maxHp);
          addLog(`${skill.name} 흡수 효과. HP ${amount} 회복`, 'good-text');
        }

        if (monster.hp <= 0) defeatMonster();
        return;
      }

      if (skill.heal) {
        const amount = Math.round(skill.heal + state.level * 3);
        state.hp = clamp(state.hp + amount, 0, state.maxHp);
        flashImpact(`+${amount}`, 'heal');
        addLog(`${skill.name} 사용. HP ${amount} 회복`, 'good-text');
      }
    });
  }

  function defeatMonster() {
    const expGain = Math.round(28 + state.wave * 8 + state.zone * 6);
    const goldGain = Math.round(12 + state.wave * 5 + Math.random() * 18);
    state.exp += expGain;
    state.gold += goldGain;
    state.kills += 1;
    state.totalKills += 1;
    state.questKills += 1;
    addLog(`${monster.name} 처치! EXP +${expGain}, Gold +${goldGain}`, 'good-text');
    if (monster.story) addLog(`기록 해금: ${monster.story}`, 'loot-text');
    maybeDropItem();
    if (state.questKills >= 3) completeQuest();
    while (state.exp >= state.expToNext) levelUp();
    state.wave += 1;
    if (state.wave % 4 === 0) state.zone = (state.zone + 1) % zones.length;
    monster = createMonster();
  }

  function maybeDropItem() {
    if (Math.random() > 0.48) return;
    const baseDrops = [
      { name: '강철 장검', atk: 1, def: 0, grade: 'normal' },
      { name: '마력 반지', atk: 1, def: 1, grade: 'magic' },
      { name: '용비늘 갑옷', atk: 0, def: 2, grade: 'rare' },
      { name: '기사단 망토', atk: 0, def: 1, grade: 'normal' },
      { name: '축복받은 검', atk: 2, def: 0, grade: 'rare' }
    ];
    const loreDrops = (lore.items || []).map((item) => ({ ...item, lore: true }));
    const item = randomOf([...baseDrops, ...loreDrops]);
    state.atk += item.atk || 0;
    state.def += item.def || 0;
    const statText = `${item.atk ? ` 공격+${item.atk}` : ''}${item.def ? ` 방어+${item.def}` : ''}`;
    const gradeText = item.grade ? `[${item.grade}] ` : '';
    state.inventory.push(`${gradeText}${item.name}${statText}`);
    addLog(`아이템 획득: ${gradeText}${item.name}${statText}`, 'loot-text');
    if (item.story) addLog(`아이템 기록: ${item.story}`, 'loot-text');
  }

  function completeQuest() {
    const reward = 60 + state.level * 12;
    state.gold += reward;
    state.questKills = 0;
    state.inventory.push(`퀘스트 보급상자 +${reward}G`);
    addLog(`퀘스트 완료! 보상 Gold +${reward}`, 'loot-text');
  }

  function levelUp() {
    state.exp -= state.expToNext;
    state.level += 1;
    state.expToNext = Math.round(state.expToNext * 1.32 + 24);
    state.maxHp += 18;
    state.maxMp += 7;
    state.atk += 3;
    state.def += 1;
    state.hp = state.maxHp;
    state.mp = state.maxMp;
    flashImpact('LEVEL UP', 'level');
    addLog(`레벨업! Lv.${state.level} 달성`, 'good-text');
  }

  function gameOver() {
    addLog('전투 불능. 마을로 귀환합니다.', 'danger-text');
    state.hp = Math.ceil(state.maxHp * 0.62);
    state.mp = Math.ceil(state.maxMp * 0.55);
    state.gold = Math.max(0, state.gold - Math.round(10 + state.level * 4));
    state.shieldTurns = 0;
  }

  function usePotion() {
    const potionIndex = state.inventory.findIndex((item) => item.includes('물약'));
    if (potionIndex === -1) {
      if (state.gold < 30) return addLog('물약이 없고 골드도 부족합니다.', 'warn-text');
      state.gold -= 30;
      state.inventory.push('빨간 물약 x1');
      addLog('상점에서 빨간 물약을 구매했습니다.', 'loot-text');
    }
    state.hp = clamp(state.hp + 45 + state.level * 3, 0, state.maxHp);
    const idx = state.inventory.findIndex((item) => item.includes('물약'));
    if (idx >= 0) state.inventory.splice(idx, 1);
    flashImpact('POTION', 'heal');
    addLog('빨간 물약 사용. HP 회복', 'good-text');
    render();
  }

  function restInTown() {
    const cost = Math.min(state.gold, 20 + state.level * 3);
    state.gold -= cost;
    state.hp = state.maxHp;
    state.mp = state.maxMp;
    state.shieldTurns = 0;
    addLog(`마을 휴식 완료. 비용 ${cost}G`, 'good-text');
    render();
  }

  function nextZone() {
    state.zone = (state.zone + 1) % zones.length;
    monster = createMonster();
    addLog(`${zones[state.zone].name}(으)로 이동했습니다.`, 'loot-text');
    if (zones[state.zone].lore) addLog(`지역 기록: ${zones[state.zone].lore}`, 'loot-text');
    render();
  }

  function resetSave() {
    localStorage.removeItem(SAVE_KEY);
    state = defaultState();
    monster = createMonster();
    refs.logList.innerHTML = '';
    addLog('저장 데이터 초기화 완료. 새 모험을 시작합니다.', 'warn-text');
    render();
  }

  function bindEvents() {
    refs.attackBtn.addEventListener('click', basicAttack);
    refs.potionBtn.addEventListener('click', usePotion);
    refs.restBtn.addEventListener('click', restInTown);
    refs.nextZoneBtn.addEventListener('click', nextZone);
    refs.resetSaveTop.addEventListener('click', resetSave);
    document.addEventListener('keydown', (event) => {
      const keyMap = { '1': 0, '2': 1, '3': 2, '4': 3, '5': 4, '6': 5, '7': 6, '8': 7, '9': 8 };
      if (event.code === 'Space') {
        event.preventDefault();
        basicAttack();
      }
      if (keyMap[event.key] !== undefined && skills[keyMap[event.key]]) useSkill(skills[keyMap[event.key]]);
    });
  }

  bindEvents();
  addLog('YDH Chronicle 접속 완료. 스페이스바로 기본 공격 가능.', 'good-text');
  if (lore.title) addLog(`소설 콘텐츠 활성화: ${lore.title}`, 'loot-text');
  render();
  setInterval(renderSkills, 150);
})();
