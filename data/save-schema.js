window.YDH_SAVE_SCHEMA = {
  version: 1,
  app: 'YDH Chronicle',
  localStorageKeys: {
    accountProfile: 'ydh-account-profile-v1',
    characterSlots: 'ydh-character-slots-v1',
    selectedCharacter: 'ydh-selected-character-v1',
    character: 'ydh-chronicle-save-v1',
    map: 'ydh-chronicle-map-v1',
    chapterQuests: 'ydh-chapter-quests-v1',
    codexUnlocks: 'ydh-codex-unlocks-v1',
    gmConsoleOpen: 'ydh-gm-console-open'
  },
  api: {
    baseUrl: '/api',
    endpoints: {
      snapshot: '/api/save/snapshot',
      character: '/api/save/character',
      map: '/api/save/map',
      quests: '/api/save/quests',
      codex: '/api/save/codex',
      restore: '/api/save/restore'
    }
  },
  models: {
    account: {
      accountId: 'string',
      provider: 'local|google|github|custom',
      displayName: 'string',
      createdAt: 'ISO-8601 datetime',
      lastLoginAt: 'ISO-8601 datetime'
    },
    characterSlot: {
      characterId: 'string',
      accountId: 'string',
      slot: 'number',
      name: 'string',
      classId: 'knight|mage|rogue|priest',
      createdAt: 'ISO-8601 datetime',
      lastSelectedAt: 'ISO-8601 datetime'
    },
    character: {
      characterId: 'string',
      accountId: 'string',
      name: 'string',
      level: 'number',
      exp: 'number',
      hp: 'number',
      mp: 'number',
      atk: 'number',
      def: 'number',
      gold: 'number',
      wave: 'number',
      inventory: 'string[]',
      cooldowns: 'Record<string, epochMs>'
    },
    mapState: {
      mapIndex: 'number',
      x: 'number',
      y: 'number',
      direction: 'number',
      steps: 'number',
      lastTarget: 'object|null'
    },
    quests: {
      questId: 'string',
      objectives: 'Record<objectiveId, progress>',
      rewardClaimed: 'boolean'
    },
    codex: {
      unlocked: 'Record<category:id, boolean>',
      lastUnlock: 'object|null'
    }
  }
};
