# YDH Chronicle 남은 작업 분할 계획

## 8차: 소설 챕터 퀘스트 시스템

상태: 완료

## 9차: 아이템 등급/장착 슬롯 UI

상태: 완료

## 10차: 도감 해금 시스템

상태: 완료

## 11차: GM 맵/스폰 관리 콘솔

상태: 완료

## 12차: 전용 소설 NPC/몬스터 16방향 SVG 제작

상태: 완료

## 13차: 서버/API 연동 준비

상태: 완료

## 14차: 실제 Node/Express 저장 서버 구현

상태: 완료

## 15차: 서버 저장 복원 UI 연결

상태: 완료

## 16차: 계정/캐릭터 선택 화면

상태: 완료

## 17차: MySQL 5.5 저장소 고도화

상태: 완료

## 18차: 서버 저장 슬롯 선택 복원 UI

상태: 완료

## 19차: MySQL 계정/캐릭터 정규화 테이블 분리

상태: 완료

## 20차: WebSocket 위치 동기화

상태: 완료

## 21차: 실시간 타일맵 원격 플레이어 아바타 렌더링

상태: 완료

## 22차: Tiled Map Editor JSON import

상태: 분할 진행 중

### 22-1: Tiled JSON 샘플맵 + 변환기

상태: 완료

작업 파일:

- `data/tiled/moon-gate-sample.json`
- `data/tiled-map-loader.js`
- `docs/tiled-json-import.md`

완료 내용:

- 8x6 소형 Tiled JSON 샘플맵 추가
- Tiled tileset `ydhCode` property 기반 GID → YDH 타일 코드 변환 추가
- Tiled map property 기반 id/name/description/start/portalTo 변환 추가
- `loadFromUrl`, `convert`, `appendToYdhMaps` 변환 API 추가
- Tiled 작성 규칙 문서화

### 22-2: Tiled JSON 자동 로드 연결

상태: 완료

작업 파일:

- `tiled-map-bootstrap.js`
- `index.html`
- `docs/tiled-json-import.md`
- `docs/next-work-breakdown.md`

완료 내용:

- `index.html`에 `data/tiled-map-loader.js` 연결
- `index.html`에 `tiled-map-bootstrap.js` 연결
- 로드 순서를 `data/maps.js` → `data/tiled-map-loader.js` → `tiled-map-bootstrap.js` → `map-engine.js`로 정리
- 샘플 Tiled JSON을 `YDH_MAPS.maps`에 안전하게 append
- 중복 맵은 재추가하지 않도록 처리
- 로드 실패 시 기존 문자 타일맵 유지
- Tiled 로드 성공/실패를 게임 로그 이벤트로 전달
- Assets/Map 설명 문구를 Tiled JSON 지원 기준으로 갱신

### 22-3: Tiled 맵 선택/검증 UI

상태: 완료

작업 파일:

- `tiled-map-bootstrap.js`
- `docs/tiled-json-import.md`
- `docs/next-work-breakdown.md`

완료 내용:

- Tiled 인라인 fallback 맵 동기 등록 추가
- fetch 기반 Tiled JSON 로드 검증 추가
- Tiled 맵 선택 카드 UI 추가
- 선택한 맵의 시작 좌표로 이동 후 새로고침 기능 추가
- 전체 맵/Tiled 맵 개수 배지 추가
- 타일 코드 검증 추가
- 시작 좌표 범위/이동 가능 여부 검증 추가
- 중복 맵 ID 목록 표시 추가
- 실패 맵 목록 표시 추가
- 기존 문자 타일맵 유지

### 22-4: 다중 Tiled JSON 등록/관리 고도화

상태: 대기

목표:

- 여러 개의 Tiled JSON URL 목록 관리
- 외부 Tiled JSON 업로드/붙여넣기 import
- GM 콘솔에 source/sourceUrl 표시
- 검증 실패 맵 상세 보기
- Tiled object layer 기반 NPC/몬스터/포탈 배치

## 전체 상태

상태: 8차~21차 완료, 22차 1~3단계 완료

현재 남은 고도화 후보:

1. 22-4 다중 Tiled JSON 등록/관리 고도화
2. 실제 PNG/WebP atlas 교체
3. 서버 계정 인증 추가
4. 운영용 관리자 저장 삭제/정리 API
5. 원격 아바타 클릭 정보창
6. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
