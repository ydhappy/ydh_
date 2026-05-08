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

작업 파일:

- `account-character.css`
- `account-character.js`
- `data/save-schema.js`
- `server-sync.js`
- `server/src/storage.js`
- `server/src/validation.js`
- `server/README.md`
- `index.html`
- `docs/next-work-breakdown.md`

완료 내용:

- 로컬 계정 표시 이름 저장 UI 추가
- 캐릭터 슬롯 3개 구조 추가
- 기사/마법사/도적/사제 클래스 선택 추가
- 캐릭터 생성/선택/삭제 UI 추가
- 캐릭터 선택 시 해당 캐릭터 기준 저장 데이터 준비
- 저장 스냅샷에 account, selectedCharacter, characterSlots 포함
- 서버 저장 목록 요약에 accountName, characterName, classId, slotCount 포함
- 서버 snapshot 검증/요약에 계정/캐릭터 필드 반영
- 상단 메뉴에 `계정` 링크 추가

## 전체 상태

상태: 8차~16차 완료

현재 남은 고도화 후보:

1. 서버 DB 저장/불러오기 고도화: SQLite 또는 MariaDB
2. WebSocket 위치 동기화
3. Tiled Map Editor JSON import
4. 실제 PNG/WebP atlas 교체
5. 서버 저장 슬롯 선택 복원 UI
6. 서버 계정 인증 추가

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
