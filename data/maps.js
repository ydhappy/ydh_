window.YDH_MAPS = {
  tileTypes: {
    G: { name: '잔디', asset: 'assets/tiles/grass.svg', passable: true, encounter: 0.04 },
    R: { name: '길', asset: 'assets/tiles/road.svg', passable: true, encounter: 0.01 },
    S: { name: '돌바닥', asset: 'assets/tiles/stone.svg', passable: true, encounter: 0.02 },
    T: { name: '나무', asset: 'assets/tiles/tree.svg', passable: false, encounter: 0 },
    W: { name: '물', asset: 'assets/tiles/water.svg', passable: false, encounter: 0 },
    M: { name: '몬스터 구역', asset: 'assets/tiles/grass.svg', passable: true, encounter: 0.55, marker: '👹' },
    N: { name: 'NPC', asset: 'assets/tiles/road.svg', passable: true, encounter: 0, marker: '💬' },
    P: { name: '포탈', asset: 'assets/tiles/portal.svg', passable: true, encounter: 0, marker: '🌀' }
  },
  maps: [
    {
      id: 'talking-island',
      name: '말하는 섬 해안',
      description: '초보 용사가 처음 사냥을 시작하는 해안 방어선입니다.',
      start: { x: 2, y: 7 },
      portalTo: 1,
      rows: [
        'TTTTTTTTTTTTTTTT',
        'TGGGGGGGGGGGMMPT',
        'TGTTTGGGGTTTGGGT',
        'TGRRRGGGGGGGGGGT',
        'TGRTTRRRRTTTGGGT',
        'TGRGGGGGRGGGGGGT',
        'TGRGGWGGGGGMMGGT',
        'TGRNNWGGTTGGGGGT',
        'TGRGGWGGTTGGGGGT',
        'TGRRRRRRGGGGGGGT',
        'TGGGGGGGGGGGGGGT',
        'TTTTTTTTTTTTTTTT'
      ]
    },
    {
      id: 'silver-forest',
      name: '은빛 숲',
      description: '나무와 정령 몬스터가 많은 중급 사냥터입니다.',
      start: { x: 1, y: 9 },
      portalTo: 2,
      rows: [
        'TTTTTTTTTTTTTTTT',
        'TGGGTTTGGGGGGGPT',
        'TGMGGTTGGTTTGGGT',
        'TGGGGRRRGTTTMMGT',
        'TTTTGRRRGGGGGGGT',
        'TGGGGRRRTTTGGGGT',
        'TGMGGGGGGTTGGGGT',
        'TGGTTTTGGTTGMMGT',
        'TGGGGGGGGGGGGGGT',
        'TRRRRNNRRRRGGGGT',
        'TGGGGGGGGGGGGGGT',
        'TTTTTTTTTTTTTTTT'
      ]
    },
    {
      id: 'ancient-cave',
      name: '버려진 광산',
      description: '돌바닥과 좁은 통로가 많은 동굴형 맵입니다.',
      start: { x: 2, y: 9 },
      portalTo: 3,
      rows: [
        'TTTTTTTTTTTTTTTT',
        'TSSSSSSSSSSSSSPT',
        'TSSTTTSSSTTTSSST',
        'TSSMMSSSSSSSSSST',
        'TTTTSSSTTTTSSSST',
        'TSSSSSSTSSSSMMST',
        'TSSSTTSTSSSTTTTT',
        'TSSSSSSSSSSSSSST',
        'TSSMMTTTTTSSSSST',
        'TRRRRNNRRRSSSSST',
        'TSSSSSSSSSSSSSST',
        'TTTTTTTTTTTTTTTT'
      ]
    }
  ]
};
