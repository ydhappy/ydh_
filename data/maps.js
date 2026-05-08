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
    },
    {
      id: 'black-moon-ruins',
      name: '검은 달 폐허',
      description: '달문양 제단이 무너진 자리입니다. 밤마다 그림자가 먼저 움직입니다.',
      start: { x: 2, y: 9 },
      portalTo: 4,
      rows: [
        'TTTTTTTTTTTTTTTT',
        'TSSSSSSSMMMMMSPT',
        'TSTTTSSSTTTTSSST',
        'TSSRRRSSSSSSSSST',
        'TSSRTTTTSSMMMSTT',
        'TSSRNNRSSSSSSSST',
        'TSSRTTTSSSTTTTST',
        'TSSRRRRSSSSMMMSP',
        'TSTTTTRSSSTTTTST',
        'TSSSSRRRSSSSSSST',
        'TSSSSSSSSSSSSSST',
        'TTTTTTTTTTTTTTTT'
      ]
    },
    {
      id: 'starlight-archive',
      name: '별빛 기록관',
      description: '기억과 이름을 보관하는 살아있는 서고입니다. 책장이 미궁처럼 움직입니다.',
      start: { x: 1, y: 9 },
      portalTo: 5,
      rows: [
        'TTTTTTTTTTTTTTTT',
        'TSSSSSSSSSSSSSPT',
        'TSSTTTTSSSTTTTST',
        'TSSNSSRSSSSMMMSP',
        'TTTTSSRSSSTTTTST',
        'TMMMMMRRRRSSSSST',
        'TSSSTTTSSRSSNNST',
        'TSSSSSSSSRSSSSST',
        'TSTTTTSSSRSSMMST',
        'TRRRRRRRRRSSSSST',
        'TSSSSSSSSSSSSSST',
        'TTTTTTTTTTTTTTTT'
      ]
    },
    {
      id: 'mirror-marsh',
      name: '거울 늪',
      description: '수면에 비친 것은 현재가 아니라 선택하지 않은 미래입니다.',
      start: { x: 2, y: 8 },
      portalTo: 6,
      rows: [
        'TTTTTTTTTTTTTTTT',
        'TGGGWWWWWGGGMGPT',
        'TGGRRRRWWGGGGGGT',
        'TGGWMMRWWTTTGGGT',
        'TNNWRRRRRGGGMMGT',
        'TGGWWWWWRGGGGGGT',
        'TGGGMMGWRGGTTTGT',
        'TGGTTTGWRGGGGGGT',
        'TGRRRRRRRGGMMGGT',
        'TGGGWWWWGGGGGGGT',
        'TGGGGGGGGGGGGGGT',
        'TTTTTTTTTTTTTTTT'
      ]
    },
    {
      id: 'abyss-throne',
      name: '심연의 왕좌',
      description: '검은 달의 군주가 잠든 최종 전장입니다. 모든 포탈이 이곳으로 접힙니다.',
      start: { x: 2, y: 9 },
      portalTo: 0,
      rows: [
        'TTTTTTTTTTTTTTTT',
        'TSSSSSMMMMSSSSPT',
        'TSTTTTSSSSTTTTST',
        'TSSMMSSSSSSMMsst'.toUpperCase(),
        'TSSSTTTNNRTTTTST',
        'TMMMMSSRRRSSMMMT',
        'TSSSTTTSSRTTTTST',
        'TSSMMSSSSRSSMMST',
        'TSTTTTSSSRSSSSST',
        'TRRRRRRRRRSSSSST',
        'TSSSSSMMMMSSSSST',
        'TTTTTTTTTTTTTTTT'
      ]
    }
  ]
};
