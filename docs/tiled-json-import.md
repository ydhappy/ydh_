# Tiled JSON Import

## 22-1 상태

현재 단계는 Tiled JSON을 기존 YDH 문자 타일맵 구조로 변환하는 기반만 추가한 상태입니다.

추가 파일:

- `data/tiled/moon-gate-sample.json`
- `data/tiled-map-loader.js`

아직 `index.html`과 `map-engine.js`에는 자동 연결하지 않았습니다. 다음 단계에서 안전하게 연결합니다.

## 변환 방식

Tiled JSON의 `tilelayer.data` GID 배열을 아래 YDH 타일 코드 문자열로 변환합니다.

| YDH Code | 의미 |
| --- | --- |
| `G` | 잔디 |
| `R` | 길 |
| `S` | 돌바닥 |
| `T` | 나무/벽 |
| `W` | 물 |
| `M` | 몬스터 구역 |
| `N` | NPC |
| `P` | 포탈 |

## Tiled Tileset property

각 tile에는 `ydhCode` property를 넣습니다.

예:

```json
{
  "id": 0,
  "properties": [
    { "name": "ydhCode", "type": "string", "value": "G" }
  ]
}
```

## Tiled Map property

맵 전체 property는 아래 이름을 사용합니다.

| property | 설명 |
| --- | --- |
| `ydhId` | YDH 맵 ID |
| `ydhName` | 표시 이름 |
| `ydhDescription` | 맵 설명 |
| `ydhStartX` | 시작 X 좌표 |
| `ydhStartY` | 시작 Y 좌표 |
| `ydhPortalTo` | 포탈 이동 대상 map index |

## 변환기 사용 예시

```js
const map = await window.YDH_TILED_MAP_LOADER.loadFromUrl('data/tiled/moon-gate-sample.json');
window.YDH_TILED_MAP_LOADER.appendToYdhMaps(map);
```

## 다음 단계

22-2에서 진행:

1. `index.html`에 `data/tiled-map-loader.js` 연결
2. Tiled 샘플맵을 자동 로드하는 bootstrap 추가
3. 로드 성공/실패 로그 추가
4. 기존 맵 엔진과 충돌 여부 확인
