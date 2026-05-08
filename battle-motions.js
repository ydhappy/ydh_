(() => {
  'use strict';

  const config = window.YDH_COMBAT_MOTIONS || {
    timings: { attackMs: 420, hitMs: 260, deathMs: 760, castMs: 520, healMs: 520, guardMs: 520 },
    classes: { attacking: 'is-attacking', hit: 'is-hit', dead: 'is-dead', casting: 'is-casting', healing: 'is-healing', guarding: 'is-guarding' },
    events: { play: 'ydh-battle-motion', resolved: 'ydh-battle-motion-resolved' }
  };

  const previous = {
    monsterHp: null,
    playerHp: null,
    monsterName: ''
  };

  function $(id) {
    return document.getElementById(id);
  }

  function getBattleSprite(target) {
    if (target === 'player') return document.querySelector('#playerUnit .battle-entity-sprite');
    if (target === 'monster') return document.querySelector('#monsterUnit .battle-entity-sprite');
    return null;
  }

  function parseHp(text) {
    const match = String(text || '').match(/(\d+)\s*\/\s*(\d+)/);
    if (!match) return null;
    return { current: Number(match[1]), max: Number(match[2]) };
  }

  function durationFor(type) {
    const timings = config.timings || {};
    if (type === 'attack') return timings.attackMs || 420;
    if (type === 'hit') return timings.hitMs || 260;
    if (type === 'death') return timings.deathMs || 760;
    if (type === 'cast') return timings.castMs || 520;
    if (type === 'heal') return timings.healMs || 520;
    if (type === 'guard') return timings.guardMs || 520;
    return 420;
  }

  function classFor(type) {
    const classes = config.classes || {};
    if (type === 'attack') return classes.attacking || 'is-attacking';
    if (type === 'hit') return classes.hit || 'is-hit';
    if (type === 'death') return classes.dead || 'is-dead';
    if (type === 'cast') return classes.casting || 'is-casting';
    if (type === 'heal') return classes.healing || 'is-healing';
    if (type === 'guard') return classes.guarding || 'is-guarding';
    return type;
  }

  function addBurst(type) {
    const field = $('battlefield');
    if (!field) return;
    const burst = document.createElement('span');
    const burstType = type === 'attack' || type === 'hit' || type === 'death' ? 'slash' : type;
    burst.className = `motion-burst ${burstType}`;
    field.appendChild(burst);
    setTimeout(() => burst.remove(), durationFor(type) + 120);
  }

  function playMotion(target, type, options = {}) {
    const sprite = getBattleSprite(target);
    const field = $('battlefield');
    const cls = classFor(type);
    const duration = options.duration || durationFor(type);
    if (!sprite) return;

    field?.classList.add('motion-active');
    sprite.classList.remove(cls);
    void sprite.offsetWidth;
    sprite.style.setProperty(`--${type}-duration`, `${duration}ms`);
    sprite.classList.add(cls);

    if (options.burst !== false) addBurst(type);

    setTimeout(() => {
      if (type !== 'death') sprite.classList.remove(cls);
      field?.classList.remove('motion-active');
      window.dispatchEvent(new CustomEvent(config.events?.resolved || 'ydh-battle-motion-resolved', {
        detail: { target, type, duration }
      }));
    }, duration + 40);
  }

  function dispatchMotion(target, type, options = {}) {
    window.dispatchEvent(new CustomEvent(config.events?.play || 'ydh-battle-motion', {
      detail: { target, type, ...options }
    }));
  }

  function handleHpChanges() {
    const monsterHp = parseHp($('monsterHpText')?.textContent);
    const playerHp = parseHp($('hpText')?.textContent);
    const monsterName = $('monsterName')?.textContent?.trim() || '';

    if (previous.monsterName && monsterName !== previous.monsterName) {
      dispatchMotion('monster', 'death', { burst: true });
    }

    if (previous.monsterHp && monsterHp) {
      if (monsterHp.current < previous.monsterHp.current) {
        dispatchMotion('player', 'attack', { burst: true });
        dispatchMotion('monster', monsterHp.current <= 0 ? 'death' : 'hit', { burst: true });
      }
    }

    if (previous.playerHp && playerHp) {
      if (playerHp.current < previous.playerHp.current) {
        dispatchMotion('monster', 'attack', { burst: true });
        dispatchMotion('player', playerHp.current <= 0 ? 'death' : 'hit', { burst: true });
      } else if (playerHp.current > previous.playerHp.current) {
        dispatchMotion('player', 'heal', { burst: true });
      }
    }

    previous.monsterHp = monsterHp;
    previous.playerHp = playerHp;
    previous.monsterName = monsterName;
  }

  function bindButtons() {
    $('attackBtn')?.addEventListener('click', () => dispatchMotion('player', 'attack', { burst: true }));
    document.querySelectorAll('.skill-card').forEach((button) => {
      button.addEventListener('click', () => {
        const name = button.querySelector('.skill-name')?.textContent || '';
        if (name.includes('힐')) dispatchMotion('player', 'heal', { burst: true });
        else if (name.includes('스킨')) dispatchMotion('player', 'guard', { burst: true });
        else dispatchMotion('player', 'cast', { burst: true });
      });
    });
  }

  function observeBattleText() {
    const targets = [$('monsterHpText'), $('hpText'), $('monsterName')].filter(Boolean);
    if (!targets.length || !window.MutationObserver) return;
    const observer = new MutationObserver(handleHpChanges);
    targets.forEach((target) => observer.observe(target, { childList: true, characterData: true, subtree: true }));
  }

  function observeSkillGrid() {
    const grid = $('skillGrid');
    if (!grid || !window.MutationObserver) return;
    const observer = new MutationObserver(bindButtons);
    observer.observe(grid, { childList: true });
  }

  function boot() {
    window.addEventListener(config.events?.play || 'ydh-battle-motion', (event) => {
      const detail = event.detail || {};
      playMotion(detail.target || 'player', detail.type || 'attack', detail);
    });

    handleHpChanges();
    observeBattleText();
    observeSkillGrid();
    bindButtons();
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
