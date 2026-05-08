# Tiled JSON Import

## 상태

현재는 22-1 ~ 22-6까지 완료된 상태입니다.

- 22-1: Tiled JSON 샘플맵 + 변환기 추가 완료
- 22-2: index 로드 연결 + 샘플맵 자동 등록 완료
- 22-3: 맵 선택/검증 UI 완료
- 22-4: 다중 Tiled JSON 등록/붙여넣기 import 완료
- 22-5: Object Layer 배치 데이터 추출 + GM 콘솔 표시 완료
- 22-6: Object Layer 기반 실제 배치 적용 완료
- 22-7: custom map 관리 UI/서버 저장 연동 대기

## 추가 파일

- `data/tiled/moon-gate-sample.json`
- `data/tiled-map-loader.js`
- `data/tiled-map-registry.js`
- `tiled-map-bootstrap.js`

## 연결 파일

- `index.html`
- `gm-console.js`
- `map-engine.js`

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

## 22-6 Object Layer 실제 배치 적용

추가된 변환 결과 필드:

```js
map.baseRows
map.rows
map.appliedPlacements
map.skippedPlacements
map.tiled.appliedPlacements
map.tiled.skippedPlacements
```

적용 방식:

1. Tiled tilelayer를 `baseRows`로 변환
2. Object Layer placement를 분석
3. `npc`는 `N`, `monster`는 `M`, `portal`은 `P`로 rows에 합성
4. 범위 밖 object 또는 지원하지 않는 kind는 `skippedPlacements`로 분리
5. 최종 `rows`를 기존 `map-engine.js`가 그대로 렌더링

맵 엔진 반영 내용:

- Object Layer npc의 `ydhEntityId` 기반 NPC 스프라이트 선택
- Object Layer npc의 `ydhDialogue` 기반 대사 출력
- Object Layer monster의 `ydhEntityId` 기반 몬스터 스프라이트 선택
- Object Layer portal의 `ydhTargetMapIndex` / `ydhTargetMapId` 기반 포탈 이동
- GM 콘솔에 적용/제외 placement 수 표시

## 안정성 기준

- 기존 문자 타일맵은 제거하지 않습니다.
- Object Layer는 Tiled 맵에만 적용됩니다.
- 적용 불가능한 Object는 게임을 중단하지 않고 `skippedPlacements`에 기록합니다.
- 기존 맵 엔진의 기본 M/N/P 처리 흐름은 유지합니다.

## 22-7 다음 작업

다음 단계에서 진행합니다.

1. custom map 내보내기/삭제 UI
2. custom map 서버 저장 연동
3. 검증 실패 맵 상세 보기
4. Object Layer marker 정보창
5. Tiled object layer 기반 퀘스트 트리거
