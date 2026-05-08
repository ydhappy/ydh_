window.YDH_ITEM_GRADE_META = {
  normal: { label: '일반', weight: 1 },
  magic: { label: '마법', weight: 2 },
  rare: { label: '희귀', weight: 3 },
  epic: { label: '영웅', weight: 4 },
  quest: { label: '퀘스트', weight: 5 }
};

window.YDH_EQUIPMENT_SLOTS = [
  { id: 'weapon', label: '무기', icon: '⚔️' },
  { id: 'armor', label: '방어구', icon: '🛡️' },
  { id: 'accessory', label: '장신구', icon: '💍' },
  { id: 'artifact', label: '유물/재료', icon: '🔮' }
];

window.YDH_BASE_ITEM_CATALOG = [
  { id: 'old-sword', name: '낡은 검', type: 'weapon', slot: 'weapon', grade: 'normal', atk: 1, def: 0 },
  { id: 'training-armor', name: '수련자 갑옷', type: 'armor', slot: 'armor', grade: 'normal', atk: 0, def: 1 },
  { id: 'red-potion', name: '빨간 물약', type: 'consumable', slot: 'artifact', grade: 'normal', atk: 0, def: 0 },
  { id: 'steel-sword', name: '강철 장검', type: 'weapon', slot: 'weapon', grade: 'normal', atk: 1, def: 0 },
  { id: 'mana-ring', name: '마력 반지', type: 'accessory', slot: 'accessory', grade: 'magic', atk: 1, def: 1 },
  { id: 'dragon-armor', name: '용비늘 갑옷', type: 'armor', slot: 'armor', grade: 'rare', atk: 0, def: 2 },
  { id: 'knight-cloak', name: '기사단 망토', type: 'armor', slot: 'armor', grade: 'normal', atk: 0, def: 1 },
  { id: 'blessed-sword', name: '축복받은 검', type: 'weapon', slot: 'weapon', grade: 'rare', atk: 2, def: 0 },
  { id: 'memory-fragment', name: '잃어버린 기억의 조각', type: 'quest', slot: 'artifact', grade: 'quest', atk: 0, def: 0 },
  { id: 'raven-quill', name: '까마귀 깃펜', type: 'quest', slot: 'artifact', grade: 'quest', atk: 0, def: 0 },
  { id: 'archive-shard', name: '별빛 기록 조각', type: 'quest', slot: 'artifact', grade: 'quest', atk: 0, def: 0 },
  { id: 'moon-seal', name: '검은 달 봉인석', type: 'quest', slot: 'artifact', grade: 'quest', atk: 0, def: 0 }
];

window.YDH_buildItemCatalog = function buildItemCatalog() {
  const loreItems = (window.YDH_LORE_CONTENT?.items || []).map((item) => ({
    ...item,
    slot: item.type === 'weapon' ? 'weapon'
      : item.type === 'armor' ? 'armor'
      : item.type === 'accessory' ? 'accessory'
      : 'artifact'
  }));
  return [...window.YDH_BASE_ITEM_CATALOG, ...loreItems];
};
