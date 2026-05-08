import * as fileStorage from './storage.js';
import * as mysqlStorage from './mysql-storage.js';

const mode = (process.env.YDH_STORAGE || 'file').toLowerCase();
const provider = mode === 'mysql' ? mysqlStorage : fileStorage;

export const storageMode = mode === 'mysql' ? 'mysql' : 'file';

export async function saveSnapshot(snapshot) {
  return provider.saveSnapshot(snapshot);
}

export async function listSnapshots() {
  return provider.listSnapshots();
}

export async function latestSnapshot() {
  return provider.latestSnapshot();
}

export async function snapshotById(id) {
  if (typeof provider.snapshotById !== 'function') return null;
  return provider.snapshotById(id);
}

export async function storageHealth() {
  if (typeof provider.health === 'function') return provider.health();
  return { storage: storageMode };
}
