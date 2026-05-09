window.YDH_ATLAS = {
  version: 2,
  mode: 'atlas',
  tiles: {
    image: 'assets/atlas/tiles-atlas.svg',
    imagePng: 'assets/atlas/tiles-atlas.png',
    imageWebp: 'assets/atlas/tiles-atlas.webp',
    preferredFormats: ['webp', 'png', 'svg'],
    activeFormat: 'svg',
    tileWidth: 64,
    tileHeight: 64,
    columns: 6,
    rows: 1,
    fallbackMode: 'single-svg',
    codes: {
      G: { name: '잔디', x: 0, y: 0 },
      R: { name: '길', x: 1, y: 0 },
      S: { name: '돌바닥', x: 2, y: 0 },
      T: { name: '나무', x: 3, y: 0 },
      W: { name: '물', x: 4, y: 0 },
      P: { name: '포탈', x: 5, y: 0 },
      M: { name: '몬스터 구역', base: 'G', marker: '👹' },
      N: { name: 'NPC', base: 'R', marker: '💬' }
    }
  }
};
