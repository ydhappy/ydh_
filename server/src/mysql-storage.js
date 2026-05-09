import mysql from 'mysql2/promise';

let pool;
let initialized = false;

const GLOBAL_SCOPE = 'global';

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
  return value instanceof Date ? value.toISOString() : value;
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

function recordFromSnapshot(snapshot) {
  const selected = snapshot.selectedCharacter || {};
  const character = snapshot.saves?.character || {};
  const now = new Date();
  return {
    id: `save_${Date.now()}_${Math.random().toString(16).slice(2)}`,
    receivedAt: toMysqlDate(now),
    schemaVersion: snapshot.schemaVersion,
    accountId: snapshot.account?.accountId || selected.accountId || character.accountId || 'local',
    accountName: snapshot.account?.displayName || 'YDH Player',
    provider: snapshot.account?.provider || 'local',
    characterId: selected.characterId || character.characterId || 'default',
    characterName: selected.name || character.name || '검은 기사',
    classId: selected.classId || character.classId || 'knight',
    slotNo: selected.slot || 1,
    slotCount: Array.isArray(snapshot.characterSlots) ? snapshot.characterSlots.length : 0,
    level: character.level || 1,
    mapIndex: snapshot.saves?.map?.mapIndex ?? 0,
    snapshot
  };
}

async function initMysqlStorage() {
  if (initialized) return;
  const db = connection();
  await db.query(`
    CREATE TABLE IF NOT EXISTS ydh_accounts (
      account_id VARCHAR(80) NOT NULL,
      provider VARCHAR(32) NOT NULL DEFAULT 'local',
      display_name VARCHAR(80) NOT NULL DEFAULT 'YDH Player',
      created_at DATETIME NOT NULL,
      last_login_at DATETIME NOT NULL,
      last_snapshot_at DATETIME DEFAULT NULL,
      PRIMARY KEY (account_id),
      KEY idx_display_name (display_name),
      KEY idx_last_login_at (last_login_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8
  `);
  await db.query(`
    CREATE TABLE IF NOT EXISTS ydh_character_slots (
      character_id VARCHAR(80) NOT NULL,
      account_id VARCHAR(80) NOT NULL,
      slot_no INT NOT NULL DEFAULT 1,
      character_name VARCHAR(80) NOT NULL DEFAULT '검은 기사',
      class_id VARCHAR(32) NOT NULL DEFAULT 'knight',
      level INT NOT NULL DEFAULT 1,
      map_index INT NOT NULL DEFAULT 0,
      created_at DATETIME NOT NULL,
      last_selected_at DATETIME DEFAULT NULL,
      last_snapshot_at DATETIME DEFAULT NULL,
      PRIMARY KEY (character_id),
      UNIQUE KEY uk_account_slot (account_id, slot_no),
      KEY idx_account_id (account_id),
      KEY idx_character_name (character_name),
      CONSTRAINT fk_character_slots_account
        FOREIGN KEY (account_id) REFERENCES ydh_accounts(account_id)
        ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8
  `);
  await db.query(`
    CREATE TABLE IF NOT EXISTS ydh_save_snapshots (
      id VARCHAR(64) NOT NULL,
      received_at DATETIME NOT NULL,
      schema_version INT NOT NULL,
      account_id VARCHAR(80) NOT NULL DEFAULT 'local',
      account_name VARCHAR(80) NOT NULL DEFAULT 'YDH Player',
      character_id VARCHAR(80) NOT NULL DEFAULT 'default',
      character_name VARCHAR(80) NOT NULL DEFAULT '검은 기사',
      class_id VARCHAR(32) NOT NULL DEFAULT 'knight',
      slot_count INT NOT NULL DEFAULT 0,
      level INT NOT NULL DEFAULT 1,
      map_index INT NOT NULL DEFAULT 0,
      snapshot_json LONGTEXT NOT NULL,
      PRIMARY KEY (id),
      KEY idx_received_at (received_at),
      KEY idx_account_character (account_id, character_id),
      KEY idx_character_name (character_name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8
  `);
  await db.query(`
    CREATE TABLE IF NOT EXISTS ydh_custom_maps (
      scope_key VARCHAR(180) NOT NULL,
      map_id VARCHAR(120) NOT NULL,
      account_id VARCHAR(80) NOT NULL DEFAULT 'global',
      character_id VARCHAR(80) NOT NULL DEFAULT 'global',
      map_name VARCHAR(120) NOT NULL,
      source VARCHAR(40) NOT NULL DEFAULT 'tiled-json',
      source_url VARCHAR(255) NOT NULL DEFAULT 'server-custom',
      width INT NOT NULL DEFAULT 0,
      height INT NOT NULL DEFAULT 0,
      saved_at DATETIME NOT NULL,
      updated_at DATETIME NOT NULL,
      map_json LONGTEXT NOT NULL,
      PRIMARY KEY (scope_key, map_id),
      KEY idx_custom_map_scope (account_id, character_id),
      KEY idx_custom_map_updated_at (updated_at),
      KEY idx_custom_map_name (map_name)
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
    VALUES ('schema_version', '3', NOW())
    ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value), updated_at = NOW()
  `);
  initialized = true;
}

