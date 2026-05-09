window.YDH_GENERATED_ASSET_DETAILS = {
  version: 1,
  directionSpec: {
    count: 16,
    order: ['S', 'SSW', 'SW', 'WSW', 'W', 'WNW', 'NW', 'NNW', 'N', 'NNE', 'NE', 'ENE', 'E', 'ESE', 'SE', 'SSE'],
    degrees: [180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5, 0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5]
  },
  sheetTargets: {
    playableClass: { cellWidth: 96, cellHeight: 128, columns: 16, rows: 7, states: ['idle', 'walk', 'attack', 'cast', 'hit', 'death', 'emote'] },
    npc: { cellWidth: 80, cellHeight: 112, columns: 16, rows: 4, states: ['idle', 'talk', 'walk', 'gesture'] },
    monster: { cellWidth: 112, cellHeight: 112, columns: 16, rows: 6, states: ['idle', 'walk', 'attack', 'hit', 'death', 'skill'] },
    tile: { cellWidth: 64, cellHeight: 64, columns: 12, rows: 8, states: ['base', 'edge', 'variant', 'animated'] }
  },
  classes: [
    { id: 'knight', nameKo: '기사', role: 'frontline-tank', weapon: 'sword-shield', armor: 'heavy', palette: ['black-steel', 'gold-trim', 'blue-cape'], silhouette: 'large shield, short sword, heavy shoulder armor', gameplay: { hp: 120, mp: 30, atk: 12, def: 10, speed: 0.9, range: 1 }, skills: ['shield-bash', 'guard-stance', 'lion-slash'], spritePriority: ['idle', 'walk', 'attack', 'hit', 'death'] },
    { id: 'wizard', nameKo: '마법사', role: 'ranged-nuker', weapon: 'staff', armor: 'robe', palette: ['deep-blue', 'ivory', 'arcane-gold'], silhouette: 'long staff, robe hem, glowing gem', gameplay: { hp: 70, mp: 120, atk: 18, def: 3, speed: 0.85, range: 6 }, skills: ['firebolt', 'ice-spear', 'mana-shield'], spritePriority: ['idle', 'cast', 'walk', 'hit', 'death'] },
    { id: 'elf-ranger', nameKo: '엘프 궁수', role: 'ranged-dps', weapon: 'bow', armor: 'leather', palette: ['forest-green', 'tan-leather', 'silver-hair'], silhouette: 'bow arc, quiver, slim cape', gameplay: { hp: 86, mp: 70, atk: 15, def: 5, speed: 1.15, range: 7 }, skills: ['rapid-shot', 'wind-step', 'piercing-arrow'], spritePriority: ['idle', 'walk', 'attack', 'cast', 'hit'] },
    { id: 'dark-rogue', nameKo: '다크 로그', role: 'burst-assassin', weapon: 'dual-dagger', armor: 'hooded-leather', palette: ['charcoal', 'violet-glow', 'dark-red'], silhouette: 'hood, twin daggers, narrow stance', gameplay: { hp: 82, mp: 55, atk: 17, def: 4, speed: 1.25, range: 1 }, skills: ['backstab', 'shadow-step', 'poison-edge'], spritePriority: ['idle', 'walk', 'attack', 'emote', 'death'] },
    { id: 'cleric', nameKo: '성직자', role: 'support-healer', weapon: 'mace-staff', armor: 'holy-robe', palette: ['white-gold', 'soft-blue', 'warm-light'], silhouette: 'holy staff, mantle, circular halo detail', gameplay: { hp: 92, mp: 100, atk: 9, def: 6, speed: 0.95, range: 5 }, skills: ['heal', 'blessing', 'turn-undead'], spritePriority: ['idle', 'cast', 'walk', 'hit', 'emote'] },
    { id: 'lancer', nameKo: '창기사', role: 'reach-bruiser', weapon: 'long-spear', armor: 'medium-plate', palette: ['steel', 'crimson-cloth', 'bronze'], silhouette: 'long spear line, angular helmet, half cape', gameplay: { hp: 105, mp: 45, atk: 14, def: 8, speed: 1.0, range: 2 }, skills: ['thrust-line', 'sweep', 'charge'], spritePriority: ['idle', 'walk', 'attack', 'hit', 'death'] }
  ],
  npcs: [
    { id: 'town-guard', nameKo: '마을 경비병', role: 'security', interaction: 'patrol-dialog', services: ['basic-guide'], anchorMap: 'town-gate' },
    { id: 'blacksmith', nameKo: '대장장이', role: 'crafting', interaction: 'shop-craft', services: ['repair', 'enhance', 'weapon-craft'], anchorMap: 'forge' },
    { id: 'potion-merchant', nameKo: '물약 상인', role: 'merchant', interaction: 'shop-buy-sell', services: ['potion', 'scroll', 'return-stone'], anchorMap: 'market' },
    { id: 'innkeeper', nameKo: '여관 주인', role: 'recovery', interaction: 'rest-save', services: ['rest', 'buff-food'], anchorMap: 'inn' },
    { id: 'priestess', nameKo: '사제', role: 'healer', interaction: 'blessing-dialog', services: ['heal', 'cleanse', 'resurrection'], anchorMap: 'temple' },
    { id: 'teleport-keeper', nameKo: '텔레포트 관리인', role: 'travel', interaction: 'teleport-menu', services: ['map-warp', 'dungeon-entry'], anchorMap: 'portal-plaza' },
    { id: 'royal-messenger', nameKo: '왕실 전령', role: 'quest', interaction: 'quest-chain', services: ['chapter-quest', 'daily-order'], anchorMap: 'castle-road' },
    { id: 'forest-hermit', nameKo: '숲의 은자', role: 'lore', interaction: 'codex-unlock', services: ['lore', 'rare-crafting-hint'], anchorMap: 'deep-forest' }
  ],
  monsters: [
    { id: 'spectral-wolf-alpha', nameKo: '그림자 늑대 우두머리', rank: 'elite', family: 'beast-spirit', habitat: 'forest-night', ai: 'circle-and-lunge', drops: ['wolf-fang', 'spirit-fur'], hp: 95, atk: 16, def: 5, speed: 1.25 },
    { id: 'skeleton-warrior', nameKo: '해골 전사', rank: 'normal', family: 'undead', habitat: 'ruins', ai: 'direct-melee', drops: ['bone-fragment', 'rusted-blade'], hp: 70, atk: 11, def: 7, speed: 0.8 },
    { id: 'goblin-scout', nameKo: '고블린 정찰병', rank: 'normal', family: 'goblin', habitat: 'road-ambush', ai: 'hit-and-run', drops: ['goblin-token', 'small-knife'], hp: 52, atk: 10, def: 2, speed: 1.2 },
    { id: 'horned-orc-berserker', nameKo: '뿔 오크 광전사', rank: 'elite', family: 'orc', habitat: 'war-camp', ai: 'rage-charge', drops: ['orc-horn', 'heavy-axe-head'], hp: 130, atk: 21, def: 8, speed: 0.95 },
    { id: 'shadow-spider', nameKo: '그림자 거미', rank: 'normal', family: 'insectoid', habitat: 'cave', ai: 'web-root', drops: ['web-silk', 'venom-sac'], hp: 60, atk: 13, def: 4, speed: 1.05 },
    { id: 'cave-bat', nameKo: '동굴 박쥐', rank: 'normal', family: 'beast', habitat: 'cave', ai: 'swarm-flight', drops: ['bat-wing', 'echo-core'], hp: 42, atk: 8, def: 1, speed: 1.4 },
    { id: 'undead-mage', nameKo: '언데드 마법사', rank: 'elite', family: 'undead-caster', habitat: 'dungeon', ai: 'kite-cast', drops: ['dark-orb', 'torn-scroll'], hp: 88, atk: 19, def: 3, speed: 0.75 },
    { id: 'swamp-lizard', nameKo: '늪지 도마뱀', rank: 'normal', family: 'reptile', habitat: 'swamp', ai: 'poison-bite', drops: ['lizard-scale', 'swamp-gland'], hp: 78, atk: 12, def: 6, speed: 0.9 }
  ],
  environment: {
    terrain: ['grass', 'dirt', 'stone-road', 'ruined-floor', 'dungeon-floor', 'swamp-water-edge', 'deep-water', 'lava-edge', 'cliff-edge', 'portal-pad'],
    props: ['torch', 'banner', 'chest', 'shrine', 'broken-column', 'fence', 'tree', 'bush', 'rock-cluster', 'wagon', 'market-stall', 'signpost', 'gravestone', 'campfire'],
    animated: ['portal-pad', 'torch', 'campfire', 'water-edge', 'lava-edge'],
    collision: {
      passable: ['grass', 'dirt', 'stone-road', 'ruined-floor', 'dungeon-floor', 'portal-pad'],
      blocked: ['deep-water', 'lava-edge', 'cliff-edge', 'tree', 'rock-cluster', 'broken-column'],
      interactable: ['chest', 'shrine', 'signpost', 'market-stall', 'portal-pad']
    }
  },
  productionChecklist: [
    '원본 PNG/WebP sprite sheet 저장',
    '각 sprite sheet meta JSON 생성',
    '16방향 slicing 좌표 검증',
    'idle/walk/attack/cast/hit/death 상태별 row 매핑',
    '모바일 저사양 품질에서 애니메이션 프레임 스킵 확인',
    'atlas manifest와 map-engine 타일 코드 연결',
    '전투/맵/상점/퀘스트 UI에서 실제 asset path 참조'
  ]
};
