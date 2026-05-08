import { mkdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';

const DATA_DIR = process.env.YDH_DATA_DIR || path.resolve(process.cwd(), 'data');
const SAVE_FILE = path.join(DATA_DIR, 'saves.json');

async function ensureStore() {
  await mkdir(DATA_DIR, { recursive: true });
  try {
    await readFile(SAVE_FILE, 'utf8');
  } catch {
    await writeFile(SAVE_FILE, JSON.stringify({ snapshots: [] }, null, 2), 'utf8');
  }
}

export async function readStore() {
  await ensureStore();
  const raw = await readFile(SAVE_FILE, 'utf8');
  return JSON.parse(raw || '{"snapshots":[]}');
}

export async function writeStore(store) {
  await ensureStore();
  await writeFile(SAVE_FILE, JSON.stringify(store, null, 2), 'utf8');
}

export async function saveSnapshot(snapshot) {
  const store = await readStore();
  const selected = snapshot.selectedCharacter || {};
  const character = snapshot.saves?.character || {};
  const record = {
    id: `save_${Date.now()}_${Math.random().toString(16).slice(2)}`,
    receivedAt: new Date().toISOString(),
    schemaVersion: snapshot.schemaVersion,
    accountId: snapshot.account?.accountId || selected.accountId || character.accountId || 'local',
    accountName: snapshot.account?.displayName || 'YDH Player',
    characterId: selected.characterId || character.characterId || 'default',
    characterName: selected.name || character.name || '검은 기사',
    classId: selected.classId || character.classId || 'knight',
    slotCount: Array.isArray(snapshot.characterSlots) ? snapshot.characterSlots.length : 0,
    level: character.level || 1,
    mapIndex: snapshot.saves?.map?.mapIndex ?? 0,
    snapshot
  };
  store.snapshots = [record, ...(store.snapshots || [])].slice(0, 50);
  await writeStore(store);
  return record;
}

export async function listSnapshots() {
  const store = await readStore();
  return (store.snapshots || []).map(({ snapshot, ...summary }) => summary);
}

export async function latestSnapshot() {
  const store = await readStore();
  return store.snapshots?.[0] || null;
}
