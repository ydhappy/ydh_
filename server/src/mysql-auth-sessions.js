import crypto from 'node:crypto';
import mysql from 'mysql2/promise';

let pool;
let initialized = false;

const REFRESH_TTL_SECONDS = Number(process.env.YDH_AUTH_REFRESH_TTL_SECONDS || 30 * 24 * 60 * 60);
const MAX_SESSIONS = Number(process.env.YDH_AUTH_MAX_SESSIONS || 2000);

function mysqlConfig() {
  return {
    host: process.env.MYSQL_HOST || '127.0.0.1',
    port: Number(process.env.MYSQL_PORT || 3306),
    user: process.env.MYSQL_USER || 'root',
    password: process.env.MYSQL_PASSWORD || '',
    database: process.env.MYSQL_DATABASE || 'ydh_chronicle',
    waitForConnections: true,
    connectionLimit: Number(process.env.MYSQL_CONNECTION_LIMIT || 5),
    queueLimit: 0,
    charset: process.env.MYSQL_CHARSET || 'UTF8_GENERAL_CI',
    timezone: 'Z'
  };
}

function connection() {
  if (!pool) pool = mysql.createPool(mysqlConfig());
  return pool;
}

function toMysqlDate(value = new Date()) {
  const date = value instanceof Date ? value : new Date(value);
  return date.toISOString().slice(0, 19).replace('T', ' ');
}

function toIso(value) {
  if (!value) return null;
  return value instanceof Date ? value.toISOString() : new Date(value).toISOString();
}

function sha256(value) {
  return crypto.createHash('sha256').update(String(value)).digest('hex');
}

function randomToken() {
  return crypto.randomBytes(32).toString('base64url');
}

function rolesJson(roles) {
  return JSON.stringify(Array.isArray(roles) && roles.length ? roles : ['player']);
}

function parseRoles(value) {
  try {
    const parsed = JSON.parse(value || '[]');
    return Array.isArray(parsed) && parsed.length ? parsed : ['player'];
  } catch {
    return ['player'];
  }
}

async function initMysqlAuthSessions() {
  if (initialized) return;
  const db = connection();
  await db.query(`
    CREATE TABLE IF NOT EXISTS ydh_auth_sessions (
      session_id VARCHAR(64) NOT NULL,
      token_hash CHAR(64) NOT NULL,
      account_id VARCHAR(80) NOT NULL DEFAULT 'local',
      display_name VARCHAR(80) NOT NULL DEFAULT 'YDH Player',
      roles_json LONGTEXT NOT NULL,
      created_at DATETIME NOT NULL,
      last_used_at DATETIME NOT NULL,
      expires_at DATETIME NOT NULL,
      revoked_at DATETIME DEFAULT NULL,
      user_agent VARCHAR(255) DEFAULT '',
      ip VARCHAR(80) DEFAULT '',
      PRIMARY KEY (session_id),
      UNIQUE KEY uk_auth_session_token_hash (token_hash),
      KEY idx_auth_session_account (account_id),
      KEY idx_auth_session_expires (expires_at),
      KEY idx_auth_session_revoked (revoked_at),
      KEY idx_auth_session_last_used (last_used_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8
  `);
  await db.query(`
    CREATE TABLE IF NOT EXISTS ydh_schema_meta (
      meta_key VARCHAR(64) NOT NULL,
      meta_value VARCHAR(255) NOT NULL,
      updated_at DATETIME NOT NULL,
      PRIMARY KEY (meta_key)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8
  `);
  await db.query(`
    INSERT INTO ydh_schema_meta (meta_key, meta_value, updated_at)
    VALUES ('schema_version', '4', NOW())
    ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value), updated_at = NOW()
  `);
  initialized = true;
}

function publicSession(row) {
  if (!row) return null;
  return {
    sessionId: row.session_id || row.sessionId,
    accountId: row.account_id || row.accountId,
    displayName: row.display_name || row.displayName,
    roles: parseRoles(row.roles_json || row.rolesJson),
    createdAt: toIso(row.created_at || row.createdAt),
    lastUsedAt: toIso(row.last_used_at || row.lastUsedAt),
    expiresAt: toIso(row.expires_at || row.expiresAt),
    revokedAt: row.revoked_at ? toIso(row.revoked_at) : null
  };
}

async function cleanup(db) {
  await db.query('DELETE FROM ydh_auth_sessions WHERE revoked_at IS NOT NULL OR expires_at <= NOW()');
  const [rows] = await db.query(
    `SELECT session_id
       FROM ydh_auth_sessions
      ORDER BY created_at DESC
      LIMIT 100000 OFFSET ?`,
    [MAX_SESSIONS]
  );
  if (rows.length) {
    await db.query('DELETE FROM ydh_auth_sessions WHERE session_id IN (?)', [rows.map((row) => row.session_id)]);
  }
}

