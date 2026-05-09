import crypto from 'node:crypto';
import { mkdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';

const DATA_DIR = process.env.YDH_DATA_DIR || path.resolve(process.cwd(), 'data');
const SESSION_FILE = path.join(DATA_DIR, 'auth-sessions.json');
const REFRESH_TTL_SECONDS = Number(process.env.YDH_AUTH_REFRESH_TTL_SECONDS || 30 * 24 * 60 * 60);
const MAX_SESSIONS = Number(process.env.YDH_AUTH_MAX_SESSIONS || 2000);

async function ensureStore() {
  await mkdir(DATA_DIR, { recursive: true });
  try {
    await readFile(SESSION_FILE, 'utf8');
  } catch {
    await writeFile(SESSION_FILE, JSON.stringify({ sessions: [] }, null, 2), 'utf8');
  }
}

async function readStore() {
  await ensureStore();
  const raw = await readFile(SESSION_FILE, 'utf8');
  const parsed = JSON.parse(raw || '{"sessions":[]}');
  parsed.sessions = Array.isArray(parsed.sessions) ? parsed.sessions : [];
  return parsed;
}

async function writeStore(store) {
  await ensureStore();
  await writeFile(SESSION_FILE, JSON.stringify(store, null, 2), 'utf8');
}

function nowMs() {
  return Date.now();
}

function iso(value = Date.now()) {
  return new Date(value).toISOString();
}

function sha256(value) {
  return crypto.createHash('sha256').update(String(value)).digest('hex');
}

function randomToken() {
  return crypto.randomBytes(32).toString('base64url');
}

function cleanExpired(sessions) {
  const now = nowMs();
  return sessions.filter((session) => !session.revokedAt && Number(session.expiresAtMs || 0) > now);
}

function publicSession(session) {
  if (!session) return null;
  return {
    sessionId: session.sessionId,
    accountId: session.accountId,
    displayName: session.displayName,
    roles: session.roles || ['player'],
    createdAt: session.createdAt,
    lastUsedAt: session.lastUsedAt,
    expiresAt: session.expiresAt,
    revokedAt: session.revokedAt || null
  };
}

export function refreshConfig() {
  return {
    storage: 'file',
    refreshTtlSeconds: REFRESH_TTL_SECONDS,
    maxSessions: MAX_SESSIONS
  };
}

export async function createRefreshSession(input = {}) {
  const store = await readStore();
  const refreshToken = randomToken();
  const sessionId = `sess_${crypto.randomBytes(12).toString('hex')}`;
  const createdAtMs = nowMs();
  const expiresAtMs = createdAtMs + REFRESH_TTL_SECONDS * 1000;
  const session = {
    sessionId,
    tokenHash: sha256(refreshToken),
    accountId: input.accountId || 'local',
    displayName: input.displayName || 'YDH Player',
    roles: Array.isArray(input.roles) && input.roles.length ? input.roles : ['player'],
    createdAtMs,
    expiresAtMs,
    createdAt: iso(createdAtMs),
    lastUsedAt: iso(createdAtMs),
    expiresAt: iso(expiresAtMs),
    userAgent: input.userAgent || '',
    ip: input.ip || ''
  };
  const alive = cleanExpired(store.sessions || []);
  store.sessions = [session, ...alive].slice(0, MAX_SESSIONS);
  await writeStore(store);
  return { refreshToken, session: publicSession(session) };
}

export async function rotateRefreshSession(refreshToken, input = {}) {
  if (!refreshToken) return null;
  const hash = sha256(refreshToken);
  const store = await readStore();
  const alive = cleanExpired(store.sessions || []);
  const index = alive.findIndex((session) => session.tokenHash === hash);
  if (index < 0) {
    store.sessions = alive;
    await writeStore(store);
    return null;
  }

  const existing = alive[index];
  const nextToken = randomToken();
  const usedAtMs = nowMs();
  const rotated = {
    ...existing,
    tokenHash: sha256(nextToken),
    lastUsedAt: iso(usedAtMs),
    userAgent: input.userAgent || existing.userAgent || '',
    ip: input.ip || existing.ip || ''
  };
  alive[index] = rotated;
  store.sessions = alive.slice(0, MAX_SESSIONS);
  await writeStore(store);
  return { refreshToken: nextToken, session: publicSession(rotated) };
}

export async function revokeRefreshSession(refreshToken) {
  if (!refreshToken) return { revoked: false };
  const hash = sha256(refreshToken);
  const store = await readStore();
  let revoked = false;
  const now = iso();
  store.sessions = (store.sessions || []).map((session) => {
    if (session.tokenHash !== hash || session.revokedAt) return session;
    revoked = true;
    return { ...session, revokedAt: now };
  });
  await writeStore(store);
  return { revoked };
}

export async function sessionHealth() {
  const store = await readStore();
  const sessions = store.sessions || [];
  const active = cleanExpired(sessions);
  return {
    storage: 'file',
    count: sessions.length,
    active: active.length,
    revoked: sessions.filter((session) => session.revokedAt).length,
    max: MAX_SESSIONS,
    refreshTtlSeconds: REFRESH_TTL_SECONDS
  };
}

export async function listPublicSessions(accountId = '') {
  const store = await readStore();
  return cleanExpired(store.sessions || [])
    .filter((session) => !accountId || session.accountId === accountId)
    .map(publicSession);
}
