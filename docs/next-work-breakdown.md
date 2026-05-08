# YDH Chronicle 남은 작업 분할 계획

7차 소설 콘텐츠 연결 이후 남은 작업은 아래처럼 작게 나눠 진행합니다.

## 8차: 소설 챕터 퀘스트 시스템

상태: 완료

목표:

- 소설 챕터와 실제 플레이 목표 연결
- 방문, NPC 대화, 몬스터 처치, 아이템 획득 기반 진행도 저장
- 모바일에서 볼 수 있는 챕터 퀘스트 UI 추가

작업 파일:

- `data/chapter-quests.js`
- `chapter-quests.css`
- `chapter-quests.js`
- `index.html`

완료 내용:

- 챕터별 퀘스트 데이터 추가
- 방문/NPC대화/몬스터처치/아이템획득 목표 타입 정의
- localStorage 기반 퀘스트 진행도 저장
- 모바일 카드형 챕터 퀘스트 UI 추가
- 맵 이동/NPC 대화/전투 로그 기반 일부 자동 진행 감지

## 9차: 아이템 등급/장착 슬롯 UI

상태: 완료

작업 파일:

- `data/item-catalog.js`
- `item-equipment.css`
- `item-equipment.js`
- `index.html`

완료 내용:

- 기본 아이템/소설 아이템 통합 카탈로그 추가
- 아이템 등급/슬롯 메타데이터 추가
- 인벤토리 타입/등급/능력치 추론
- 등급별 색상/테두리 인벤토리 표시
- 슬롯별 최고 점수 장비 후보 자동 표시
- 모바일 필터 버튼 추가

## 10차: 도감 해금 시스템

상태: 완료

작업 파일:

- `data/codex-unlocks.js`
- `codex-unlocks.css`
- `lore-codex.js`
- `index.html`

완료 내용:

- 도감 해금 상태 저장소 추가
- localStorage 기반 해금 상태 저장
- 기본 해금 항목 설정
- NPC/맵/몬스터/아이템/스킬 기반 자동 해금
- 도감 카드 잠금/해금 UI 적용
- 분류별 해금 진행률 표시

## 11차: GM 맵/스폰 관리 콘솔

상태: 완료

작업 파일:

- `gm-console.css`
- `gm-console.js`
- `index.html`

완료 내용:

- 화면 우하단 `GM MAP` 플로팅 토글 추가
- 현재 맵/좌표/타일/스폰 후보/타일 통계 표시
- 스폰 타일 강조/해제 기능 추가
- 현재 맵 정보 클립보드 복사 기능 추가

## 12차: 전용 소설 NPC/몬스터 16방향 SVG 제작

상태: 완료

작업 파일:

- `assets/sprites/npc-raven-archivist-16dir.svg`
- `assets/sprites/npc-moon-priestess-16dir.svg`
- `assets/sprites/npc-broken-smith-16dir.svg`
- `assets/sprites/npc-silent-porter-16dir.svg`
- `assets/sprites/monster-moon-stalker-16dir.svg`
- `assets/sprites/monster-ink-wraith-16dir.svg`
- `assets/sprites/monster-mirror-witch-16dir.svg`
- `assets/sprites/monster-abyss-knight-16dir.svg`
- `assets/sprites/monster-black-moon-lord-16dir.svg`
- `data/entities.js`

완료 내용:

- 소설 NPC 4종 전용 16방향 SVG 추가
- 소설 몬스터 5종 전용 16방향 SVG 추가
- `data/entities.js`에서 재사용 스프라이트 경로를 전용 경로로 교체

## 13차: 서버/API 연동 준비

상태: 완료

작업 파일:

- `data/save-schema.js`
- `server-sync.js`
- `server-sync.css`
- `server-sync-panel.js`
- `docs/server-api-integration.md`
- `index.html`

완료 내용:

- localStorage 저장 키를 서버 모델 기준으로 정리
- 캐릭터/맵/퀘스트/도감 저장 스냅샷 생성 어댑터 추가
- `/api/save/snapshot` 전송 테스트 함수 추가
- JSON 스냅샷 보기/내보내기/클립보드 복사 UI 추가
- 서버 전송 테스트 UI 추가
- Java/Spring Boot record 예시 문서화
- Node/Express endpoint 예시 문서화
- DB 테이블 후보 문서화

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

## 전체 상태

상태: 8차~14차 완료

현재 남은 고도화 후보:

1. 계정 로그인/캐릭터 선택 화면
2. 서버 DB 저장/불러오기 고도화: SQLite 또는 MariaDB
3. WebSocket 위치 동기화
4. Tiled Map Editor JSON import
5. 실제 PNG/WebP atlas 교체
6. 서버 저장 복원 UI를 클라이언트에 연결

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
