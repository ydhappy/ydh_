(() => {
  'use strict';

  const saveKey = 'ydh-mobile-mmorpg-v1';
  const clientIdKey = 'ydh-mobile-mmorpg-client-id';
  const clientId = localStorage.getItem(clientIdKey) || `p_${Math.random().toString(36).slice(2, 10)}`;
  localStorage.setItem(clientIdKey, clientId);

  const peers = new Map();
  const state = { ws: null, connected: false, lastSend: 0, url: '', reconnectTimer: 0 };

  function readPlayer() {
    try {
      const saved = JSON.parse(localStorage.getItem(saveKey) || '{}');
      return {
        id: clientId,
        name: saved.name || 'YDH 모험가',
        classId: saved.classId || 'knight',
        lv: saved.lv || 1,
        x: Number(saved.x || 672),
        y: Number(saved.y || 672),
        hp: Number(saved.hp || 100),
        maxHp: Number(saved.maxHp || 100),
        zone: 'talking-island-outpost',
        t: Date.now()
      };
    } catch {
      return { id: clientId, name: 'YDH 모험가', classId: 'knight', lv: 1, x: 672, y: 672, zone: 'talking-island-outpost', t: Date.now() };
    }
  }

  function wsUrl() {
    const override = localStorage.getItem('ydh-mmo-ws-url');
    if (override) return override;
    if (location.protocol === 'file:') return 'ws://127.0.0.1:8787/mmo';
    const proto = location.protocol === 'https:' ? 'wss:' : 'ws:';
    return `${proto}//${location.host}/mmo`;
  }

  function send(type, payload = {}) {
    if (!state.ws || state.ws.readyState !== WebSocket.OPEN) return false;
    state.ws.send(JSON.stringify({ type, payload, clientId, at: Date.now() }));
    return true;
  }

  function connect() {
    clearTimeout(state.reconnectTimer);
    state.url = wsUrl();
    try {
      state.ws = new WebSocket(state.url);
    } catch {
      scheduleReconnect();
      renderStatus();
      return;
    }
    state.ws.addEventListener('open', () => {
      state.connected = true;
      send('hello', readPlayer());
      logNet('서버 연결 완료');
      renderStatus();
    });
    state.ws.addEventListener('message', (event) => {
      let msg;
      try { msg = JSON.parse(event.data); } catch { return; }
      if (msg.type === 'peers') {
        peers.clear();
        for (const peer of msg.payload || []) if (peer.id !== clientId) peers.set(peer.id, peer);
      }
      if (msg.type === 'player') {
        const peer = msg.payload;
        if (peer && peer.id !== clientId) peers.set(peer.id, peer);
      }
      if (msg.type === 'leave') peers.delete(msg.payload?.id);
      if (msg.type === 'chat') logChat(`${msg.payload?.name || 'player'}: ${msg.payload?.text || ''}`);
      renderPeers();
      renderStatus();
    });
    state.ws.addEventListener('close', () => {
      state.connected = false;
      renderStatus();
      scheduleReconnect();
    });
    state.ws.addEventListener('error', () => {
      state.connected = false;
      renderStatus();
    });
  }

  function scheduleReconnect() {
    clearTimeout(state.reconnectTimer);
    state.reconnectTimer = setTimeout(connect, 3000);
  }

  function heartbeat() {
    const player = readPlayer();
    send('player', player);
    for (const [id, peer] of peers) if (Date.now() - Number(peer.t || 0) > 15000) peers.delete(id);
    renderPeers();
  }

  function ensurePanel() {
    if (document.getElementById('mmoNetPanel')) return;
    const panel = document.createElement('section');
    panel.id = 'mmoNetPanel';
    panel.className = 'mmo-net-panel';
    panel.innerHTML = `
      <div><b id="mmoNetState">OFFLINE</b><span id="mmoPeerCount">0명</span></div>
      <form id="mmoChatForm"><input id="mmoChatInput" maxlength="40" placeholder="채팅 입력" /><button>전송</button></form>
      <div id="mmoPeerLayer"></div>
    `;
    document.getElementById('app')?.appendChild(panel);
    document.getElementById('mmoChatForm')?.addEventListener('submit', (event) => {
      event.preventDefault();
      const input = document.getElementById('mmoChatInput');
      const text = input.value.trim();
      if (!text) return;
      const player = readPlayer();
      if (send('chat', { name: player.name, text })) logChat(`나: ${text}`);
      else logChat(`오프라인: ${text}`);
      input.value = '';
    });
  }

  function renderStatus() {
    ensurePanel();
    const status = document.getElementById('mmoNetState');
    const count = document.getElementById('mmoPeerCount');
    if (status) {
      status.textContent = state.connected ? 'ONLINE' : 'OFFLINE';
      status.className = state.connected ? 'online' : 'offline';
    }
    if (count) count.textContent = `${peers.size + 1}명`;
  }

  function renderPeers() {
    ensurePanel();
    const layer = document.getElementById('mmoPeerLayer');
    if (!layer) return;
    layer.innerHTML = '';
    const list = Array.from(peers.values()).slice(0, 12);
    for (const peer of list) {
      const item = document.createElement('span');
      item.className = `mmo-peer class-${peer.classId || 'knight'}`;
      item.textContent = `${peer.name || 'player'} Lv.${peer.lv || 1}`;
      layer.appendChild(item);
    }
  }

  function logChat(text) {
    const chat = document.getElementById('chatLog');
    if (!chat) return;
    const p = document.createElement('p');
    p.textContent = text;
    chat.appendChild(p);
    while (chat.children.length > 10) chat.removeChild(chat.firstChild);
  }

  function logNet(text) { logChat(`[네트워크] ${text}`); }

  function boot() {
    ensurePanel();
    renderStatus();
    connect();
    setInterval(heartbeat, 700);
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();

  window.YDH_MMO_NET = { connect, send, peers, readPlayer };
})();
