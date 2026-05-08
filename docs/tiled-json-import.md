# Tiled JSON Import

## 상태

현재는 22-1 ~ 22-5까지 완료된 상태입니다.

- 22-1: Tiled JSON 샘플맵 + 변환기 추가 완료
- 22-2: index 로드 연결 + 샘플맵 자동 등록 완료
- 22-3: 맵 선택/검증 UI 완료
- 22-4: 다중 Tiled JSON 등록/붙여넣기 import 완료
- 22-5: Object Layer 배치 데이터 추출 + GM 콘솔 표시 완료
- 22-6: Object Layer 기반 실제 배치 적용 대기

## 추가 파일

- `data/tiled/moon-gate-sample.json`
- `data/tiled-map-loader.js`
- `data/tiled-map-registry.js`
- `tiled-map-bootstrap.js`

## 연결 파일

- `index.html`
- `gm-console.js`

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

## Tiled Object Layer property

Object Layer의 object에는 아래 property를 사용할 수 있습니다.

| property | 설명 |
| --- | --- |
| `ydhKind` | `npc`, `monster`, `portal`, `marker` 등 배치 종류 |
| `ydhEntityId` | 연결할 NPC/몬스터 ID |
| `ydhName` | 표시 이름 override |
| `ydhTargetMapId` | 포탈 대상 맵 ID |
| `ydhTargetMapIndex` | 포탈 대상 맵 index |
| `ydhDialogue` | NPC 대사 |

Object 좌표는 Tiled pixel 좌표를 `tilewidth/tileheight`로 나눠 타일 좌표로 변환합니다.

## 자동 로드 흐름

`index.html`은 아래 순서로 로드합니다.

```html
<script src="data/maps.js"></script>
<script src="data/tiled-map-registry.js"></script>
<script src="data/tiled-map-loader.js"></script>
<script src="tiled-map-bootstrap.js"></script>
<script src="map-engine.js"></script>
```

## 22-4 다중 등록/붙여넣기 import

추가된 구조:

```js
window.YDH_TILED_MAP_REGISTRY = {
  urls: ['data/tiled/moon-gate-sample.json'],
  localStorageKey: 'ydh-tiled-custom-maps-v1',
  maxCustomMaps: 10
};
```

지원 기능:

1. 여러 Tiled JSON URL 목록을 registry에서 관리
2. registry URL을 순차 로드
3. Tiled JSON 붙여넣기 import 지원
4. 붙여넣기로 등록한 맵을 localStorage에 보존
5. 최대 custom map 수 제한
6. 중복 ID는 재등록 방지
7. 등록 실패/검증 실패를 TILED MAP MANAGER에 표시

## 22-5 Object Layer 추출/표시

추가된 변환 결과 필드:

```js
map.placements
map.tiled.objectLayers
map.tiled.placementSummary
map.source
map.sourceUrl
```

GM 콘솔 표시 항목:

- 맵 source/sourceUrl
- Object 총 개수
- Object kind별 개수
- Object 이름, 종류, 좌표, entityId
- Object 타일 강조 버튼
- 맵 정보 복사 시 object 목록 포함

## 안정성 기준

- Tiled JSON fetch가 늦거나 실패해도 인라인 fallback 맵이 먼저 등록됩니다.
- 알 수 없는 타일 코드가 있으면 해당 맵은 실패 목록에 기록됩니다.
- 기존 문자 타일맵은 제거하지 않습니다.
- 기존 맵 엔진의 이동/전투/스폰 처리는 그대로 유지합니다.
- 22-5는 Object Layer 데이터를 추출/표시만 하며 실제 NPC/몬스터/포탈 스폰 로직은 아직 변경하지 않습니다.

## 22-6 다음 작업

다음 단계에서 진행합니다.

1. Object Layer의 npc/monster/portal을 실제 맵 타일 코드에 반영
2. Object Layer 포탈 이동 대상 적용
3. Object Layer NPC 대사 적용
4. custom map 내보내기/삭제 UI
5. custom map 서버 저장 연동
