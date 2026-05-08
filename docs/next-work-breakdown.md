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

작업 파일:

- `server/src/storage.js`
- `server/src/mysql-storage.js`
- `server/src/storage-provider.js`
- `server/src/server.js`
- `server-sync.js`
- `server-sync-panel.js`
- `server-sync.css`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- 파일 저장소에 `snapshotById(id)` 추가
- MySQL 5.5 저장소에 `snapshotById(id)` 추가
- 저장소 provider에 ID 기반 조회 함수 추가
- 서버 API `GET /api/save/:id` 추가
- 클라이언트 `restoreSnapshotById(id)` 추가
- 서버 저장목록을 카드형 UI로 표시
- 각 저장 카드에 `복원`, `복원+새로고침` 버튼 추가
- 저장 카드에 계정명, 캐릭터명, 클래스, 레벨, 맵 인덱스, 저장 시각 표시
- 모바일 대응 저장 카드 스타일 추가
- README에 선택 저장 복원 흐름 문서화

## 전체 상태

상태: 8차~18차 완료

현재 남은 고도화 후보:

1. MySQL 계정/캐릭터 정규화 테이블 분리
2. WebSocket 위치 동기화
3. Tiled Map Editor JSON import
4. 실제 PNG/WebP atlas 교체
5. 서버 계정 인증 추가
6. 운영용 관리자 저장 삭제/정리 API

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