function rowToSummary(row) {
  return {
    id: row.id,
    receivedAt: toIso(row.received_at),
    schemaVersion: row.schema_version,
    accountId: row.account_id,
    accountName: row.account_name,
    characterId: row.character_id,
    characterName: row.character_name,
    classId: row.class_id,
    slotCount: row.slot_count,
    level: row.level,
    mapIndex: row.map_index
  };
}

function rowToRecord(row) {
  return {
    ...rowToSummary(row),
    snapshot: JSON.parse(row.snapshot_json)
  };
}

function customMapSummary(row) {
  return {
    id: row.map_id,
    name: row.map_name,
    accountId: row.account_id,
    characterId: row.character_id,
    scopeKey: row.scope_key,
    source: row.source,
    sourceUrl: row.source_url,
    width: row.width,
    height: row.height,
    savedAt: toIso(row.saved_at),
    updatedAt: toIso(row.updated_at)
  };
}

function customMapRecord(row) {
  return {
    ...customMapSummary(row),
    map: JSON.parse(row.map_json)
  };
}

async function syncAccountAndCharacters(db, snapshot, record) {
  await db.query(
    `INSERT INTO ydh_accounts (
      account_id, provider, display_name, created_at, last_login_at, last_snapshot_at
    ) VALUES (?, ?, ?, ?, ?, ?)
    ON DUPLICATE KEY UPDATE
      provider = VALUES(provider),
      display_name = VALUES(display_name),
      last_login_at = VALUES(last_login_at),
      last_snapshot_at = VALUES(last_snapshot_at)`,
    [record.accountId, record.provider, record.accountName, record.receivedAt, record.receivedAt, record.receivedAt]
  );

  const slots = Array.isArray(snapshot.characterSlots) && snapshot.characterSlots.length
    ? snapshot.characterSlots
    : [{
        characterId: record.characterId,
        accountId: record.accountId,
        slot: record.slotNo,
        name: record.characterName,
        classId: record.classId,
        createdAt: record.receivedAt,
        lastSelectedAt: record.receivedAt
      }];

  for (const slot of slots) {
    const isSelected = slot.characterId === record.characterId;
    await db.query(
      `INSERT INTO ydh_character_slots (
        character_id, account_id, slot_no, character_name, class_id,
        level, map_index, created_at, last_selected_at, last_snapshot_at
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        account_id = VALUES(account_id),
        slot_no = VALUES(slot_no),
        character_name = VALUES(character_name),
        class_id = VALUES(class_id),
        level = VALUES(level),
        map_index = VALUES(map_index),
        last_selected_at = VALUES(last_selected_at),
        last_snapshot_at = VALUES(last_snapshot_at)`,
      [
        slot.characterId || record.characterId,
        slot.accountId || record.accountId,
        slot.slot || record.slotNo,
        slot.name || record.characterName,
        slot.classId || record.classId,
        isSelected ? record.level : 1,
        isSelected ? record.mapIndex : 0,
        toMysqlDate(slot.createdAt || record.receivedAt),
        toMysqlDate(slot.lastSelectedAt || record.receivedAt),
        isSelected ? record.receivedAt : null
      ]
    );
  }
}

