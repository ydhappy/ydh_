import { WebSocketServer } from 'ws';

const clients = new Map();

function safeJson(value) {
  try { return JSON.parse(value); } catch { return null; }
}

function publicPeer(client) {
  return {
    id: client.id,
    name: client.name || 'YDH 모험가',
    classId: client.classId || 'knight',
    lv: client.lv || 1,
    x: Number(client.x || 0),
    y: Number(client.y || 0),
    hp: Number(client.hp || 0),
    maxHp: Number(client.maxHp || 0),
    zone: client.zone || 'talking-island-outpost',
    t: client.t || Date.now()
  };
}

function send(ws, type, payload) {
  if (ws.readyState !== ws.OPEN) return;
  ws.send(JSON.stringify({ type, payload, at: Date.now() }));
}

function broadcast(type, payload, exceptWs = null) {
  for (const client of clients.values()) {
    if (client.ws === exceptWs) continue;
    send(client.ws, type, payload);
  }
}

function upsertClient(ws, payload = {}) {
  const id = String(payload.id || payload.clientId || `guest_${Math.random().toString(36).slice(2, 10)}`);
  const existing = clients.get(id) || { id, ws };
  Object.assign(existing, payload, { id, ws, t: Date.now() });
  clients.set(id, existing);
  ws.__ydhClientId = id;
  return existing;
}

function activePeers() {
  const now = Date.now();
  for (const [id, client] of clients) {
    if (now - Number(client.t || 0) > 30000) clients.delete(id);
  }
  return Array.from(clients.values()).map(publicPeer);
}

export function attachMobileMmoGateway(server, options = {}) {
  const path = options.path || '/mmo';
  const wss = new WebSocketServer({ server, path });

  wss.on('connection', (ws) => {
    send(ws, 'peers', activePeers());

    ws.on('message', (raw) => {
      const msg = safeJson(String(raw));
      if (!msg || !msg.type) return;
      const payload = msg.payload || {};

      if (msg.type === 'hello' || msg.type === 'player') {
        const client = upsertClient(ws, payload);
        broadcast('player', publicPeer(client), ws);
        if (msg.type === 'hello') send(ws, 'peers', activePeers());
        return;
      }

      if (msg.type === 'chat') {
        const id = ws.__ydhClientId;
        const client = clients.get(id) || { name: 'YDH 모험가' };
        const text = String(payload.text || '').slice(0, 80);
        if (!text) return;
        broadcast('chat', { id, name: client.name || payload.name || 'YDH 모험가', text });
        return;
      }
    });

    ws.on('close', () => {
      const id = ws.__ydhClientId;
      if (id) {
        clients.delete(id);
        broadcast('leave', { id });
      }
    });
  });

  return { wss, clients, activePeers };
}
