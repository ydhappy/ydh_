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

작업 파일:

- `map-engine.js`
- `realtime-sync.js`
- `realtime-map-peers.css`
- `realtime-map-peers.js`
- `index.html`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- 맵 타일 DOM에 `data-x`, `data-y`, `data-map-index`, `data-map-id` 좌표 메타데이터 추가
- 맵 렌더 완료 이벤트 `ydh-map-rendered` 추가
- 현재 맵 상태 전역값 `window.YDH_CURRENT_MAP_STATE` 추가
- 실시간 peer 목록 전역값 `window.YDH_REALTIME_PEERS` 추가
- 실시간 peer 변경 이벤트 `ydh-realtime-peers-updated` 추가
- 같은 `mapIndex`에 있는 원격 플레이어만 타일맵 위에 표시
- 원격 플레이어 스프라이트/이름표 표시
- 같은 타일에 여러 원격 플레이어가 있으면 `+N` 배지 표시
- 원격 플레이어 타일 강조 스타일 추가
- README에 원격 아바타 테스트 흐름 문서화

## 전체 상태

상태: 8차~21차 완료

현재 남은 고도화 후보:

1. Tiled Map Editor JSON import
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
