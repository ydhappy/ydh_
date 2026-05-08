# Tiled JSON Import

## 상태

현재는 22-1, 22-2, 22-3까지 완료된 상태입니다.

- 22-1: Tiled JSON 샘플맵 + 변환기 추가 완료
- 22-2: index 로드 연결 + 샘플맵 자동 등록 완료
- 22-3: 맵 선택/검증 UI 완료
- 22-4: 다중 Tiled JSON 등록/관리 고도화 대기

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

## 22-3 맵 선택/검증 UI

`tiled-map-bootstrap.js`는 이제 아래 기능을 함께 제공합니다.

1. 인라인 fallback Tiled 변환맵 동기 등록
2. fetch 기반 Tiled JSON 로드 검증
3. 중복 맵 ID 감지
4. 타일 코드 검증
5. 시작 좌표 검증
6. 맵 선택 카드 UI 표시
7. 선택한 맵의 시작 좌표로 이동 후 새로고침

자동 생성 UI:

```text
TILED MAP MANAGER
```

표시 위치:

```text
Assets/Map 섹션 바로 아래
```

맵 카드에서 `이 맵으로 이동`을 누르면 `ydh-chronicle-map-v1` 저장값을 해당 맵 index와 시작 좌표로 변경한 뒤 페이지를 새로고침합니다.

## 안정성 기준

- Tiled JSON fetch가 늦거나 실패해도 인라인 fallback 맵이 먼저 등록됩니다.
- 알 수 없는 타일 코드가 있으면 해당 맵은 실패 목록에 기록됩니다.
- 기존 문자 타일맵은 제거하지 않습니다.
- 기존 맵 엔진의 이동/전투/스폰 처리는 그대로 유지합니다.

## 22-4 다음 작업

다음 단계에서 진행합니다.

1. 여러 개의 Tiled JSON URL 목록 관리
2. 외부 Tiled JSON 업로드/붙여넣기 import
3. GM 콘솔에 source/sourceUrl 표시
4. 검증 실패 맵 상세 보기
5. Tiled object layer 기반 NPC/몬스터/포탈 배치
