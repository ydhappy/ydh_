-- YDH Chronicle MySQL 5.5 compatible schema
-- MySQL 5.5 has no JSON type, so JSON payloads are stored as LONGTEXT.
-- Recommended database charset: utf8mb4 if available. If your MySQL 5.5 build does not support utf8mb4 well, use utf8.

CREATE DATABASE IF NOT EXISTS ydh_chronicle
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

USE ydh_chronicle;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS ydh_schema_meta (
  meta_key VARCHAR(64) NOT NULL,
  meta_value VARCHAR(255) NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (meta_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO ydh_schema_meta (meta_key, meta_value, updated_at)
VALUES ('schema_version', '3', NOW())
ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value), updated_at = NOW();
