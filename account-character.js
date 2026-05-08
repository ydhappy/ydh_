(() => {
  'use strict';

  const schema = window.YDH_SAVE_SCHEMA;
  if (!schema) return;

  const keys = schema.localStorageKeys;
  const classes = {
    knight: { label: '기사', icon: '⚔️', hp: 110, mp: 35, atk: 14, def: 6 },
    mage: { label: '마법사', icon: '🔥', hp: 82, mp: 70, atk: 17, def: 3 },
    rogue: { label: '도적', icon: '🗡️', hp: 94, mp: 46, atk: 16, def: 4 },
    priest: { label: '사제', icon: '✨', hp: 96, mp: 62, atk: 11, def: 5 }
  };

  function readJson(key, fallback = null) {
    try {
      return JSON.parse(localStorage.getItem(key) || 'null') ?? fallback;
    } catch {
      return fallback;
    }
  }

  function writeJson(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  function nowIso() {
    return new Date().toISOString();
  }

  function makeId(prefix) {
    return `${prefix}_${Date.now()}_${Math.random().toString(16).slice(2, 8)}`;
  }

  function account() {
    return readJson(keys.accountProfile, null);
  }

  function slots() {
    return readJson(keys.characterSlots, []);
  }

  function selected() {
    return readJson(keys.selectedCharacter, null);
  }

  function saveAccount(displayName) {
    const prev = account();
    const profile = {
      accountId: prev?.accountId || makeId('acc'),
      provider: 'local',
      displayName: displayName.trim() || 'YDH Player',
      createdAt: prev?.createdAt || nowIso(),
      lastLoginAt: nowIso()
    };
    writeJson(keys.accountProfile, profile);
    return profile;
  }

  function createDefaultSave(slot) {
    const cls = classes[slot.classId] || classes.knight;
    return {
      characterId: slot.characterId,
      accountId: slot.accountId,
      name: slot.name,
      classId: slot.classId,
      level: 1,
      exp: 0,
      expToNext: 80,
      hp: cls.hp,
      maxHp: cls.hp,
      mp: cls.mp,
      maxMp: cls.mp,
      atk: cls.atk,
      def: cls.def,
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
    };
  }

  function createCharacter(name, classId) {
    const profile = account() || saveAccount('YDH Player');
    const list = slots();
    if (list.length >= 3) return { ok: false, error: '캐릭터 슬롯은 최대 3개입니다.' };
    const slot = {
      characterId: makeId('char'),
      accountId: profile.accountId,
      slot: list.length + 1,
      name: name.trim() || `검은 기사 ${list.length + 1}`,
      classId,
      createdAt: nowIso(),
      lastSelectedAt: nowIso()
    };
    list.push(slot);
    writeJson(keys.characterSlots, list);
    selectCharacter(slot.characterId, false);
    localStorage.setItem(keys.character, JSON.stringify(createDefaultSave(slot)));
    window.dispatchEvent(new CustomEvent('ydh-map-event', { detail: { message: `캐릭터 생성: ${slot.name}` } }));
    return { ok: true, slot };
  }

  function selectCharacter(characterId, reload = true) {
    const list = slots();
    const slot = list.find((item) => item.characterId === characterId);
    if (!slot) return false;
    slot.lastSelectedAt = nowIso();
    writeJson(keys.characterSlots, list);
    writeJson(keys.selectedCharacter, slot);
    const currentSave = readJson(keys.character, null);
    if (!currentSave || currentSave.characterId !== slot.characterId) {
      localStorage.setItem(keys.character, JSON.stringify(createDefaultSave(slot)));
      localStorage.removeItem(keys.map);
      localStorage.removeItem(keys.chapterQuests);
      localStorage.removeItem(keys.codexUnlocks);
    } else {
      currentSave.characterId = slot.characterId;
      currentSave.accountId = slot.accountId;
      currentSave.name = slot.name;
      currentSave.classId = slot.classId;
      writeJson(keys.character, currentSave);
    }
    window.dispatchEvent(new CustomEvent('ydh-character-selected', { detail: { slot } }));
    if (reload) window.location.reload();
    return true;
  }

  function deleteCharacter(characterId) {
    const list = slots().filter((item) => item.characterId !== characterId).map((item, index) => ({ ...item, slot: index + 1 }));
    writeJson(keys.characterSlots, list);
    const current = selected();
    if (current?.characterId === characterId) {
      localStorage.removeItem(keys.selectedCharacter);
      localStorage.removeItem(keys.character);
      localStorage.removeItem(keys.map);
      localStorage.removeItem(keys.chapterQuests);
      localStorage.removeItem(keys.codexUnlocks);
    }
    render();
  }

  function render() {
    const root = document.getElementById('accountCharacterRoot');
    if (!root) return;
    const profile = account();
    const list = slots();
    const current = selected();
    root.innerHTML = `
      <div class="account-head">
        <div>
          <p class="eyebrow">ACCOUNT / CHARACTER</p>
          <h2>계정 및 캐릭터 선택</h2>
          <p>현재는 로컬 계정 방식입니다. 서버 저장 스냅샷에는 계정 ID와 캐릭터 슬롯 정보가 함께 포함됩니다.</p>
        </div>
        <div class="account-badge">${profile ? `${profile.displayName}<br />${list.length}/3 슬롯` : '로컬 계정<br />미설정'}</div>
      </div>
      <div class="account-form">
        <input id="accountNameInput" type="text" placeholder="계정 표시 이름" value="${profile?.displayName || ''}" maxlength="24" />
        <button type="button" id="saveAccountBtn">계정 저장</button>
      </div>
      <div class="character-form">
        <input id="characterNameInput" type="text" placeholder="새 캐릭터 이름" maxlength="18" />
        <select id="characterClassSelect">
          ${Object.entries(classes).map(([id, cls]) => `<option value="${id}">${cls.icon} ${cls.label}</option>`).join('')}
        </select>
        <button type="button" id="createCharacterBtn">캐릭터 생성</button>
      </div>
      <div class="character-slots">
        ${[0, 1, 2].map((index) => renderSlot(list[index], current)).join('')}
      </div>
      <div class="account-note">캐릭터를 선택하면 해당 캐릭터 기준으로 새 저장 데이터가 준비됩니다. 기존 선택 캐릭터와 다르면 맵/퀘스트/도감 진행은 초기화됩니다.</div>
    `;

    document.getElementById('saveAccountBtn')?.addEventListener('click', () => {
      saveAccount(document.getElementById('accountNameInput')?.value || 'YDH Player');
      render();
    });
    document.getElementById('createCharacterBtn')?.addEventListener('click', () => {
      const result = createCharacter(
        document.getElementById('characterNameInput')?.value || '',
        document.getElementById('characterClassSelect')?.value || 'knight'
      );
      if (!result.ok) alert(result.error);
      render();
    });
    root.querySelectorAll('[data-select-character]').forEach((button) => {
      button.addEventListener('click', () => selectCharacter(button.dataset.selectCharacter));
    });
    root.querySelectorAll('[data-delete-character]').forEach((button) => {
      button.addEventListener('click', () => {
        if (confirm('이 캐릭터 슬롯을 삭제할까요? 현재 선택 캐릭터면 진행 데이터도 초기화됩니다.')) deleteCharacter(button.dataset.deleteCharacter);
      });
    });
  }

  function renderSlot(slot, current) {
    if (!slot) return '<article class="character-slot-card empty"><h3>빈 슬롯</h3><p>새 캐릭터를 생성하면 이곳에 표시됩니다.</p></article>';
    const cls = classes[slot.classId] || classes.knight;
    const isSelected = current?.characterId === slot.characterId;
    return `
      <article class="character-slot-card ${isSelected ? 'selected' : ''}">
        <h3>${cls.icon} ${slot.name}</h3>
        <p>${cls.label} · SLOT ${slot.slot}<br />생성 ${new Date(slot.createdAt).toLocaleDateString('ko-KR')}</p>
        <div class="character-slot-actions">
          <button type="button" data-select-character="${slot.characterId}">${isSelected ? '선택됨' : '선택'}</button>
          <button type="button" class="danger" data-delete-character="${slot.characterId}">삭제</button>
        </div>
      </article>
    `;
  }

  function create() {
    const section = document.createElement('section');
    section.className = 'panel account-panel';
    section.id = 'account';
    section.innerHTML = '<div id="accountCharacterRoot"></div>';
    const game = document.getElementById('game');
    if (game?.parentNode) game.parentNode.insertBefore(section, game);
    else document.querySelector('main')?.prepend(section);
    render();
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', create);
  else create();
})();
