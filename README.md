# YDH Chronicle

YDH Chronicle은 `.github/agents/my-agent.agent.md`의 MMORPG Full-Scope Expert Agent 방향을 바탕으로 만든 **홈페이지형 브라우저 RPG 미니게임**입니다.

별도 설치, 빌드, 서버 없이 `index.html`만 열면 바로 실행됩니다. GitHub Pages에 올리기 좋은 정적 웹 구조입니다.

## 주요 기능

- 레트로 MMORPG 감성 홈페이지 랜딩 화면
- 기본 공격, 5종 스킬, 쿨타임, MP 소모
- 몬스터 웨이브 전투 루프
- 8개 지역 순환 탐험
- EXP/레벨업/HP/MP/공격/방어 성장
- 아이템 드롭 및 자동 인벤토리 기록
- 3마리 처치 퀘스트와 보상
- GM 콘솔 스타일 실시간 전투 로그
- localStorage 기반 자동 저장
- 모바일/PC 반응형 UI

## 실행 방법

```bash
# 저장소를 받은 뒤
open index.html
```

또는 GitHub Pages를 활성화하면 다음 구조로 바로 서비스할 수 있습니다.

```text
/
├── index.html
├── styles.css
├── game.js
└── README.md
```

## 조작법

- `기본 공격` 버튼 또는 `Space`: 기본 공격
- 숫자키 `1 ~ 5`: 스킬 사용
- `물약 사용`: HP 회복
- `마을 휴식`: HP/MP 회복
- `지역 이동`: 다음 지역 몬스터로 전환
- `저장 초기화`: 브라우저 저장 데이터 리셋

## 설계 방향

이 프로젝트는 대규모 MMORPG의 완성 서버가 아니라, 홈페이지에서 바로 보여줄 수 있는 **가벼운 플레이어블 프로토타입**입니다.

추후 확장 후보:

1. 로그인/캐릭터 선택
2. SQLite/MySQL 저장소 연동
3. Java 또는 Node 기반 API 서버
4. 웹소켓 전투 로그
5. GM 운영자 콘솔
6. 몬스터/스킬/아이템 DB 테이블화
7. 로봇 AI 사냥 루틴
8. 실제 서버 런처 페이지

## 파일 설명

| 파일 | 설명 |
| --- | --- |
| `index.html` | 홈페이지/게임 화면 구조 |
| `styles.css` | 반응형 UI, 전투 화면, RPG 테마 스타일 |
| `game.js` | 전투, 성장, 저장, 스킬, 퀘스트 로직 |
| `.github/agents/my-agent.agent.md` | MMORPG 개발 전문 에이전트 문서 |
| `.github/workflows/pages.yml` | GitHub Pages 정적 배포 워크플로 |

## 개발 메모

- 순수 HTML/CSS/JavaScript만 사용했습니다.
- 외부 CDN, npm 패키지, 빌드 도구가 없습니다.
- 저장 데이터는 브라우저 `localStorage`에 저장됩니다.
- UTF-8 기준으로 한글 UI를 구성했습니다.