export function refreshConfig() {
  return {
    storage: 'mysql',
    refreshTtlSeconds: REFRESH_TTL_SECONDS,
    maxSessions: MAX_SESSIONS
  };
}

export async function createRefreshSession(input = {}) {
  await initMysqlAuthSessions();
  const db = connection();
  await cleanup(db);

  const refreshToken = randomToken();
  const sessionId = `sess_${crypto.randomBytes(12).toString('hex')}`;
  const createdAt = toMysqlDate(new Date());
  const expiresAt = toMysqlDate(new Date(Date.now() + REFRESH_TTL_SECONDS * 1000));

  await db.query(
    `INSERT INTO ydh_auth_sessions (
      session_id, token_hash, account_id, display_name, roles_json,
      created_at, last_used_at, expires_at, user_agent, ip
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [
      sessionId,
      sha256(refreshToken),
      input.accountId || 'local',
      input.displayName || 'YDH Player',
      rolesJson(input.roles),
      createdAt,
      createdAt,
      expiresAt,
      String(input.userAgent || '').slice(0, 255),
      String(input.ip || '').slice(0, 80)
    ]
  );

  const [rows] = await db.query('SELECT * FROM ydh_auth_sessions WHERE session_id = ? LIMIT 1', [sessionId]);
  return { refreshToken, session: publicSession(rows[0]) };
}

export async function rotateRefreshSession(refreshToken, input = {}) {
  if (!refreshToken) return null;
  await initMysqlAuthSessions();
  const db = connection();
  await cleanup(db);

  const hash = sha256(refreshToken);
  const [rows] = await db.query(
    `SELECT *
       FROM ydh_auth_sessions
      WHERE token_hash = ? AND revoked_at IS NULL AND expires_at > NOW()
      LIMIT 1`,
    [hash]
  );
  if (!rows.length) return null;

  const nextToken = randomToken();
  const lastUsedAt = toMysqlDate(new Date());
  await db.query(
    `UPDATE ydh_auth_sessions
        SET token_hash = ?, last_used_at = ?, user_agent = ?, ip = ?
      WHERE session_id = ?`,
    [
      sha256(nextToken),
      lastUsedAt,
      String(input.userAgent || rows[0].user_agent || '').slice(0, 255),
      String(input.ip || rows[0].ip || '').slice(0, 80),
      rows[0].session_id
    ]
  );

  const [updated] = await db.query('SELECT * FROM ydh_auth_sessions WHERE session_id = ? LIMIT 1', [rows[0].session_id]);
  return { refreshToken: nextToken, session: publicSession(updated[0]) };
}

export async function revokeRefreshSession(refreshToken) {
  if (!refreshToken) return { revoked: false };
  await initMysqlAuthSessions();
  const db = connection();
  const [result] = await db.query(
    `UPDATE ydh_auth_sessions
        SET revoked_at = NOW()
      WHERE token_hash = ? AND revoked_at IS NULL`,
    [sha256(refreshToken)]
  );
  return { revoked: result.affectedRows > 0 };
}

export async function sessionHealth() {
  await initMysqlAuthSessions();
  const db = connection();
  const [countRows] = await db.query('SELECT COUNT(*) AS count FROM ydh_auth_sessions');
  const [activeRows] = await db.query('SELECT COUNT(*) AS count FROM ydh_auth_sessions WHERE revoked_at IS NULL AND expires_at > NOW()');
  const [revokedRows] = await db.query('SELECT COUNT(*) AS count FROM ydh_auth_sessions WHERE revoked_at IS NOT NULL');
  return {
    storage: 'mysql',
    count: countRows[0]?.count || 0,
    active: activeRows[0]?.count || 0,
    revoked: revokedRows[0]?.count || 0,
    max: MAX_SESSIONS,
    refreshTtlSeconds: REFRESH_TTL_SECONDS
  };
}

export async function listPublicSessions(accountId = '') {
  await initMysqlAuthSessions();
  const db = connection();
  const params = [];
  let where = 'WHERE revoked_at IS NULL AND expires_at > NOW()';
  if (accountId) {
    where += ' AND account_id = ?';
    params.push(accountId);
  }
  const [rows] = await db.query(
    `SELECT *
       FROM ydh_auth_sessions
       ${where}
      ORDER BY last_used_at DESC
      LIMIT 200`,
    params
  );
  return rows.map(publicSession);
}
