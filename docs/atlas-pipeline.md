# Atlas Pipeline

## 상태

현재 24-1A ~ 24-1C까지 완료된 상태입니다.

- 24-1A: atlas 매니페스트와 타일 렌더러 지원 완료
- 24-1B: PNG/WebP 생성 스크립트 + WebP/PNG/SVG 우선 로더 완료
- 24-1C: GitHub Actions atlas binary 생성/커밋 workflow + 품질 비교 기준 완료

## 파일

```text
assets/atlas/tiles-atlas.svg
data/atlas.js
atlas-loader.js
tools/generate-tile-atlas.mjs
.github/workflows/generate-atlas.yml
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

## 로컬 생성 명령

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

## GitHub Actions 생성

수동 실행 workflow:

```text
.github/workflows/generate-atlas.yml
```

실행 방법:

1. GitHub repository 접속
2. Actions 탭 선택
3. `Generate Tile Atlas` workflow 선택
4. `Run workflow` 클릭
5. `commit_binaries=true` 선택
6. 실행 완료 후 아래 파일이 자동 commit 되는지 확인

자동 commit 대상:

```text
assets/atlas/tiles-atlas.png
assets/atlas/tiles-atlas.webp
assets/atlas/tiles-atlas.meta.json
```

## 품질/용량 기준

현재 generator와 동일한 384x64 atlas 기준 예상값:

```text
PNG  약 12~13 KB
WebP 약 5~6 KB
SVG  텍스트 기반 fallback
```

검증 기준:

- 타일 크기: 64x64
- atlas 크기: 384x64
- columns: 6
- rows: 1
- tile order: G, R, S, T, W, P
- WebP가 있으면 WebP가 우선 선택되어야 함
- WebP가 없고 PNG가 있으면 PNG가 선택되어야 함
- 둘 다 없으면 SVG fallback이 선택되어야 함

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

- GitHub contents API 직접 작업에서는 binary PNG/WebP 파일 경로 commit이 제한될 수 있어 workflow를 추가했습니다.
- 실제 binary 파일은 `node tools/generate-tile-atlas.mjs` 또는 GitHub Actions `Generate Tile Atlas`로 생성합니다.
- PNG 생성은 Node 기본 모듈만 사용합니다.
- WebP 생성은 `cwebp` 또는 `sharp`가 필요합니다.
