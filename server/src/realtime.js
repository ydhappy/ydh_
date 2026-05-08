import { WebSocketServer } from 'ws';
import { randomUUID } from 'node:crypto';

const peers = new Map();

function safeSend(ws, payload) {
  if (ws.readyState !== ws.OPEN) return;
  ws.send(JSON.stringify(payload));
}

function publicPeer(peer) {
  return {
    clientId: peer.clientId,
    accountId: peer.accountId,
    accountName: peer.accountName,
    characterId: peer.characterId,
    characterName: peer.characterName,
    classId: peer.classId,
    mapIndex: peer.mapIndex,
    x: peer.x,
    y: peer.y,
    direction: peer.direction,
    updatedAt: peer.updatedAt
  };
}

function broadcast(payload, exceptClientId = '') {
  for (const peer of peers.values()) {
    if (exceptClientId && peer.clientId === exceptClientId) continue;
    safeSend(peer.ws, payload);
  }
}

function updatePeer(peer, data = {}) {
  peer.accountId = data.accountId || peer.accountId || 'local';
  peer.accountName = data.accountName || peer.accountName || 'YDH Player';
  peer.characterId = data.characterId || peer.characterId || peer.clientId;
  peer.characterName = data.characterName || peer.characterName || '검은 기사';
  peer.classId = data.classId || peer.classId || 'knight';
  peer.mapIndex = Number.isFinite(Number(data.mapIndex)) ? Number(data.mapIndex) : (peer.mapIndex ?? 0);
  peer.x = Number.isFinite(Number(data.x)) ? Number(data.x) : (peer.x ?? 0);
  peer.y = Number.isFinite(Number(data.y)) ? Number(data.y) : (peer.y ?? 0);
  peer.direction = Number.isFinite(Number(data.direction)) ? Number(data.direction) : (peer.direction ?? 12);
  peer.updatedAt = new Date().toISOString();
  return peer;
}

function handleMessage(peer, raw) {
  let message;
  try {
    message = JSON.parse(raw.toString());
  } catch {
    safeSend(peer.ws, { type: 'error', error: 'Invalid JSON message' });
    return;
  }

  if (message.type === 'hello') {
    updatePeer(peer, message.payload || {});
    safeSend(peer.ws, {
      type: 'welcome',
      payload: {
        clientId: peer.clientId,
        peers: [...peers.values()].filter((item) => item.clientId !== peer.clientId).map(publicPeer)
      }
    });
    broadcast({ type: 'peer-joined', payload: publicPeer(peer) }, peer.clientId);
    return;
  }

  if (message.type === 'position') {
    updatePeer(peer, message.payload || {});
    broadcast({ type: 'peer-position', payload: publicPeer(peer) }, peer.clientId);
    return;
  }

  if (message.type === 'ping') {
    safeSend(peer.ws, { type: 'pong', payload: { now: new Date().toISOString() } });
    return;
  }

  safeSend(peer.ws, { type: 'error', error: `Unsupported message type: ${message.type || 'unknown'}` });
}

export function attachRealtimeServer(httpServer) {
  const wss = new WebSocketServer({ server: httpServer, path: '/ws/position' });

  wss.on('connection', (ws) => {
    const clientId = randomUUID();
    const peer = updatePeer({ ws, clientId }, {});
    peers.set(clientId, peer);

    safeSend(ws, { type: 'connected', payload: { clientId, path: '/ws/position' } });

    ws.on('message', (raw) => handleMessage(peer, raw));
    ws.on('close', () => {
      peers.delete(clientId);
      broadcast({ type: 'peer-left', payload: { clientId } });
    });
    ws.on('error', () => {
      peers.delete(clientId);
      broadcast({ type: 'peer-left', payload: { clientId } });
    });
  });

  return {
    wss,
    stats() {
      return {
        clients: peers.size,
        peers: [...peers.values()].map(publicPeer)
      };
    }
  };
}
