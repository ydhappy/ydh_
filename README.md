# YDH Chronicle

YDH Chronicle은 `.github/agents/my-agent.agent.md`의 MMORPG Full-Scope Expert Agent 방향을 바탕으로 만든 **모바일 대응 홈페이지형 브라우저 RPG 미니게임**입니다.

별도 설치, 빌드, 서버 없이 `index.html`만 열면 바로 실행됩니다. GitHub Pages에 올리기 좋은 정적 웹 구조이며, 모바일 터치 조작과 PC 키보드 조작을 함께 지원합니다.

## 주요 기능

- 레트로 MMORPG 감성 홈페이지 랜딩 화면
- 기본 공격, 5종 스킬, 쿨타임, MP 소모
- 몬스터 웨이브 전투 루프
- 3개 기본 타일맵: 말하는 섬, 은빛 숲, 버려진 광산
- 모바일 방향 버튼, 인접 타일 클릭, PC WASD/방향키 이동
- 타일 충돌 처리: 나무/물/벽 이동 불가
- 포탈 타일을 통한 맵 이동
- NPC 타일 안내 이벤트
- 몬스터 구역 랜덤 조우 이벤트
- EXP/레벨업/HP/MP/공격/방어 성장
- 아이템 드롭 및 자동 인벤토리 기록
- 3마리 처치 퀘스트와 보상
- GM 콘솔 스타일 실시간 전투/맵 로그
- localStorage 기반 자동 저장
- 모바일/PC 반응형 UI

## 실행 방법

```bash
# 저장소를 받은 뒤
open index.html
```

또는 GitHub Pages를 활성화하면 정적 웹 게임으로 바로 서비스할 수 있습니다.

## 조작법

### 전투

- `기본 공격` 버튼 또는 `Space`: 기본 공격
- 숫자키 `1 ~ 5`: 스킬 사용
- `물약 사용`: HP 회복
- `마을 휴식`: HP/MP 회복
- `전투 지역 이동`: 전투 몬스터 지역 전환
- `저장 초기화`: 브라우저 저장 데이터 리셋

### 맵

- 모바일: 화면의 `▲ ◀ ▼ ▶` 버튼
- PC: `WASD` 또는 방향키
- 마우스/터치: 현재 위치와 인접한 타일 클릭
- `🌀` 포탈: 다음 맵으로 이동
- `💬` NPC: 안내 이벤트
- `👹` 몬스터 구역: 조우 이벤트

## 현재 프로젝트 구조

```text
/
├── index.html
├── styles.css
├── map.css
├── game.js
├── map-engine.js
├── game-map-bridge.js
├── data/
│   └── maps.js
├── assets/
│   ├── README.md
│   ├── tiles/
│   │   ├── grass.svg
│   │   ├── road.svg
│   │   ├── water.svg
│   │   ├── tree.svg
│   │   ├── stone.svg
│   │   └── portal.svg
│   └── sprites/
│       ├── player.svg
│       └── slime.svg
└── .github/
    ├── agents/my-agent.agent.md
    └── workflows/pages.yml
```

## Assets 개발 기준

초기 버전은 GitHub Pages에서 빠르게 로딩되도록 **SVG 기반 경량 에셋**을 사용합니다.
실제 픽셀아트가 준비되면 같은 경로의 파일을 PNG/WebP로 교체하거나 `data/maps.js`의 asset 경로만 변경하면 됩니다.

권장 규격:

| 분류 | 권장 크기 | 포맷 | 설명 |
| --- | --- | --- | --- |
| 타일 | 32x32 또는 64x64 | PNG/WebP/SVG | 잔디, 길, 물, 벽, 포탈 등 |
| 캐릭터 | 64x64 | PNG/WebP/SVG | 4방향/8방향 확장 가능 |
| 몬스터 | 64x64 | PNG/WebP/SVG | 지역별 몬스터 |
| UI 아이콘 | 32x32 | SVG/PNG | 스킬, 아이템, 장비 |

## Map 개발 기준

맵은 `data/maps.js`에서 문자 타일 방식으로 관리합니다.

```js
rows: [
  'TTTTTTTT',
  'TGRRRGTT',
  'TGPPMGTT',
  'TTTTTTTT'
]
```

문자 의미:

| 코드 | 의미 | 이동 | 기능 |
| --- | --- | --- | --- |
| `G` | grass | 가능 | 낮은 확률 조우 |
| `R` | road | 가능 | 거의 안전한 길 |
| `S` | stone | 가능 | 동굴/광산 바닥 |
| `T` | tree/wall | 불가 | 충돌 타일 |
| `W` | water | 불가 | 충돌 타일 |
| `M` | monster area | 가능 | 높은 확률 조우 |
| `N` | npc | 가능 | 안내 이벤트 |
| `P` | portal | 가능 | 다음 맵 이동 |

## 파일 설명

| 파일 | 설명 |
| --- | --- |
| `index.html` | 홈페이지/게임/맵 화면 구조 |
| `styles.css` | 전체 반응형 UI, 전투 화면, RPG 테마 스타일 |
| `map.css` | 모바일 타일맵, 이동 버튼, 맵 이벤트 스타일 |
| `game.js` | 전투, 성장, 저장, 스킬, 퀘스트 로직 |
| `data/maps.js` | 타일 타입과 기본 맵 데이터 |
| `map-engine.js` | 타일맵 렌더링, 이동, 충돌, 포탈, 조우 처리 |
| `game-map-bridge.js` | 맵 이벤트를 GM 콘솔 로그에 연결 |
| `assets/` | 타일/스프라이트 리소스 |
| `.github/agents/my-agent.agent.md` | MMORPG 개발 전문 에이전트 문서 |
| `.github/workflows/pages.yml` | GitHub Pages 정적 배포 워크플로 |

## 향후 확장 후보

1. Tiled Map Editor JSON import
2. 타일셋 PNG atlas 적용
3. 캐릭터 4방향/8방향 스프라이트 애니메이션
4. 몬스터 spawn table 분리
5. 서버 DB 기반 맵/오브젝트 관리
6. Java/Node API 서버 연동
7. WebSocket 위치 동기화
8. GM 맵/스폰 관리 콘솔
9. 로봇 AI 사냥 루틴
10. 실제 게임 런처 페이지

## 개발 메모

- 순수 HTML/CSS/JavaScript만 사용했습니다.
- 외부 CDN, npm 패키지, 빌드 도구가 없습니다.
- 저장 데이터는 브라우저 `localStorage`에 저장됩니다.
- UTF-8 기준으로 한글 UI를 구성했습니다.
- 모바일 브라우저에서 터치 버튼으로 이동 가능합니다.
