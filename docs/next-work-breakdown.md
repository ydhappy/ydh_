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

작업 파일:

- `server/sql/mysql55-schema.sql`
- `server/src/mysql-storage.js`
- `server/src/storage-provider.js`
- `server/src/server.js`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- MySQL 5.5 호환 `ydh_accounts` 테이블 추가
- MySQL 5.5 호환 `ydh_character_slots` 테이블 추가
- 기존 `ydh_save_snapshots` 원본 스냅샷 저장 유지
- 저장 스냅샷 수신 시 계정 정보를 `ydh_accounts`에 upsert
- 저장 스냅샷 수신 시 캐릭터 슬롯 정보를 `ydh_character_slots`에 upsert
- `ydh_accounts.last_snapshot_at` 갱신 처리
- 선택 캐릭터의 level/map_index 요약을 캐릭터 슬롯 테이블에 반영
- `/api/accounts` 계정 목록 API 추가
- `/api/characters` 캐릭터 슬롯 목록 API 추가
- `/api/characters?accountId=...` 특정 계정 캐릭터 조회 추가
- `/api/health`에 MySQL 계정/캐릭터 카운트 포함
- README에 정규화 테이블 및 조회 API 문서화

## 전체 상태

상태: 8차~19차 완료

현재 남은 고도화 후보:

1. WebSocket 위치 동기화
2. Tiled Map Editor JSON import
3. 실제 PNG/WebP atlas 교체
4. 서버 계정 인증 추가
5. 운영용 관리자 저장 삭제/정리 API
6. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
