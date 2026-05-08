-- YDH Chronicle MySQL 5.5 compatible schema
-- MySQL 5.5 has no JSON type, so JSON payloads are stored as LONGTEXT.
-- Recommended database charset: utf8mb4 if available. If your MySQL 5.5 build does not support utf8mb4 well, use utf8.

CREATE DATABASE IF NOT EXISTS ydh_chronicle
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

USE ydh_chronicle;

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

CREATE TABLE IF NOT EXISTS ydh_schema_meta (
  meta_key VARCHAR(64) NOT NULL,
  meta_value VARCHAR(255) NOT NULL,
  updated_at DATETIME NOT NULL,
  PRIMARY KEY (meta_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO ydh_schema_meta (meta_key, meta_value, updated_at)
VALUES ('schema_version', '1', NOW())
ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value), updated_at = NOW();
