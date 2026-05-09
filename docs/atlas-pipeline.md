# Atlas Pipeline

## 상태

현재 24-1A ~ 24-1E까지 완료된 상태입니다.

- 24-1A: atlas 매니페스트와 타일 렌더러 지원 완료
- 24-1B: PNG/WebP 생성 스크립트와 우선 로더 완료
- 24-1C: GitHub Actions binary 생성 workflow 완료
- 24-1D: atlas 품질 검사 스크립트와 workflow 검증 단계 완료
- 24-1E: atlas 색감/디테일 조정 config와 generator 연동 완료

## 주요 파일

```text
assets/atlas/tiles-atlas.svg
assets/atlas/tile-atlas-config.json
data/atlas.js
atlas-loader.js
tools/generate-tile-atlas.mjs
tools/check-atlas-quality.mjs
.github/workflows/generate-atlas.yml
```

## 생성/검사

```bash
node tools/generate-tile-atlas.mjs
node tools/check-atlas-quality.mjs
```

생성 결과:

```text
assets/atlas/tiles-atlas.png
assets/atlas/tiles-atlas.webp
assets/atlas/tiles-atlas.meta.json
assets/atlas/tiles-atlas.quality.json
```

## atlas 우선순위

```text
WebP → PNG → SVG
```

선택된 atlas는 아래 값으로 확인합니다.

```js
window.YDH_ATLAS.tiles.imageActive
window.YDH_ATLAS.tiles.activeFormat
```

## 색감/디테일 조정

색감과 디테일은 아래 파일에서 조정합니다.

```text
assets/atlas/tile-atlas-config.json
```

조정 항목:

```text
webpQuality
qualityLimits.maxPngBytes
qualityLimits.maxWebpBytes
style.noiseStrength
style.gradientStrength
style.grassBlades
style.waterWaves
style.portalSparkles
style.stoneHighlight
style.roadCurve
style.treeCanopyRadius
tiles[].fill
tiles[].accent
tiles[].detail
```

품질 검사 스크립트는 config의 tile size, grid, tile order, 용량 제한을 읽어서 검사합니다.

## GitHub Actions

수동 workflow:

```text
Generate Tile Atlas
```

실행 경로:

```text
Actions → Generate Tile Atlas → Run workflow → commit_binaries=true
```

workflow는 atlas 생성, 품질 검사, 결과 파일 commit을 수행합니다.

## 검증 기준

```text
PNG signature 정상
PNG 크기 config 기준 일치
PNG 용량 제한 통과
meta JSON grid/order 일치
WebP가 있으면 RIFF/WEBP signature 정상
WebP 용량 제한 통과
브라우저는 WebP, PNG, SVG 순서로 fallback
```
