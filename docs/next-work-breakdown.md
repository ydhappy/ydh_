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

작업 파일:

- `server/package.json`
- `server/sql/mysql55-schema.sql`
- `server/src/mysql-storage.js`
- `server/src/storage-provider.js`
- `server/src/server.js`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- `mysql2` dependency 추가
- MySQL 5.5 호환 schema SQL 추가
- MySQL 5.5 미지원 JSON 타입 대신 `LONGTEXT` JSON 문자열 저장 방식 적용
- `ydh_save_snapshots` 저장 테이블 추가
- `ydh_schema_meta` schema version 테이블 추가
- MySQL 저장 provider 추가
- 파일 저장소와 MySQL 저장소를 `YDH_STORAGE` 환경변수로 선택 가능하게 구성
- `/api/health` 응답에 `storageMode` 추가
- `/api/save/snapshot`, `/api/save/list`, `/api/save/restore`가 선택된 저장소 provider를 사용하도록 변경
- MySQL 5.5 실행 방법 문서화

## 전체 상태

상태: 8차~17차 완료

현재 남은 고도화 후보:

1. 서버 저장 슬롯 선택 복원 UI
2. MySQL 계정/캐릭터 정규화 테이블 분리
3. WebSocket 위치 동기화
4. Tiled Map Editor JSON import
5. 실제 PNG/WebP atlas 교체
6. 서버 계정 인증 추가

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
