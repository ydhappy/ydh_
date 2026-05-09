(() => {
  'use strict';

  const quests = window.YDH_CHAPTER_QUESTS || [];
  if (!quests.length) return;

  const SAVE_KEY = 'ydh-chapter-quests-v1';

  function loadState() {
    try {
      return JSON.parse(localStorage.getItem(SAVE_KEY) || '{}') || {};
    } catch {
      return {};
    }
  }

  let state = loadState();

  function saveState() {
    localStorage.setItem(SAVE_KEY, JSON.stringify(state));
  }

  function getObjectiveValue(questId, objectiveId) {
    return state[questId]?.objectives?.[objectiveId] || 0;
  }

  function isObjectiveDone(quest, objective) {
    return getObjectiveValue(quest.id, objective.id) >= objective.required;
  }

  function isQuestDone(quest) {
    return quest.objectives.every((objective) => isObjectiveDone(quest, objective));
  }

  function completedCount() {
    return quests.filter(isQuestDone).length;
  }

  function ensureQuest(questId) {
    if (!state[questId]) state[questId] = { objectives: {}, rewardClaimed: false };
    if (!state[questId].objectives) state[questId].objectives = {};
  }

  function progress(type, target, amount = 1) {
    let changed = false;
    quests.forEach((quest) => {
      ensureQuest(quest.id);
      quest.objectives.forEach((objective) => {
        if (objective.type !== type || objective.target !== target) return;
        const current = getObjectiveValue(quest.id, objective.id);
        const next = Math.min(objective.required, current + amount);
        if (next !== current) {
          state[quest.id].objectives[objective.id] = next;
          changed = true;
        }
      });
    });
    if (changed) {
      saveState();
      render();
      announce('챕터 퀘스트 진행도가 갱신되었습니다.');
    }
  }

  function rewardText(reward = {}) {
    const parts = [];
    if (reward.gold) parts.push(`Gold +${reward.gold}`);
    if (reward.exp) parts.push(`EXP +${reward.exp}`);
    if (reward.item) parts.push(reward.item);
    return parts;
  }

  function render() {
    const root = document.getElementById('chapterQuestRoot');
    if (!root) return;
    const done = completedCount();
    root.innerHTML = `
      <div class="chapter-quests-head">
        <div>
          <p class="eyebrow">CHAPTER QUEST</p>
          <h2>검은 달 연대기 퀘스트</h2>
          <p>맵 방문, NPC 대화, 몬스터 처치, 아이템 획득, Tiled marker 확인으로 소설 챕터를 진행합니다.</p>
        </div>
        <div class="chapter-progress-badge">완료 ${done} / ${quests.length}</div>
      </div>
      <div class="quest-chain">
        ${quests.map(renderQuest).join('')}
      </div>
      <button class="btn small quest-reset" type="button" id="resetChapterQuests">퀘스트 진행 초기화</button>
    `;
    document.getElementById('resetChapterQuests')?.addEventListener('click', () => {
      state = {};
      saveState();
      render();
      announce('챕터 퀘스트 진행도를 초기화했습니다.');
    });
  }

  function renderQuest(quest) {
    const completed = isQuestDone(quest);
    return `
      <article class="chapter-quest-card ${completed ? 'completed' : ''}">
        <small>${quest.chapterId} · ${quest.mapId}</small>
        <h3>${quest.title}</h3>
        <p>${quest.description}</p>
        <div class="quest-objectives">
          ${quest.objectives.map((objective) => renderObjective(quest, objective)).join('')}
        </div>
        <div class="quest-reward">
          ${rewardText(quest.rewards).map((item) => `<span>${item}</span>`).join('')}
        </div>
      </article>
    `;
  }

  function renderObjective(quest, objective) {
    const current = getObjectiveValue(quest.id, objective.id);
    const done = current >= objective.required;
    return `
      <div class="quest-objective ${done ? 'done' : ''}">
        <strong>${done ? '✅' : '⬜'} ${objective.label}</strong>
        <span>${Math.min(current, objective.required)} / ${objective.required}</span>
      </div>
    `;
  }

  function createUI() {
    const section = document.createElement('section');
    section.className = 'panel chapter-quests';
    section.id = 'quests';
    section.innerHTML = '<div id="chapterQuestRoot"></div>';
    const lore = document.getElementById('lore');
    if (lore?.parentNode) lore.parentNode.insertBefore(section, lore);
    else document.querySelector('main')?.appendChild(section);
    render();
  }

  function announce(message) {
    window.dispatchEvent(new CustomEvent('ydh-map-event', { detail: { message } }));
  }

  function bindEvents() {
    window.addEventListener('ydh-player-moved', () => {
      const mapTitle = document.getElementById('mapTitle')?.textContent || '';
      const mapIdByTitle = {
        '말하는 섬 해안': 'talking-island',
        '은빛 숲': 'silver-forest',
        '버려진 광산': 'ancient-cave',
        '검은 달 폐허': 'black-moon-ruins',
        '별빛 기록관': 'starlight-archive',
        '거울 늪': 'mirror-marsh',
        '심연의 왕좌': 'abyss-throne',
        '달문 광장': 'moon-gate-yard'
      };
      const mapId = mapIdByTitle[mapTitle];
      if (mapId) progress('visitMap', mapId, 1);
    });

    window.addEventListener('ydh-map-event', (event) => {
      const message = event.detail?.message || '';
      const npcMatch = message.match(/^(.+?) 대화:/);
      if (npcMatch) progress('talkNpc', npcMatch[1], 1);
      const monsterMatch = message.match(/맵 조우 이벤트: (.+?) 출현/);
      if (monsterMatch) progress('defeatMonster', monsterMatch[1], 0);
    });

    window.addEventListener('ydh-tiled-quest-trigger', (event) => {
      const detail = event.detail || {};
      if (!detail.type || !detail.target) return;
      progress(detail.type, detail.target, Number(detail.amount || 1));
      announce(`Tiled 퀘스트 트리거: ${detail.type} / ${detail.target}`);
    });

    const logList = document.getElementById('logList');
    if (logList && window.MutationObserver) {
      const observer = new MutationObserver(() => {
        const text = logList.firstElementChild?.textContent || '';
        const defeat = text.match(/(.+?) 처치!/);
        if (defeat) progress('defeatMonster', defeat[1].replace(/오전|오후|\d|:|\s/g, '').trim() || defeat[1], 1);
        quests.forEach((quest) => quest.objectives.forEach((objective) => {
          if (objective.type === 'obtainItem' && text.includes(objective.target)) progress('obtainItem', objective.target, 1);
        }));
      });
      observer.observe(logList, { childList: true });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
      createUI();
      bindEvents();
    });
  } else {
    createUI();
    bindEvents();
  }
})();
