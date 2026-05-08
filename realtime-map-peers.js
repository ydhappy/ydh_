(() => {
  'use strict';

  const PLAYER_SHEET = 'assets/sprites/player-16dir.svg';
  const FRAME_WIDTH = 64;

  function peers() {
    return Array.isArray(window.YDH_REALTIME_PEERS) ? window.YDH_REALTIME_PEERS : [];
  }

  function currentMapIndex() {
    const state = window.YDH_CURRENT_MAP_STATE;
    if (state && Number.isFinite(Number(state.mapIndex))) return Number(state.mapIndex);
    try {
      const keys = window.YDH_SAVE_SCHEMA?.localStorageKeys || {};
      const saved = JSON.parse(localStorage.getItem(keys.map || 'ydh-chronicle-map-v1') || '{}');
      return Number(saved.mapIndex || 0);
    } catch {
      return 0;
    }
  }

  function clearRemotePeers() {
    document.querySelectorAll('.remote-peer-sprite,.remote-peer-nameplate,.remote-peer-count').forEach((node) => node.remove());
    document.querySelectorAll('.map-tile.has-remote-peer').forEach((tile) => tile.classList.remove('has-remote-peer'));
  }

  function renderRemotePeers() {
    const mapIndex = currentMapIndex();
    const grouped = new Map();

    peers()
      .filter((peer) => Number(peer.mapIndex) === mapIndex)
      .forEach((peer) => {
        const x = Number(peer.x || 0);
        const y = Number(peer.y || 0);
        const key = `${x}:${y}`;
        const list = grouped.get(key) || [];
        list.push(peer);
        grouped.set(key, list);
      });

    clearRemotePeers();

    grouped.forEach((list, key) => {
      const [x, y] = key.split(':');
      const tile = document.querySelector(`.map-tile[data-map-index="${mapIndex}"][data-x="${x}"][data-y="${y}"]`);
      if (!tile) return;
      tile.classList.add('has-remote-peer');
      const peer = list[0];
      appendPeer(tile, peer, list.length);
    });
  }

  function appendPeer(tile, peer, count) {
    const direction = Math.max(0, Math.min(15, Number(peer.direction || 12)));
    const sprite = document.createElement('span');
    sprite.className = 'remote-peer-sprite';
    sprite.style.backgroundImage = `url(${PLAYER_SHEET})`;
    sprite.style.backgroundPosition = `-${direction * FRAME_WIDTH}px 0px`;
    sprite.title = `${peer.characterName || 'Remote'} · ${direction}/15`;
    tile.appendChild(sprite);

    const label = document.createElement('span');
    label.className = 'remote-peer-nameplate';
    label.textContent = peer.characterName || 'Remote';
    tile.appendChild(label);

    if (count > 1) {
      const badge = document.createElement('span');
      badge.className = 'remote-peer-count';
      badge.textContent = `+${count - 1}`;
      tile.appendChild(badge);
    }
  }

  window.addEventListener('ydh-realtime-peers-updated', renderRemotePeers);
  window.addEventListener('ydh-map-rendered', renderRemotePeers);
  document.addEventListener('DOMContentLoaded', renderRemotePeers);
})();
