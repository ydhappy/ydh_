# Atlas Pipeline

## 상태

현재 24-1A ~ 24-1B까지 완료된 상태입니다.

- 24-1A: atlas 매니페스트와 타일 렌더러 지원 완료
- 24-1B: PNG/WebP 생성 스크립트 + WebP/PNG/SVG 우선 로더 완료

## 파일

```text
assets/atlas/tiles-atlas.svg
data/atlas.js
atlas-loader.js
tools/generate-tile-atlas.mjs
```

## atlas 우선순위

브라우저는 아래 순서로 atlas 이미지를 검사합니다.

```text
WebP → PNG → SVG
```

사용 가능한 첫 번째 이미지를 `window.YDH_ATLAS.tiles.imageActive`에 저장합니다.

선택된 format은 아래 값으로 확인할 수 있습니다.

```js
window.YDH_ATLAS.tiles.activeFormat
```

## 생성 명령

PNG 생성:

```bash
node tools/generate-tile-atlas.mjs
```

생성 결과:

```text
assets/atlas/tiles-atlas.png
assets/atlas/tiles-atlas.meta.json
```

WebP 생성은 아래 둘 중 하나가 있으면 자동으로 수행됩니다.

```text
cwebp CLI
npm package sharp
```

WebP 생성 결과:

```text
assets/atlas/tiles-atlas.webp
```

WebP 도구가 없으면 PNG만 생성하고 WebP는 건너뜁니다.

## atlas manifest

```js
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
```

## debug panel

`atlas-loader.js`는 `ATLAS DEBUG` 패널을 생성합니다.

표시 내용:

- active atlas path
- 선택된 format
- WebP/PNG/SVG 검사 결과
- 실패 메시지

## map-engine 연결

`map-engine.js`는 아래 순서로 타일 배경을 선택합니다.

1. `window.YDH_ATLAS.tiles.imageActive`
2. `window.YDH_ATLAS.tiles.image`
3. 기존 `tile.asset`

atlas 선택이 완료되면 `ydh-atlas-ready` 이벤트가 발생하고 map-engine은 현재 맵을 다시 렌더링합니다.

## 주의

- GitHub contents API 작업에서는 binary PNG/WebP 직접 생성이 어렵기 때문에 repo에는 생성 스크립트를 우선 반영했습니다.
- 실제 PNG/WebP 파일은 로컬 또는 서버에서 `node tools/generate-tile-atlas.mjs` 실행 후 커밋하면 됩니다.
- PNG 생성은 Node 기본 모듈만 사용합니다.
- WebP 생성은 `cwebp` 또는 `sharp`가 필요합니다.
