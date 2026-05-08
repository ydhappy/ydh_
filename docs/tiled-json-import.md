# Tiled JSON Import

## 상태

현재는 22-1, 22-2까지 완료된 상태입니다.

- 22-1: Tiled JSON 샘플맵 + 변환기 추가 완료
- 22-2: index 로드 연결 + 샘플맵 자동 등록 완료
- 22-3: 맵 선택/검증 UI 및 다중 Tiled 맵 확장 대기

## 추가 파일

- `data/tiled/moon-gate-sample.json`
- `data/tiled-map-loader.js`
- `tiled-map-bootstrap.js`

## 연결 파일

- `index.html`

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

## 자동 로드 흐름

`index.html`은 아래 순서로 로드합니다.

```html
<script src="data/maps.js"></script>
<script src="data/tiled-map-loader.js"></script>
<script src="tiled-map-bootstrap.js"></script>
<script src="map-engine.js"></script>
```

`map-engine.js`가 시작되기 전에 `tiled-map-bootstrap.js`가 샘플맵을 `YDH_MAPS.maps`에 추가합니다.

로드 실패 시에는 콘솔 경고와 게임 로그 이벤트만 남기고 기존 문자 타일맵은 그대로 유지됩니다.

## 22-3 다음 작업

다음 단계에서 진행합니다.

1. 맵 선택 UI 추가
2. GM 콘솔에 Tiled source 표시
3. 다중 Tiled JSON 등록 구조 추가
4. Tiled 맵 검증 결과 표시
5. 실패 맵/중복 맵 목록 표시
