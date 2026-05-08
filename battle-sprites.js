(() => {
  'use strict';

  const entities = window.YDH_ENTITIES || {};
  const monsterByName = {
    '그림자 늑대': entities.monsters?.wolf,
    '고블린 약탈자': entities.monsters?.goblin,
    '광산 골렘': entities.monsters?.golem,
    '동굴 박쥐': entities.monsters?.wolf,
    '해골 정찰병': entities.monsters?.goblin,
    '독버섯 정령': entities.monsters?.goblin,
    '모래 암살자': entities.monsters?.goblin,
    '불꽃 임프': entities.monsters?.goblin,
    '와이번': entities.monsters?.golem,
    '드레이크': entities.monsters?.golem,
    '타락 기사': entities.monsters?.golem,
    '고대 리치': entities.monsters?.golem,
    '심연 파수꾼': entities.monsters?.golem,
    '카오스 비스트': entities.monsters?.golem,
    '발록의 그림자': entities.monsters?.golem,
    '봉인된 마룡': entities.monsters?.golem
  };

  const state = {
    playerDir: 0,
    monsterDir: 8,
    lastMonsterName: ''
  };

  function setSprite(el, entity, direction, className) {
    if (!el || !entity?.sheet) return;
    el.className = `unit-sprite battle-entity-sprite ${className}`;
    el.style.backgroundImage = `url(${entity.sheet})`;
    el.style.setProperty('--battle-dir', direction);
    el.textContent = entity.name;
    el.setAttribute('aria-label', `${entity.name} ${direction}/15`);
    el.title = `${entity.name} · 16방향 ${direction}`;
  }

  function detectMonsterEntity() {
    const name = document.getElementById('monsterName')?.textContent?.trim() || '';
    return monsterByName[name] || entities.monsters?.wolf || entities.monster;
  }

  function renderBattleSprites() {
    const battlefield = document.getElementById('battlefield');
    const playerSprite = document.querySelector('#playerUnit .unit-sprite');
    const monsterSprite = document.getElementById('monsterSprite');
    const monsterName = document.getElementById('monsterName')?.textContent?.trim() || '';

    battlefield?.classList.add('sprite-ready');
    setSprite(playerSprite, entities.player, state.playerDir, 'player-battle');

    if (monsterName !== state.lastMonsterName) {
      state.monsterDir = 8;
      state.lastMonsterName = monsterName;
    }
    setSprite(monsterSprite, detectMonsterEntity(), state.monsterDir, 'monster-battle');
  }

  function watchCombatMotion() {
    const playerUnit = document.getElementById('playerUnit');
    const monsterUnit = document.getElementById('monsterUnit');
    if (playerUnit) {
      playerUnit.addEventListener('animationstart', () => {
        state.playerDir = 0;
        state.monsterDir = 8;
        renderBattleSprites();
      });
    }
    if (monsterUnit) {
      monsterUnit.addEventListener('animationstart', () => {
        state.monsterDir = 8;
        state.playerDir = 0;
        renderBattleSprites();
      });
    }
  }

  function observeMonsterName() {
    const target = document.getElementById('monsterName');
    if (!target || !window.MutationObserver) return;
    const observer = new MutationObserver(renderBattleSprites);
    observer.observe(target, { childList: true, characterData: true, subtree: true });
  }

  function boot() {
    renderBattleSprites();
    watchCombatMotion();
    observeMonsterName();
    setInterval(renderBattleSprites, 800);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', boot);
  } else {
    boot();
  }
})();
