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

상태: 완료

작업 파일:

- `data/tiled-map-registry.js`
- `tiled-map-bootstrap.js`
- `index.html`
- `docs/tiled-json-import.md`
- `docs/next-work-breakdown.md`

완료 내용:

- 다중 Tiled JSON URL registry 추가
- registry URL 순차 로드 처리 추가
- Tiled JSON 붙여넣기 import textarea 추가
- 붙여넣기 JSON을 Tiled map으로 변환 후 맵 목록에 등록
- custom Tiled map을 localStorage에 보존
- custom map 최대 저장 개수 제한 구조 추가
- 중복 map id 재등록 방지
- 검증 실패/로드 실패/중복 상태를 TILED MAP MANAGER에 표시
- `index.html`에서 `data/tiled-map-registry.js`를 bootstrap 전에 로드하도록 연결

### 22-5: Tiled Object Layer 배치 데이터 추출/표시

상태: 완료

작업 파일:

- `data/tiled-map-loader.js`
- `data/tiled/moon-gate-sample.json`
- `gm-console.js`
- `docs/tiled-json-import.md`
- `docs/next-work-breakdown.md`

완료 내용:

- Tiled objectgroup layer 탐색 함수 추가
- Object Layer의 object를 tile 좌표 기반 placement 데이터로 변환
- object property `ydhKind`, `ydhEntityId`, `ydhName`, `ydhTargetMapId`, `ydhTargetMapIndex`, `ydhDialogue` 지원
- 변환 결과에 `map.placements`, `map.tiled.objectLayers`, `map.tiled.placementSummary` 추가
- 샘플 Tiled JSON에 npc/monster/portal object 예시 추가
- GM 콘솔에 map source/sourceUrl 표시
- GM 콘솔에 Object 총 개수 및 kind별 요약 표시
- GM 콘솔에 Object 이름/종류/좌표/entityId 표시
- GM 콘솔 `Object 강조` 버튼 추가
- 맵 정보 복사에 Object 목록 포함

### 22-6: Object Layer 실제 배치 적용

상태: 완료

작업 파일:

- `data/tiled-map-loader.js`
- `map-engine.js`
- `gm-console.js`
- `docs/tiled-json-import.md`
- `docs/next-work-breakdown.md`

완료 내용:

- Object Layer placement를 실제 `rows` 타일 코드에 합성
- `npc` object를 `N` 타일로 반영
- `monster` object를 `M` 타일로 반영
- `portal` object를 `P` 타일로 반영
- 원본 tilelayer rows를 `baseRows`로 보관
- 적용된 placement를 `appliedPlacements`로 보관
- 제외된 placement를 `skippedPlacements`로 보관
- 맵 엔진에서 Object Layer `ydhEntityId` 기반 NPC/몬스터 스프라이트 선택
- 맵 엔진에서 Object Layer `ydhDialogue` 기반 NPC 대사 출력
- 맵 엔진에서 Object Layer `ydhTargetMapIndex`/`ydhTargetMapId` 기반 포탈 이동
- GM 콘솔에 적용/제외 placement 수 표시
- GM 콘솔 맵 정보 복사에 applied/skipped placement 포함

### 22-7: custom map 관리 UI/서버 저장 연동

상태: 완료

작업 파일:

- `server/src/map-storage.js`
- `server/src/server.js`
- `tiled-map-bootstrap.js`
- `server/README.md`
- `docs/tiled-json-import.md`
- `docs/next-work-breakdown.md`

완료 내용:

- 서버 custom map 파일 저장소 추가
- `server/data/custom-maps.json` 저장 구조 추가
- `GET /api/maps/custom` 목록 API 추가
- `POST /api/maps/custom` 저장 API 추가
- `GET /api/maps/custom/:id` 단건 조회 API 추가
- `DELETE /api/maps/custom/:id` 삭제 API 추가
- `/api/health` 응답에 custom map 저장 상태 추가
- TILED MAP MANAGER에 `내보내기` 버튼 추가
- localStorage custom map `삭제` 버튼 추가
- localStorage custom map `서버저장` 버튼 추가
- `서버 맵 목록` 조회 버튼 추가
- custom/server map 카운트 표시 추가

### 22-8A: 서버 custom map 가져오기/삭제 UI

상태: 완료

작업 파일:

- `tiled-map-bootstrap.js`
- `docs/tiled-json-import.md`
- `docs/next-work-breakdown.md`

완료 내용:

- 서버 custom map 요약 목록을 별도 `서버 맵` 섹션으로 표시
- `GET /api/maps/custom/:id`로 서버 저장 맵 전체 데이터를 받아 클라이언트에 import
- 가져온 서버 맵을 `YDH_MAPS.maps`에 등록
- 가져온 서버 맵을 localStorage custom map으로 보존
- 서버 맵 카드에 `클라이언트로 가져오기` 버튼 추가
- 서버 맵 카드에 `서버삭제` 버튼 추가
- `DELETE /api/maps/custom/:id` 호출 후 서버 목록에서 제거
- 검증 실패/서버 API 실패를 `실패 상세 보기`로 표시

### 22-8B-1: Object Layer marker 정보창

상태: 완료

작업 파일:

- `map-engine.js`
- `map.css`
- `data/tiled/moon-gate-sample.json`
- `docs/tiled-json-import.md`
- `docs/next-work-breakdown.md`

완료 내용:

- Object Layer `ydhKind=marker` 배치 탐색 추가
- marker를 rows 타일 코드로 합성하지 않고 별도 UI 배지로 표시
- marker 타일 우측 상단 `ⓘ` 아이콘 표시
- marker 클릭 시 `map-marker-info` 정보창 표시
- 정보창에 marker 이름, 좌표, layer, kind, id, dialogue 표시
- marker 클릭 시 타일 이동 이벤트가 발생하지 않도록 분리
- 모바일 대응 정보창 스타일 추가
- 샘플 Tiled JSON에 `달문 석비` marker object 추가

### 22-8B-2: Tiled object layer 기반 퀘스트 트리거

상태: 대기

목표:

- marker 기반 퀘스트 시작/완료 조건
- Tiled object layer 기반 퀘스트 트리거
- 서버 custom map 자동 동기화 옵션
- 서버 custom map을 계정/캐릭터별로 분리 저장
- custom map MySQL 저장소 연동

## 전체 상태

상태: 8차~21차 완료, 22차 1~8B-1단계 완료

현재 남은 고도화 후보:

1. 22-8B-2 Tiled object layer 기반 퀘스트 트리거
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
