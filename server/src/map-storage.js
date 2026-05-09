import { mkdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import * as mysqlStorage from './mysql-storage.js';

const DATA_DIR = process.env.YDH_DATA_DIR || path.resolve(process.cwd(), 'data');
const CUSTOM_MAP_FILE = path.join(DATA_DIR, 'custom-maps.json');
const MAX_CUSTOM_MAPS = Number(process.env.YDH_MAX_CUSTOM_MAPS || 100);
const GLOBAL_SCOPE = 'global';
const storageMode = (process.env.YDH_STORAGE || 'file').toLowerCase();
const useMysql = storageMode === 'mysql';

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
  const parsed = JSON.parse(raw || '{"maps":[]}');
  parsed.maps = (parsed.maps || []).map(normalizeRecord);
  return parsed;
}

async function writeStore(store) {
  await ensureStore();
  await writeFile(CUSTOM_MAP_FILE, JSON.stringify(store, null, 2), 'utf8');
}

function cleanScopeValue(value) {
  const text = String(value || '').trim();
  return text || GLOBAL_SCOPE;
}

function scopeFrom(input = {}) {
  return {
    accountId: cleanScopeValue(input.accountId),
    characterId: cleanScopeValue(input.characterId)
  };
}

function scopeKey(scope = {}) {
  const normalized = scopeFrom(scope);
  return `${normalized.accountId}::${normalized.characterId}`;
}

function recordKey(record) {
  return `${scopeKey(record)}::${record.id}`;
}

function normalizeRecord(record) {
  const scope = scopeFrom(record);
  return {
    ...record,
    accountId: scope.accountId,
    characterId: scope.characterId,
    scopeKey: scopeKey(scope),
    map: record.map
  };
}

function validationScope(payload = {}) {
  return scopeFrom({
    accountId: payload.accountId || payload.map?.accountId,
    characterId: payload.characterId || payload.map?.characterId
  });
}

function validateCustomMapPayload(payload) {
  const errors = [];
  const map = payload?.map || payload;
  if (!map || typeof map !== 'object') errors.push('map object is required');
  if (!map?.id) errors.push('map.id is required');
  if (!map?.name) errors.push('map.name is required');
  if (!Array.isArray(map?.rows) || !map.rows.length) errors.push('map.rows is required');
  if (map?.rows?.some((row) => typeof row !== 'string')) errors.push('map.rows must be string array');
  return { ok: errors.length === 0, errors, map, scope: validationScope(payload) };
}

function matchesScope(record, scope = {}) {
  const normalized = normalizeRecord(record);
  const wanted = scopeFrom(scope);
  return normalized.accountId === wanted.accountId && normalized.characterId === wanted.characterId;
}

function summary(record) {
  const normalized = normalizeRecord(record);
  return {
    id: normalized.id,
    name: normalized.name,
    accountId: normalized.accountId,
    characterId: normalized.characterId,
    scopeKey: normalized.scopeKey,
    source: normalized.source,
    sourceUrl: normalized.sourceUrl,
    width: normalized.width,
    height: normalized.height,
    savedAt: normalized.savedAt,
    updatedAt: normalized.updatedAt
  };
}

async function saveCustomMapFile(payload) {
  const validation = validateCustomMapPayload(payload);
  if (!validation.ok) {
    const error = new Error(validation.errors.join(', '));
    error.statusCode = 400;
    throw error;
  }

  const map = validation.map;
  const scope = validation.scope;
  const store = await readStore();
  const now = new Date().toISOString();
  const lookup = `${scopeKey(scope)}::${map.id}`;
  const existing = (store.maps || []).find((item) => recordKey(item) === lookup);
  const record = normalizeRecord({
    id: map.id,
    name: map.name,
    accountId: scope.accountId,
    characterId: scope.characterId,
    source: map.source || 'tiled-json',
    sourceUrl: map.sourceUrl || 'server-custom',
    width: map.rows?.[0]?.length || 0,
    height: map.rows?.length || 0,
    savedAt: existing?.savedAt || now,
    updatedAt: now,
    map: {
      ...map,
      accountId: scope.accountId,
      characterId: scope.characterId
    }
  });

  store.maps = [record, ...(store.maps || []).filter((item) => recordKey(item) !== lookup)].slice(0, MAX_CUSTOM_MAPS);
  await writeStore(store);
  return record;
}

async function listCustomMapsFile(scope = {}) {
  const store = await readStore();
  return (store.maps || []).filter((item) => matchesScope(item, scope)).map(summary);
}

async function getCustomMapFile(id, scope = {}) {
  const store = await readStore();
  return (store.maps || []).find((item) => item.id === id && matchesScope(item, scope)) || null;
}

async function deleteCustomMapFile(id, scope = {}) {
  const store = await readStore();
  const before = store.maps || [];
  const after = before.filter((item) => !(item.id === id && matchesScope(item, scope)));
  store.maps = after;
  await writeStore(store);
  return { deleted: before.length !== after.length, count: after.length };
}

async function customMapHealthFile() {
  const store = await readStore();
  const maps = store.maps || [];
  const scopes = Array.from(new Set(maps.map((item) => normalizeRecord(item).scopeKey)));
  return { storage: 'file', count: maps.length, max: MAX_CUSTOM_MAPS, scopes: scopes.length };
}

export async function saveCustomMap(payload) {
  if (useMysql && typeof mysqlStorage.saveCustomMap === 'function') return mysqlStorage.saveCustomMap(payload);
  return saveCustomMapFile(payload);
}

export async function listCustomMaps(scope = {}) {
  if (useMysql && typeof mysqlStorage.listCustomMaps === 'function') return mysqlStorage.listCustomMaps(scope);
  return listCustomMapsFile(scope);
}

export async function getCustomMap(id, scope = {}) {
  if (useMysql && typeof mysqlStorage.getCustomMap === 'function') return mysqlStorage.getCustomMap(id, scope);
  return getCustomMapFile(id, scope);
}

export async function deleteCustomMap(id, scope = {}) {
  if (useMysql && typeof mysqlStorage.deleteCustomMap === 'function') return mysqlStorage.deleteCustomMap(id, scope);
  return deleteCustomMapFile(id, scope);
}

export async function customMapHealth() {
  if (useMysql && typeof mysqlStorage.customMapHealth === 'function') return mysqlStorage.customMapHealth();
  return customMapHealthFile();
}

export const customMapScope = { GLOBAL_SCOPE, scopeFrom, scopeKey };
