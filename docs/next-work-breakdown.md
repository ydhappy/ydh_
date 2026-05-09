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

상태: 완료

## 23차: 서버 custom map 고도화

상태: 분할 진행 중

### 23-1: 서버 custom map 자동 동기화 옵션

상태: 완료

작업 파일:

- `server-custom-map-sync.js`
- `index.html`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- 기존 TILED MAP MANAGER를 크게 수정하지 않고 별도 자동 동기화 모듈 추가
- `SERVER CUSTOM MAP SYNC` 패널 추가
- `지금 동기화` 버튼 추가
- `자동 동기화` 체크 옵션 추가
- 자동 동기화 설정을 `ydh-server-custom-map-auto-sync-v1` localStorage key에 저장
- `GET /api/maps/custom`로 서버 custom map 목록 조회
- `GET /api/maps/custom/:id`로 전체 map 데이터 조회
- 서버 map을 `YDH_MAPS.maps`에 import
- 서버 map을 localStorage custom map에도 저장
- 같은 map id가 이미 있으면 맵 목록 중복 추가 없이 localStorage만 갱신
- 동기화 결과 요약 표시: 서버 수, 신규 import 수, 로컬 갱신 수, 실패 수
- `window.YDH_SERVER_CUSTOM_MAP_SYNC.syncNow()` 공개
- `ydh-server-custom-maps-synced` 이벤트 발행

## 전체 상태

상태: 8차~22차 완료, 23-1 완료

현재 남은 고도화 후보:

1. 23-2 서버 custom map을 계정/캐릭터별로 분리 저장
2. custom map MySQL 저장소 연동
3. 실제 PNG/WebP atlas 교체
4. 서버 계정 인증 추가
5. 운영용 관리자 저장 삭제/정리 API
6. 원격 아바타 클릭 정보창
7. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
