(() => {
  'use strict';

  const config = window.YDH_ANIMATIONS || { timings: { walkMs: 320 }, classes: { walking: 'is-walking' } };
  let walkTimer = null;

  function findPlayerTile() {
    return document.querySelector('.map-tile.player-here');
  }

  function findPlayerSprite() {
    return document.querySelector('.map-tile.player-here .entity-sprite.player');
  }

  function playWalkAnimation(detail = {}) {
    const tile = findPlayerTile();
    const sprite = findPlayerSprite();
    const duration = detail.duration || config.timings?.walkMs || 320;
    const walkClass = config.classes?.walking || 'is-walking';

    if (!tile || !sprite) return;

    clearTimeout(walkTimer);
    tile.classList.remove('is-moving');
    sprite.classList.remove(walkClass);
    void sprite.offsetWidth;

    tile.classList.add('is-moving');
    sprite.classList.add(walkClass);
    sprite.style.setProperty('--walk-duration', `${duration}ms`);

    walkTimer = setTimeout(() => {
      tile.classList.remove('is-moving');
      sprite.classList.remove(walkClass);
    }, duration + 40);
  }

  window.addEventListener('ydh-player-moved', (event) => playWalkAnimation(event.detail || {}));
})();
