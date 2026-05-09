import * as fileSessions from './auth-sessions-file.js';
import * as mysqlSessions from './mysql-auth-sessions.js';

const storageMode = String(process.env.YDH_STORAGE || 'file').toLowerCase();
const provider = storageMode === 'mysql' ? mysqlSessions : fileSessions;

export const refreshConfig = (...args) => provider.refreshConfig(...args);
export const createRefreshSession = (...args) => provider.createRefreshSession(...args);
export const rotateRefreshSession = (...args) => provider.rotateRefreshSession(...args);
export const revokeRefreshSession = (...args) => provider.revokeRefreshSession(...args);
export const sessionHealth = (...args) => provider.sessionHealth(...args);
export const listPublicSessions = (...args) => provider.listPublicSessions(...args);
