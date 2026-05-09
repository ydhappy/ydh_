import * as fileSessions from './auth-sessions.js';
import * as mysqlSessions from './mysql-auth-sessions.js';

const STORAGE_MODE = String(process.env.YDH_STORAGE || 'file').toLowerCase();
const provider = STORAGE_MODE === 'mysql' ? mysqlSessions : fileSessions;

export function refreshConfig() {
  return provider.refreshConfig();
}

export async function createRefreshSession(input = {}) {
  return provider.createRefreshSession(input);
}

export async function rotateRefreshSession(refreshValue, input = {}) {
  return provider.rotateRefreshSession(refreshValue, input);
}

export async function revokeRefreshSession(refreshValue) {
  return provider.revokeRefreshSession(refreshValue);
}

export async function sessionHealth() {
  return provider.sessionHealth();
}

export async function listPublicSessions(accountId = '') {
  return provider.listPublicSessions(accountId);
}