export async function saveSnapshot(snapshot) {
  await initMysqlStorage();
  const db = connection();
  const record = recordFromSnapshot(snapshot);
  await syncAccountAndCharacters(db, snapshot, record);
  await db.query(
    `INSERT INTO ydh_save_snapshots (
      id, received_at, schema_version, account_id, account_name,
      character_id, character_name, class_id, slot_count, level, map_index, snapshot_json
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [
      record.id,
      record.receivedAt,
      record.schemaVersion,
      record.accountId,
      record.accountName,
      record.characterId,
      record.characterName,
      record.classId,
      record.slotCount,
      record.level,
      record.mapIndex,
      JSON.stringify(snapshot)
    ]
  );
  return record;
}

export async function listSnapshots() {
  await initMysqlStorage();
  const db = connection();
  const [rows] = await db.query(
    `SELECT id, received_at, schema_version, account_id, account_name,
            character_id, character_name, class_id, slot_count, level, map_index
       FROM ydh_save_snapshots
      ORDER BY received_at DESC
      LIMIT 50`
  );
  return rows.map(rowToSummary);
}

export async function latestSnapshot() {
  await initMysqlStorage();
  const db = connection();
  const [rows] = await db.query(
    `SELECT id, received_at, schema_version, account_id, account_name,
            character_id, character_name, class_id, slot_count, level, map_index, snapshot_json
       FROM ydh_save_snapshots
      ORDER BY received_at DESC
      LIMIT 1`
  );
  if (!rows.length) return null;
  return rowToRecord(rows[0]);
}

export async function snapshotById(id) {
  await initMysqlStorage();
  const db = connection();
  const [rows] = await db.query(
    `SELECT id, received_at, schema_version, account_id, account_name,
            character_id, character_name, class_id, slot_count, level, map_index, snapshot_json
       FROM ydh_save_snapshots
      WHERE id = ?
      LIMIT 1`,
    [id]
  );
  if (!rows.length) return null;
  return rowToRecord(rows[0]);
}

export async function listAccounts() {
  await initMysqlStorage();
  const db = connection();
  const [rows] = await db.query(
    `SELECT account_id AS accountId, provider, display_name AS displayName,
            created_at AS createdAt, last_login_at AS lastLoginAt, last_snapshot_at AS lastSnapshotAt
       FROM ydh_accounts
      ORDER BY last_snapshot_at DESC, last_login_at DESC
      LIMIT 100`
  );
  return rows;
}

export async function listCharacters(accountId) {
  await initMysqlStorage();
  const db = connection();
  const params = [];
  let where = '';
  if (accountId) {
    where = 'WHERE account_id = ?';
    params.push(accountId);
  }
  const [rows] = await db.query(
    `SELECT character_id AS characterId, account_id AS accountId, slot_no AS slot,
            character_name AS name, class_id AS classId, level, map_index AS mapIndex,
            created_at AS createdAt, last_selected_at AS lastSelectedAt, last_snapshot_at AS lastSnapshotAt
       FROM ydh_character_slots
       ${where}
      ORDER BY account_id ASC, slot_no ASC
      LIMIT 300`,
    params
  );
  return rows;
}

export async function saveCustomMap(payload) {
  await initMysqlStorage();
  const validation = validateCustomMapPayload(payload);
  if (!validation.ok) {
    const error = new Error(validation.errors.join(', '));
    error.statusCode = 400;
    throw error;
  }

  const db = connection();
  const map = validation.map;
  const scope = validation.scope;
  const key = scopeKey(scope);
  const now = toMysqlDate(new Date());
  const mapJson = JSON.stringify({ ...map, accountId: scope.accountId, characterId: scope.characterId });

  await db.query(
    `INSERT INTO ydh_custom_maps (
      scope_key, map_id, account_id, character_id, map_name, source, source_url,
      width, height, saved_at, updated_at, map_json
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ON DUPLICATE KEY UPDATE
      account_id = VALUES(account_id),
      character_id = VALUES(character_id),
      map_name = VALUES(map_name),
      source = VALUES(source),
      source_url = VALUES(source_url),
      width = VALUES(width),
      height = VALUES(height),
      updated_at = VALUES(updated_at),
      map_json = VALUES(map_json)`,
    [
      key,
      map.id,
      scope.accountId,
      scope.characterId,
      map.name,
      map.source || 'tiled-json',
      map.sourceUrl || 'server-custom',
      map.rows?.[0]?.length || 0,
      map.rows?.length || 0,
      now,
      now,
      mapJson
    ]
  );

  return getCustomMap(map.id, scope);
}

export async function listCustomMaps(scope = {}) {
  await initMysqlStorage();
  const db = connection();
  const wanted = scopeFrom(scope);
  const [rows] = await db.query(
    `SELECT scope_key, map_id, account_id, character_id, map_name, source, source_url,
            width, height, saved_at, updated_at
       FROM ydh_custom_maps
      WHERE account_id = ? AND character_id = ?
      ORDER BY updated_at DESC
      LIMIT 200`,
    [wanted.accountId, wanted.characterId]
  );
  return rows.map(customMapSummary);
}

export async function getCustomMap(id, scope = {}) {
  await initMysqlStorage();
  const db = connection();
  const wanted = scopeFrom(scope);
  const [rows] = await db.query(
    `SELECT scope_key, map_id, account_id, character_id, map_name, source, source_url,
            width, height, saved_at, updated_at, map_json
       FROM ydh_custom_maps
      WHERE map_id = ? AND account_id = ? AND character_id = ?
      LIMIT 1`,
    [id, wanted.accountId, wanted.characterId]
  );
  if (!rows.length) return null;
  return customMapRecord(rows[0]);
}

export async function deleteCustomMap(id, scope = {}) {
  await initMysqlStorage();
  const db = connection();
  const wanted = scopeFrom(scope);
  const [result] = await db.query(
    `DELETE FROM ydh_custom_maps
      WHERE map_id = ? AND account_id = ? AND character_id = ?`,
    [id, wanted.accountId, wanted.characterId]
  );
  const [countRows] = await db.query('SELECT COUNT(*) AS count FROM ydh_custom_maps');
  return { deleted: result.affectedRows > 0, count: countRows[0]?.count || 0 };
}

export async function health() {
  await initMysqlStorage();
  const db = connection();
  const [snapshotRows] = await db.query('SELECT COUNT(*) AS count FROM ydh_save_snapshots');
  const [accountRows] = await db.query('SELECT COUNT(*) AS count FROM ydh_accounts');
  const [characterRows] = await db.query('SELECT COUNT(*) AS count FROM ydh_character_slots');
  return {
    storage: 'mysql',
    count: snapshotRows[0]?.count || 0,
    accounts: accountRows[0]?.count || 0,
    characters: characterRows[0]?.count || 0
  };
}

export async function customMapHealth() {
  await initMysqlStorage();
  const db = connection();
  const [mapRows] = await db.query('SELECT COUNT(*) AS count FROM ydh_custom_maps');
  const [scopeRows] = await db.query('SELECT COUNT(DISTINCT scope_key) AS count FROM ydh_custom_maps');
  return {
    storage: 'mysql',
    count: mapRows[0]?.count || 0,
    max: Number(process.env.YDH_MAX_CUSTOM_MAPS || 100),
    scopes: scopeRows[0]?.count || 0
  };
}
