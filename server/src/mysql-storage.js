import mysql from 'mysql2/promise';

let pool;
let initialized = false;

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
    CREATE TABLE IF NOT EXISTS ydh_schema_meta (
      meta_key VARCHAR(64) NOT NULL,
      meta_value VARCHAR(255) NOT NULL,
      updated_at DATETIME NOT NULL,
      PRIMARY KEY (meta_key)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8
  `);
  await db.query(`
    INSERT INTO ydh_schema_meta (meta_key, meta_value, updated_at)
    VALUES ('schema_version', '2', NOW())
    ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value), updated_at = NOW()
  `);
  initialized = true;
}

function rowToSummary(row) {
  return {
    id: row.id,
    receivedAt: row.received_at instanceof Date ? row.received_at.toISOString() : row.received_at,
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
