# Tiled JSON Import

## 상태

현재는 22-1 ~ 22-7까지 완료된 상태입니다.

- 22-1: Tiled JSON 샘플맵 + 변환기 추가 완료
- 22-2: index 로드 연결 + 샘플맵 자동 등록 완료
- 22-3: 맵 선택/검증 UI 완료
- 22-4: 다중 Tiled JSON 등록/붙여넣기 import 완료
- 22-5: Object Layer 배치 데이터 추출 + GM 콘솔 표시 완료
- 22-6: Object Layer 기반 실제 배치 적용 완료
- 22-7: custom map 관리 UI/서버 저장 API 완료
- 22-8: 서버 custom map 복원/import 고도화 대기

## 추가 파일

- `data/tiled/moon-gate-sample.json`
- `data/tiled-map-loader.js`
- `data/tiled-map-registry.js`
- `tiled-map-bootstrap.js`
- `server/src/map-storage.js`

## 연결 파일

- `index.html`
- `gm-console.js`
- `map-engine.js`
- `server/src/server.js`

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

## 22-7 custom map 관리 UI/서버 저장 API

클라이언트 TILED MAP MANAGER 기능:

- custom map `내보내기`
- localStorage custom map `삭제`
- localStorage custom map `서버저장`
- `서버 맵 목록` 조회
- custom/server 상태 카운트 표시

브라우저 localStorage key:

```text
ydh-tiled-custom-maps-v1
```

서버 저장 파일:

```text
server/data/custom-maps.json
```

서버 API:

```text
GET    /api/maps/custom
POST   /api/maps/custom
GET    /api/maps/custom/:id
DELETE /api/maps/custom/:id
```

서버 health 응답에는 custom map 저장 상태가 포함됩니다.

```json
{
  "customMaps": {
    "count": 0,
    "max": 100
  }
}
```

## 안정성 기준

- 기존 문자 타일맵은 제거하지 않습니다.
- Object Layer는 Tiled 맵에만 적용됩니다.
- 적용 불가능한 Object는 게임을 중단하지 않고 `skippedPlacements`에 기록합니다.
- 서버 custom map은 현재 파일 저장 방식입니다.
- 서버 저장 맵을 클라이언트 맵 목록으로 자동 복원/import하는 기능은 22-8로 분리합니다.

## 22-8 다음 작업

다음 단계에서 진행합니다.

1. 서버 custom map 목록에서 클라이언트로 import
2. 서버 custom map 삭제 UI 연결
3. 검증 실패 맵 상세 보기
4. Object Layer marker 정보창
5. Tiled object layer 기반 퀘스트 트리거
