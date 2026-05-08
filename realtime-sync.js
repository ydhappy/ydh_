(() => {
  'use strict';

  const peers = new Map();
  let socket = null;
  let clientId = '';
  let status = 'off';
  let logLines = [];

  function readJson(key, fallback = null) {
    try {
      return JSON.parse(localStorage.getItem(key) || 'null') ?? fallback;
    } catch {
      return fallback;
    }
  }

  function schemaKeys() {
    return window.YDH_SAVE_SCHEMA?.localStorageKeys || {};
  }

  function mapState() {
    const keys = schemaKeys();
    return readJson(keys.map || 'ydh-chronicle-map-v1', {}) || {};
  }

  function accountState() {
    const keys = schemaKeys();
    return readJson(keys.accountProfile || 'ydh-account-profile-v1', {}) || {};
  }

  function selectedCharacter() {
    const keys = schemaKeys();
    return readJson(keys.selectedCharacter || 'ydh-selected-character-v1', {}) || {};
  }

  function currentPayload(extra = {}) {
    const account = accountState();
    const character = selectedCharacter();
    const map = mapState();
    return {
      accountId: account.accountId || 'local',
      accountName: account.displayName || 'YDH Player',
      characterId: character.characterId || 'local-character',
      characterName: character.name || document.getElementById('playerName')?.textContent || '검은 기사',
      classId: character.classId || 'knight',
      mapIndex: Number(map.mapIndex ?? 0),
      x: Number(map.x ?? 0),
      y: Number(map.y ?? 0),
      direction: Number(map.direction ?? 12),
      ...extra
    };
  }

  function wsUrl() {
    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
    return `${protocol}//${location.host}/ws/position`;
  }

  function addLog(message) {
    logLines = [`${new Date().toLocaleTimeString('ko-KR')} ${message}`, ...logLines].slice(0, 8);
    render();
  }

  function send(type, payload = {}) {
    if (!socket || socket.readyState !== WebSocket.OPEN) return false;
    socket.send(JSON.stringify({ type, payload }));
    return true;
  }

  function connect() {
    if (socket && [WebSocket.OPEN, WebSocket.CONNECTING].includes(socket.readyState)) return;
    status = 'connecting';
    render();
    try {
      socket = new WebSocket(wsUrl());
    } catch (error) {
      status = 'error';
      addLog(`WebSocket 생성 실패: ${error.message}`);
      return;
    }

    socket.addEventListener('open', () => {
      status = 'connected';
      send('hello', currentPayload());
      addLog('실시간 위치 서버에 연결되었습니다.');
      render();
    });

    socket.addEventListener('message', (event) => {
      handleMessage(event.data);
    });

    socket.addEventListener('close', () => {
      status = 'off';
      addLog('실시간 위치 연결이 종료되었습니다.');
      render();
    });

    socket.addEventListener('error', () => {
      status = 'error';
      addLog('실시간 위치 연결 오류가 발생했습니다.');
      render();
    });
  }

  function disconnect() {
    if (socket) socket.close();
    socket = null;
    status = 'off';
    peers.clear();
    render();
  }

  function handleMessage(raw) {
    let message;
    try {
      message = JSON.parse(raw);
    } catch {
      addLog('잘못된 실시간 메시지를 수신했습니다.');
      return;
    }

    if (message.type === 'connected') {
      clientId = message.payload?.clientId || clientId;
      return;
    }

    if (message.type === 'welcome') {
      clientId = message.payload?.clientId || clientId;
      peers.clear();
      (message.payload?.peers || []).forEach((peer) => peers.set(peer.clientId, peer));
      addLog(`기존 접속자 ${peers.size}명 동기화`);
      render();
      return;
    }

    if (message.type === 'peer-joined' || message.type === 'peer-position') {
      const peer = message.payload;
      if (peer?.clientId) peers.set(peer.clientId, peer);
      render();
      return;
    }

    if (message.type === 'peer-left') {
      const id = message.payload?.clientId;
      if (id) peers.delete(id);
      render();
      return;
    }

    if (message.type === 'error') {
      addLog(`서버 오류: ${message.error}`);
    }
  }

  function broadcastPosition(eventDetail = {}) {
    const to = eventDetail.to || {};
    send('position', currentPayload({
      mapIndex: Number(to.mapIndex ?? mapState().mapIndex ?? 0),
      x: Number(to.x ?? mapState().x ?? 0),
      y: Number(to.y ?? mapState().y ?? 0),
      direction: Number(eventDetail.direction ?? mapState().direction ?? 12)
    }));
  }

  function render() {
    const root = document.getElementById('realtimeSyncRoot');
    if (!root) return;
    const on = status === 'connected';
    root.innerHTML = `
      <div class="realtime-head">
        <div>
          <p class="eyebrow">REALTIME POSITION</p>
          <h2>WebSocket 위치 동기화</h2>
          <p>같은 서버에 접속한 다른 플레이어의 현재 위치를 실시간 메시지로 수신합니다.</p>
        </div>
        <div class="realtime-badge ${on ? '' : 'off'}">${status}<br />접속자 ${peers.size}</div>
      </div>
      <div class="realtime-actions">
        <button type="button" id="connectRealtime">연결</button>
        <button type="button" id="sendRealtimePosition">현재 위치 송신</button>
        <button type="button" id="disconnectRealtime">해제</button>
      </div>
      <div class="realtime-peers">
        ${renderPeers()}
      </div>
      <pre class="realtime-log">${logLines.join('\n')}</pre>
    `;
    document.getElementById('connectRealtime')?.addEventListener('click', connect);
    document.getElementById('sendRealtimePosition')?.addEventListener('click', () => {
      if (send('position', currentPayload())) addLog('현재 위치를 송신했습니다.');
      else addLog('WebSocket 연결 후 송신할 수 있습니다.');
    });
    document.getElementById('disconnectRealtime')?.addEventListener('click', disconnect);
  }

  function renderPeers() {
    if (!peers.size) return '<article class="realtime-peer-card"><strong>대기 중</strong><small>다른 접속자가 아직 없습니다.</small></article>';
    return [...peers.values()].map((peer) => `
      <article class="realtime-peer-card">
        <strong>${escapeHtml(peer.characterName || 'Unknown')}</strong>
        <small>${escapeHtml(peer.accountName || 'YDH Player')} · ${escapeHtml(peer.classId || 'knight')}</small>
        <small>map ${peer.mapIndex ?? 0} / X:${peer.x ?? 0} Y:${peer.y ?? 0} / DIR:${peer.direction ?? 12}</small>
      </article>
    `).join('');
  }

  function escapeHtml(value) {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function create() {
    const section = document.createElement('section');
    section.className = 'panel realtime-panel';
    section.id = 'realtime';
    section.innerHTML = '<div id="realtimeSyncRoot"></div>';
    const serverSync = document.getElementById('server-sync');
    if (serverSync?.parentNode) serverSync.parentNode.insertBefore(section, serverSync.nextSibling);
    else document.querySelector('main')?.appendChild(section);
    render();
    window.addEventListener('ydh-player-moved', (event) => broadcastPosition(event.detail || {}));
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', create);
  else create();
})();
