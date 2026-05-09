# YDH Chronicle 남은 작업 분할 계획

## 8차~23차

상태: 완료

요약:

- 소설 챕터 퀘스트 시스템 완료
- 아이템/장착/도감/GM 콘솔 완료
- 계정/캐릭터 선택 완료
- Node/Express 저장 서버 완료
- MySQL 5.5 저장소 완료
- 서버 저장 슬롯 복원 완료
- WebSocket 위치 동기화 완료
- 실시간 타일맵 원격 아바타 렌더링 완료
- Tiled Map Editor JSON import 완료
- 서버 custom map 자동 동기화, scope 분리, MySQL 저장소 완료

## 24차: 실제 PNG/WebP atlas 교체

상태: 분할 진행 중

### 24-1A: atlas 매니페스트와 타일 렌더러 지원

상태: 완료

작업 파일:

- `assets/atlas/tiles-atlas.svg`
- `data/atlas.js`
- `map-engine.js`
- `index.html`

완료 내용:

- 단일 타일 atlas fallback SVG 추가
- atlas manifest 추가
- `G/R/S/T/W/P` atlas 좌표 정의
- `M/N` base tile + marker 처리
- `map-engine.js` atlas 렌더링 지원
- atlas 미지원 시 기존 개별 SVG fallback 유지

### 24-1B: PNG/WebP atlas 생성 스크립트와 우선 로더

상태: 완료

작업 파일:

- `tools/generate-tile-atlas.mjs`
- `atlas-loader.js`
- `data/atlas.js`
- `map-engine.js`
- `index.html`
- `docs/atlas-pipeline.md`

완료 내용:

- Node 기본 모듈 기반 `tiles-atlas.png` 생성 스크립트 추가
- `cwebp` 또는 `sharp`가 있으면 `tiles-atlas.webp` 자동 생성
- `tiles-atlas.meta.json` 생성 지원
- WebP → PNG → SVG 순서로 atlas 이미지 검사
- 선택된 atlas를 `window.YDH_ATLAS.tiles.imageActive`에 저장
- 선택 format을 `window.YDH_ATLAS.tiles.activeFormat`에 저장
- `ydh-atlas-ready` 이벤트 발행
- map-engine이 atlas ready 시 현재 맵 재렌더링
- `ATLAS DEBUG` 패널 추가
- atlas 생성/로더 문서 추가

### 24-1C: binary atlas 생성 workflow 및 품질 비교

상태: 완료

작업 파일:

- `.github/workflows/generate-atlas.yml`
- `docs/atlas-pipeline.md`
- `docs/next-work-breakdown.md`

완료 내용:

- GitHub Actions 수동 workflow 추가
- `node tools/generate-tile-atlas.mjs` 실행 자동화
- Ubuntu runner에서 `webp` 패키지 설치 후 WebP 생성 지원
- `commit_binaries=true` 선택 시 PNG/WebP/meta 파일 자동 commit
- atlas 예상 용량 기준 문서화
- WebP/PNG/SVG fallback 검증 기준 문서화

### 24-1D: atlas 품질 검사 및 workflow 검증 단계

상태: 완료

작업 파일:

- `tools/check-atlas-quality.mjs`
- `.github/workflows/generate-atlas.yml`
- `docs/atlas-pipeline.md`
- `docs/next-work-breakdown.md`

완료 내용:

- atlas 품질 검사 스크립트 추가
- PNG signature 검사 추가
- PNG 크기 384x64 검사 추가
- PNG 용량 24KB 이하 검사 추가
- meta JSON 크기/grid/tile order 검사 추가
- WebP가 생성된 경우 RIFF/WEBP signature 검사 추가
- WebP가 생성된 경우 용량 16KB 이하 검사 추가
- `tiles-atlas.quality.json` 리포트 생성 추가
- GitHub Actions workflow에 `Validate atlas quality` 단계 추가
- 자동 commit 대상에 `tiles-atlas.quality.json` 추가

### 24-1E: atlas 색감/디테일 조정 config

상태: 완료

작업 파일:

- `assets/atlas/tile-atlas-config.json`
- `tools/generate-tile-atlas.mjs`
- `tools/check-atlas-quality.mjs`
- `docs/atlas-pipeline.md`
- `docs/next-work-breakdown.md`

완료 내용:

- atlas 색감/디테일 조정용 config 추가
- generator가 config의 tile size, grid, WebP quality, tile palette를 읽도록 변경
- grass/road/stone/tree/water/portal 디테일 수치를 config화
- quality checker가 config의 tile size, grid, tile order, 용량 제한을 읽도록 변경
- Actions 직접 dispatch는 현재 도구에서 불가하여 실행 후 결과 확인은 다음 수동 실행 단계로 분리

## 전체 상태

상태: 8차~23차 완료, 24-1A~24-1E 완료

현재 남은 고도화 후보:

1. 24-1F GitHub Actions 수동 실행 후 생성 결과 확인
2. 서버 계정 인증 추가
3. 운영용 관리자 저장 삭제/정리 API
4. 원격 아바타 클릭 정보창
5. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회
6. custom map 파일→MySQL 마이그레이션 도구

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
