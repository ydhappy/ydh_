import { mkdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';

const DATA_DIR = process.env.YDH_DATA_DIR || path.resolve(process.cwd(), 'data');
const CUSTOM_MAP_FILE = path.join(DATA_DIR, 'custom-maps.json');
const MAX_CUSTOM_MAPS = Number(process.env.YDH_MAX_CUSTOM_MAPS || 100);

async function ensureStore() {
  await mkdir(DATA_DIR, { recursive: true });
  try {
    await readFile(CUSTOM_MAP_FILE, 'utf8');
  } catch {
    await writeFile(CUSTOM_MAP_FILE, JSON.stringify({ maps: [] }, null, 2), 'utf8');
  }
}

async function readStore() {
  await ensureStore();
  const raw = await readFile(CUSTOM_MAP_FILE, 'utf8');
  return JSON.parse(raw || '{"maps":[]}');
}

async function writeStore(store) {
  await ensureStore();
  await writeFile(CUSTOM_MAP_FILE, JSON.stringify(store, null, 2), 'utf8');
}

function validateCustomMapPayload(payload) {
  const errors = [];
  const map = payload?.map || payload;
  if (!map || typeof map !== 'object') errors.push('map object is required');
  if (!map?.id) errors.push('map.id is required');
  if (!map?.name) errors.push('map.name is required');
  if (!Array.isArray(map?.rows) || !map.rows.length) errors.push('map.rows is required');
  if (map?.rows?.some((row) => typeof row !== 'string')) errors.push('map.rows must be string array');
  return { ok: errors.length === 0, errors, map };
}

function summary(record) {
  return {
    id: record.id,
    name: record.name,
    source: record.source,
    sourceUrl: record.sourceUrl,
    width: record.width,
    height: record.height,
    savedAt: record.savedAt,
    updatedAt: record.updatedAt
  };
}

export async function saveCustomMap(payload) {
  const validation = validateCustomMapPayload(payload);
  if (!validation.ok) {
    const error = new Error(validation.errors.join(', '));
    error.statusCode = 400;
    throw error;
  }

  const map = validation.map;
  const store = await readStore();
  const now = new Date().toISOString();
  const existing = (store.maps || []).find((item) => item.id === map.id);
  const record = {
    id: map.id,
    name: map.name,
    source: map.source || 'tiled-json',
    sourceUrl: map.sourceUrl || 'server-custom',
    width: map.rows?.[0]?.length || 0,
    height: map.rows?.length || 0,
    savedAt: existing?.savedAt || now,
    updatedAt: now,
    map
  };

  store.maps = [record, ...(store.maps || []).filter((item) => item.id !== map.id)].slice(0, MAX_CUSTOM_MAPS);
  await writeStore(store);
  return record;
}

export async function listCustomMaps() {
  const store = await readStore();
  return (store.maps || []).map(summary);
}

export async function getCustomMap(id) {
  const store = await readStore();
  return (store.maps || []).find((item) => item.id === id) || null;
}

export async function deleteCustomMap(id) {
  const store = await readStore();
  const before = store.maps || [];
  const after = before.filter((item) => item.id !== id);
  store.maps = after;
  await writeStore(store);
  return { deleted: before.length !== after.length, count: after.length };
}

export async function customMapHealth() {
  const store = await readStore();
  return { count: store.maps?.length || 0, max: MAX_CUSTOM_MAPS };
}
