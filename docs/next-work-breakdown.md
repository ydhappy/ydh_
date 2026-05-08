# YDH Chronicle 남은 작업 분할 계획

7차 소설 콘텐츠 연결 이후 남은 작업은 아래처럼 작게 나눠 진행합니다.

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

작업 파일:

- `server/package.json`
- `server/src/server.js`
- `server/src/storage.js`
- `server/src/validation.js`
- `server/README.md`

완료 내용:

- Express 기반 저장 API 서버 추가
- 정적 웹 파일 서빙 지원
- `/api/health` 상태 확인 API 추가
- `/api/save/snapshot` 전체 저장 스냅샷 수신 API 추가
- `/api/save/character` 캐릭터 단독 저장 API 추가
- `/api/save/list` 저장 목록 조회 API 추가
- `/api/save/restore` 최신 저장 복원 API 추가
- 파일 기반 저장소 `server/data/saves.json` 구조 추가
- 최근 50개 저장 스냅샷 보관
- 저장 스냅샷 기본 검증/요약 처리 추가
- 서버 실행 문서 추가

## 15차: 서버 저장 복원 UI 연결

상태: 완료

작업 파일:

- `server-sync.js`
- `server-sync-panel.js`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- `/api/save/list` 클라이언트 호출 함수 추가
- `/api/save/restore` 최신 저장 복원 함수 추가
- 서버 저장 snapshot을 브라우저 localStorage에 적용하는 복원 함수 추가
- 서버연동 패널에 `서버 저장목록` 버튼 추가
- 서버연동 패널에 `최신 저장 복원` 버튼 추가
- 서버연동 패널에 `복원 후 새로고침` 버튼 추가
- 서버 README에 브라우저 저장/복원 흐름 문서화

## 전체 상태

상태: 8차~15차 완료

현재 남은 고도화 후보:

1. 계정 로그인/캐릭터 선택 화면
2. 서버 DB 저장/불러오기 고도화: SQLite 또는 MariaDB
3. WebSocket 위치 동기화
4. Tiled Map Editor JSON import
5. 실제 PNG/WebP atlas 교체
6. 서버 저장 슬롯 선택 복원 UI

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
