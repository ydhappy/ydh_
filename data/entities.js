window.YDH_DIRECTIONS_16 = [
  { id: 0, key: 'E', name: '동', angle: 0, dx: 1, dy: 0 },
  { id: 1, key: 'ENE', name: '동동북', angle: 22.5, dx: 1, dy: -0.5 },
  { id: 2, key: 'NE', name: '북동', angle: 45, dx: 1, dy: -1 },
  { id: 3, key: 'NNE', name: '북북동', angle: 67.5, dx: 0.5, dy: -1 },
  { id: 4, key: 'N', name: '북', angle: 90, dx: 0, dy: -1 },
  { id: 5, key: 'NNW', name: '북북서', angle: 112.5, dx: -0.5, dy: -1 },
  { id: 6, key: 'NW', name: '북서', angle: 135, dx: -1, dy: -1 },
  { id: 7, key: 'WNW', name: '서북서', angle: 157.5, dx: -1, dy: -0.5 },
  { id: 8, key: 'W', name: '서', angle: 180, dx: -1, dy: 0 },
  { id: 9, key: 'WSW', name: '서남서', angle: 202.5, dx: -1, dy: 0.5 },
  { id: 10, key: 'SW', name: '남서', angle: 225, dx: -1, dy: 1 },
  { id: 11, key: 'SSW', name: '남남서', angle: 247.5, dx: -0.5, dy: 1 },
  { id: 12, key: 'S', name: '남', angle: 270, dx: 0, dy: 1 },
  { id: 13, key: 'SSE', name: '남남동', angle: 292.5, dx: 0.5, dy: 1 },
  { id: 14, key: 'SE', name: '남동', angle: 315, dx: 1, dy: 1 },
  { id: 15, key: 'ESE', name: '동남동', angle: 337.5, dx: 1, dy: 0.5 }
];

window.YDH_ENTITIES = {
  player: {
    id: 'player',
    name: '검은 기사',
    role: 'player',
    sheet: 'assets/sprites/player-16dir.svg',
    frameWidth: 64,
    frameHeight: 64,
    directions: 16,
    defaultDirection: 12
  },
  monster: {
    id: 'monster-wolf',
    name: '그림자 늑대',
    role: 'monster',
    sheet: 'assets/sprites/monster-wolf-16dir.svg',
    frameWidth: 64,
    frameHeight: 64,
    directions: 16,
    defaultDirection: 8
  },
  npc: {
    id: 'npc-guide',
    name: '마을 안내인',
    role: 'npc',
    sheet: 'assets/sprites/npc-guide-16dir.svg',
    frameWidth: 64,
    frameHeight: 64,
    directions: 16,
    defaultDirection: 12
  }
};

window.YDH_getDirection16 = function getDirection16(dx, dy) {
  if (dx === 0 && dy === 0) return 12;
  const angle = (Math.atan2(-dy, dx) * 180 / Math.PI + 360) % 360;
  return Math.round(angle / 22.5) % 16;
};
