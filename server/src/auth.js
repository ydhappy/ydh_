import crypto from 'node:crypto';

const AUTH_REQUIRED = String(process.env.YDH_AUTH_REQUIRED || 'false').toLowerCase() === 'true';
const TOKEN_TTL_SECONDS = Number(process.env.YDH_AUTH_TOKEN_TTL_SECONDS || 7 * 24 * 60 * 60);
const SHARED_SECRET = process.env.YDH_AUTH_SHARED_SECRET || '';
const SERVER_SECRET = process.env.YDH_AUTH_SECRET || process.env.YDH_AUTH_SHARED_SECRET || 'ydh-dev-secret-change-me';
const ISSUER = 'ydh-chronicle-api';

function base64url(input) {
  return Buffer.from(input).toString('base64url');
}

function fromBase64url(input) {
  return Buffer.from(input, 'base64url').toString('utf8');
}

function sign(text) {
  return crypto.createHmac('sha256', SERVER_SECRET).update(text).digest('base64url');
}

function safeEqual(a, b) {
  const aa = Buffer.from(String(a));
  const bb = Buffer.from(String(b));
  if (aa.length !== bb.length) return false;
  return crypto.timingSafeEqual(aa, bb);
}

function cleanId(value, fallback = '') {
  return String(value || fallback)
    .trim()
    .replace(/[^a-zA-Z0-9_\-:.@]/g, '')
    .slice(0, 120);
}

function cleanName(value, fallback = 'YDH Player') {
  return String(value || fallback).trim().slice(0, 80);
}

export function authStatus() {
  return {
    required: AUTH_REQUIRED,
    sharedSecretRequired: !!SHARED_SECRET,
    tokenTtlSeconds: TOKEN_TTL_SECONDS,
    issuer: ISSUER
  };
}

export function createAuthToken(input = {}) {
  const now = Math.floor(Date.now() / 1000);
  const payload = {
    iss: ISSUER,
    iat: now,
    exp: now + TOKEN_TTL_SECONDS,
    accountId: cleanId(input.accountId, 'local'),
    displayName: cleanName(input.displayName || input.accountName),
    roles: Array.isArray(input.roles) && input.roles.length ? input.roles : ['player']
  };
  const header = { alg: 'HS256', typ: 'YDH' };
  const encodedHeader = base64url(JSON.stringify(header));
  const encodedPayload = base64url(JSON.stringify(payload));
  const body = `${encodedHeader}.${encodedPayload}`;
  return `${body}.${sign(body)}`;
}

export function verifyAuthToken(token) {
  if (!token || typeof token !== 'string') return null;
  const parts = token.split('.');
  if (parts.length !== 3) return null;
  const body = `${parts[0]}.${parts[1]}`;
  const expected = sign(body);
  if (!safeEqual(expected, parts[2])) return null;
  try {
    const payload = JSON.parse(fromBase64url(parts[1]));
    const now = Math.floor(Date.now() / 1000);
    if (payload.iss !== ISSUER) return null;
    if (!payload.exp || payload.exp < now) return null;
    return payload;
  } catch {
    return null;
  }
}

export function tokenFromRequest(req) {
  const header = req.get?.('authorization') || req.headers?.authorization || '';
  const match = String(header).match(/^Bearer\s+(.+)$/i);
  return match ? match[1].trim() : '';
}

export function optionalAuth(req, res, next) {
  const token = tokenFromRequest(req);
  req.auth = token ? verifyAuthToken(token) : null;
  next();
}

export function requireAuth(req, res, next) {
  const token = tokenFromRequest(req);
  const auth = token ? verifyAuthToken(token) : null;
  req.auth = auth;
  if (!AUTH_REQUIRED) return next();
  if (!auth) return res.status(401).json({ ok: false, error: 'Authentication required' });
  return next();
}

export function loginHandler(req, res) {
  const body = req.body || {};
  if (SHARED_SECRET && body.secret !== SHARED_SECRET) {
    return res.status(401).json({ ok: false, error: 'Invalid auth secret' });
  }
  const token = createAuthToken({
    accountId: body.accountId || body.account?.accountId || 'local',
    displayName: body.displayName || body.accountName || body.account?.displayName || 'YDH Player',
    roles: body.roles || ['player']
  });
  const auth = verifyAuthToken(token);
  return res.json({ ok: true, token, auth, status: authStatus() });
}

export function authScope(req, fallback = {}) {
  return {
    accountId: req.auth?.accountId || fallback.accountId || '',
    characterId: fallback.characterId || ''
  };
}

export function applyAuthToSnapshot(req, snapshot) {
  if (!req.auth) return snapshot;
  const account = {
    ...(snapshot.account || {}),
    accountId: req.auth.accountId,
    displayName: req.auth.displayName || snapshot.account?.displayName || 'YDH Player',
    provider: 'server-auth'
  };
  const selectedCharacter = snapshot.selectedCharacter
    ? { ...snapshot.selectedCharacter, accountId: req.auth.accountId }
    : snapshot.selectedCharacter;
  const characterSlots = Array.isArray(snapshot.characterSlots)
    ? snapshot.characterSlots.map((slot) => ({ ...slot, accountId: req.auth.accountId }))
    : [];
  return { ...snapshot, account, selectedCharacter, characterSlots };
}

export const authConfig = authStatus();
