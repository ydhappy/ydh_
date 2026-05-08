export function validateSnapshot(body) {
  const errors = [];

  if (!body || typeof body !== 'object') errors.push('body must be an object');
  if (body?.schemaVersion !== 1) errors.push('schemaVersion must be 1');
  if (body?.app !== 'YDH Chronicle') errors.push('app must be YDH Chronicle');
  if (!body?.generatedAt) errors.push('generatedAt is required');
  if (!body?.saves || typeof body.saves !== 'object') errors.push('saves object is required');
  if (body?.account && typeof body.account !== 'object') errors.push('account must be object or null');
  if (body?.selectedCharacter && typeof body.selectedCharacter !== 'object') errors.push('selectedCharacter must be object or null');
  if (body?.characterSlots && !Array.isArray(body.characterSlots)) errors.push('characterSlots must be array or null');

  const saves = body?.saves || {};
  if (saves.character && typeof saves.character !== 'object') errors.push('saves.character must be object or null');
  if (saves.map && typeof saves.map !== 'object') errors.push('saves.map must be object or null');
  if (saves.chapterQuests && typeof saves.chapterQuests !== 'object') errors.push('saves.chapterQuests must be object or null');
  if (saves.codexUnlocks && typeof saves.codexUnlocks !== 'object') errors.push('saves.codexUnlocks must be object or null');

  return {
    ok: errors.length === 0,
    errors
  };
}

export function summarizeSnapshot(snapshot) {
  const character = snapshot.saves?.character || {};
  const map = snapshot.saves?.map || {};
  const quests = snapshot.saves?.chapterQuests || {};
  const codex = snapshot.saves?.codexUnlocks?.unlocked || {};
  const selected = snapshot.selectedCharacter || {};

  return {
    schemaVersion: snapshot.schemaVersion,
    generatedAt: snapshot.generatedAt,
    account: {
      accountId: snapshot.account?.accountId || selected.accountId || character.accountId || 'local',
      displayName: snapshot.account?.displayName || 'YDH Player',
      provider: snapshot.account?.provider || 'local'
    },
    selectedCharacter: {
      characterId: selected.characterId || character.characterId || 'default',
      name: selected.name || character.name || '검은 기사',
      classId: selected.classId || character.classId || 'knight',
      slot: selected.slot || 1
    },
    character: {
      name: character.name || selected.name || '검은 기사',
      level: character.level || 1,
      gold: character.gold || 0,
      wave: character.wave || 1,
      inventoryCount: Array.isArray(character.inventory) ? character.inventory.length : 0
    },
    map: {
      mapIndex: map.mapIndex ?? 0,
      x: map.x ?? 0,
      y: map.y ?? 0,
      direction: map.direction ?? 12,
      steps: map.steps ?? 0
    },
    characterSlots: Array.isArray(snapshot.characterSlots) ? snapshot.characterSlots.length : 0,
    quests: Object.keys(quests).length,
    codexUnlocks: Object.keys(codex).length
  };
}
