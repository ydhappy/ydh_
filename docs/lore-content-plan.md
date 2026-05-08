# YDH Chronicle 7차: 소설형 콘텐츠 구현 계획

7차는 작업량이 크므로 한 번에 처리하지 않고 4단계로 분리합니다.

## 7-1차: 세계관 원본 데이터 확정

상태: 완료

추가 파일:

- `data/lore-content.js`
- `docs/lore-content-plan.md`

구성:

- 소설 제목/전제
- 챕터
- 아이템
- 추가 맵
- 추가 NPC
- 추가 몬스터
- 추가 스킬

## 7-2차: 전투 아이템/스킬 연결

목표:

- `game.js`의 하드코딩 스킬 배열에 `YDH_LORE_CONTENT.skills` 병합
- `maybeDropItem()`에서 소설 아이템 드롭 가능
- 드롭 로그에 아이템 등급/스토리 표시
- 까마귀 표식처럼 피해+회복이 있는 혼합 스킬 처리

권장 파일:

- `lore-game-adapter.js`
- 또는 `game.js` 직접 소규모 수정

## 7-3차: 맵/NPC/몬스터 연결

목표:

- `data/maps.js`에 검은 달 폐허/별빛 기록관/거울 늪/심연의 왕좌 추가
- `data/entities.js`에 신규 NPC/몬스터 엔티티 추가
- 기존 SVG 스프라이트 재사용 또는 신규 스프라이트 추가
- `spawnTables`에 신규 맵 연결

권장 순서:

1. 맵 데이터만 추가
2. 스폰 테이블 추가
3. 신규 NPC 대사 연결
4. 신규 몬스터 조우 연결

## 7-4차: 도감/소설 UI 연결

목표:

- 홈페이지에 `Lore Codex` 섹션 추가
- 챕터/아이템/맵/NPC/몬스터/스킬 탭 또는 카드 표시
- 모바일에서도 카드형으로 보기 쉽게 구성

권장 파일:

- `lore-codex.css`
- `lore-codex.js`

## 핵심 원칙

- 한 번에 `game.js`, `maps.js`, `entities.js`, `index.html`을 모두 대량 수정하지 않습니다.
- 데이터 → 어댑터 → UI 순서로 연결합니다.
- 기존 플레이 가능한 상태를 깨지 않는 것을 우선합니다.
- SVG 리소스가 부족한 엔티티는 기존 wolf/goblin/golem/npc 스프라이트를 임시 재사용합니다.
