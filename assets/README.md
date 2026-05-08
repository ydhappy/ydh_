# Assets

이 폴더는 YDH Chronicle의 게임 리소스 저장소입니다.

## 현재 구성

```text
assets/
├── tiles/      # 맵 타일 SVG
├── sprites/    # 캐릭터/몬스터/포탈 SVG
└── README.md   # 에셋 제작/교체 가이드
```

## 운영 방식

초기 버전은 GitHub Pages에서 바로 동작하도록 **가벼운 SVG 리소스**를 사용합니다.
나중에 실제 픽셀아트 PNG/WebP가 준비되면 같은 파일명 또는 `data/assets.js`의 경로만 교체하면 됩니다.

## 권장 규격

| 분류 | 크기 | 포맷 | 설명 |
| --- | --- | --- | --- |
| 타일 | 32x32 또는 64x64 | PNG/WebP/SVG | 잔디, 길, 물, 벽, 포탈 등 |
| 캐릭터 | 64x64 | PNG/WebP/SVG | 4방향/8방향 확장 가능 |
| 몬스터 | 64x64 | PNG/WebP/SVG | 지역별 몬스터 |
| UI 아이콘 | 32x32 | SVG/PNG | 스킬, 아이템, 장비 |

## 맵 개발 규칙

맵은 `data/maps.js`에서 문자 타일 방식으로 관리합니다.

예시:

```js
rows: [
  'TTTTTTTT',
  'TGRRRGTT',
  'TGPPMGTT',
  'TTTTTTTT'
]
```

문자 의미:

- `G`: grass, 이동 가능
- `R`: road, 이동 가능
- `S`: stone, 이동 가능
- `T`: tree/wall, 이동 불가
- `W`: water, 이동 불가
- `M`: monster spawn, 이동 가능 + 조우 이벤트
- `N`: npc, 이동 가능 + 안내 이벤트
- `P`: portal, 이동 가능 + 다음 지역 이동

## 향후 확장

1. Tiled Map Editor JSON 불러오기
2. 타일셋 PNG atlas 적용
3. 몬스터 spawn table 분리
4. 충돌 레이어/collision layer 분리
5. 서버 DB 기반 맵/오브젝트 관리
6. Java 서버와 WebSocket 위치 동기화
