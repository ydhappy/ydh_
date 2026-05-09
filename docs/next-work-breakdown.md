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

상태: 완료

### 24-1A: atlas 매니페스트와 타일 렌더러 지원

상태: 완료

작업 파일:

- `assets/atlas/tiles-atlas.svg`
- `data/atlas.js`
- `map-engine.js`
- `index.html`

### 24-1B: PNG/WebP atlas 생성 스크립트와 우선 로더

상태: 완료

작업 파일:

- `tools/generate-tile-atlas.mjs`
- `atlas-loader.js`
- `data/atlas.js`
- `map-engine.js`
- `index.html`
- `docs/atlas-pipeline.md`

### 24-1C: binary atlas 생성 workflow 및 품질 비교

상태: 완료

작업 파일:

- `.github/workflows/generate-atlas.yml`
- `docs/atlas-pipeline.md`
- `docs/next-work-breakdown.md`

### 24-1D: atlas 품질 검사 및 workflow 검증 단계

상태: 완료

작업 파일:

- `tools/check-atlas-quality.mjs`
- `.github/workflows/generate-atlas.yml`
- `docs/atlas-pipeline.md`
- `docs/next-work-breakdown.md`

### 24-1E: atlas 색감/디테일 조정 config

상태: 완료

작업 파일:

- `assets/atlas/tile-atlas-config.json`
- `tools/generate-tile-atlas.mjs`
- `tools/check-atlas-quality.mjs`
- `docs/atlas-pipeline.md`
- `docs/next-work-breakdown.md`

### 24-1F: binary atlas readiness 표시

상태: 완료

작업 파일:

- `atlas-loader.js`
- `docs/next-work-breakdown.md`

완료 내용:

- ATLAS DEBUG 패널에 binary 생성 완료 여부 표시
- WebP/PNG 사용 시 `BINARY READY` 표시
- SVG fallback만 있을 때 `SVG FALLBACK ONLY` 표시
- fallback 상태에서 Actions 실행 안내 표시
- `ydh-atlas-ready` 이벤트 detail에 `binaryReady`, `fallbackOnly` 추가

## 전체 상태

상태: 8차~24차 완료

현재 남은 고도화 후보:

1. 25-1 서버 계정 인증 추가
2. 운영용 관리자 저장 삭제/정리 API
3. 원격 아바타 클릭 정보창
4. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회
5. custom map 파일→MySQL 마이그레이션 도구

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
