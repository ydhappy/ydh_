(() => {
  'use strict';

  const SAVE_KEY = 'ydh-codex-unlocks-v1';
  const lore = window.YDH_LORE_CONTENT || {};

  const initialUnlocked = [
    'chapters:ch1',
    'skills:moonSlash'
  ];

  function load() {
    try {
      const saved = JSON.parse(localStorage.getItem(SAVE_KEY) || '{}');
      return { unlocked: {}, ...saved };
    } catch {
      return { unlocked: {} };
    }
  }

  const state = load();
  initialUnlocked.forEach((key) => { state.unlocked[key] = true; });
  save();

  function save() {
    localStorage.setItem(SAVE_KEY, JSON.stringify(state));
  }

  function normalize(value) {
    return String(value || '')
      .replace(/\[[^\]]+\]/g, '')
      .replace(/공격\+\d+/g, '')
      .replace(/방어\+\d+/g, '')
      .replace(/획득:|아이템|기록|사용|명중|처치!/g, '')
      .replace(/오전|오후|\d|:|\s/g, '')
      .trim();
  }

  function keyOf(category, id) {
    return `${category}:${id}`;
  }

  function itemId(item) {
    return item?.id || item?.name || item?.title;
  }

  function isUnlocked(category, item) {
    return !!state.unlocked[keyOf(category, itemId(item))];
  }

  function unlock(category, id, reason = '') {
    if (!category || !id) return false;
    const key = keyOf(category, id);
    if (state.unlocked[key]) return false;
    state.unlocked[key] = true;
    state.lastUnlock = { category, id, reason, at: Date.now() };
    save();
    window.dispatchEvent(new CustomEvent('ydh-codex-updated', { detail: state.lastUnlock }));
    window.dispatchEvent(new CustomEvent('ydh-map-event', { detail: { message: `도감 해금: ${id}${reason ? ` · ${reason}` : ''}` } }));
    return true;
  }

  function unlockByName(category, name, reason = '') {
    const clean = normalize(name);
    const list = lore[category] || [];
    const found = list.find((entry) => {
      const title = normalize(entry.name || entry.title || entry.id);
      return title && (title.includes(clean) || clean.includes(title));
    });
    if (found) return unlock(category, itemId(found), reason);
    return false;
  }

  function unlockChapterByMap(mapIdOrName) {
    const clean = normalize(mapIdOrName);
    const chapter = (lore.chapters || []).find((entry) => {
      return normalize(entry.mapId).includes(clean) || clean.includes(normalize(entry.mapId)) || normalize(entry.title).includes(clean);
    });
    if (chapter) unlock('chapters', chapter.id, '챕터 지역 단서 발견');
  }

  function counts(category) {
    const list = lore[category] || [];
    const total = list.length;
    const opened = list.filter((item) => isUnlocked(category, item)).length;
    return { opened, total };
  }

  function reset() {
    state.unlocked = {};
    initialUnlocked.forEach((key) => { state.unlocked[key] = true; });
    save();
    window.dispatchEvent(new CustomEvent('ydh-codex-updated', { detail: { reset: true } }));
  }

  function bindMapEvents() {
    window.addEventListener('ydh-player-moved', () => {
      const title = document.getElementById('mapTitle')?.textContent || '';
      unlockByName('maps', title, '지역 방문');
      unlockChapterByMap(title);
    });

    window.addEventListener('ydh-map-event', (event) => {
      const message = event.detail?.message || '';
      const npc = message.match(/^(.+?) 대화:/)?.[1];
      if (npc) unlockByName('npcs', npc, 'NPC 대화');
      const monster = message.match(/맵 조우 이벤트: (.+?) 출현/)?.[1];
      if (monster) unlockByName('monsters', monster, '몬스터 조우');
      const portal = message.match(/포탈 이동: (.+)$/)?.[1];
      if (portal) {
        unlockByName('maps', portal, '포탈 이동');
        unlockChapterByMap(portal);
      }
    });
  }

  function bindLogEvents() {
    const logList = document.getElementById('logList');
    if (!logList || !window.MutationObserver) return;
    const observer = new MutationObserver(() => {
      const text = logList.firstElementChild?.textContent || '';
      const defeated = text.match(/(.+?) 처치!/)?.[1];
      if (defeated) unlockByName('monsters', defeated, '몬스터 처치');
      const itemText = text.match(/아이템 획득:\s*(.+)$/)?.[1];
      if (itemText) unlockByName('items', itemText, '아이템 획득');
      (lore.skills || []).forEach((skill) => {
        if (text.includes(skill.name)) unlock('skills', skill.id, '스킬 사용');
      });
    });
    observer.observe(logList, { childList: true });
  }

  window.YDH_CODEX_UNLOCKS = {
    isUnlocked,
    unlock,
    unlockByName,
    counts,
    reset,
    keyOf,
    state
  };

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
      bindMapEvents();
      bindLogEvents();
    });
  } else {
    bindMapEvents();
    bindLogEvents();
  }
})();
