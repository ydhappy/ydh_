# Tiled JSON Import

## 상태

현재는 22-1 ~ 22-8B-1까지 완료된 상태입니다.

- 22-1: Tiled JSON 샘플맵 + 변환기 추가 완료
- 22-2: index 로드 연결 + 샘플맵 자동 등록 완료
- 22-3: 맵 선택/검증 UI 완료
- 22-4: 다중 Tiled JSON 등록/붙여넣기 import 완료
- 22-5: Object Layer 배치 데이터 추출 + GM 콘솔 표시 완료
- 22-6: Object Layer 기반 실제 배치 적용 완료
- 22-7: custom map 관리 UI/서버 저장 API 완료
- 22-8A: 서버 custom map 가져오기/삭제 UI 완료
- 22-8B-1: Object Layer marker 정보창 완료
- 22-8B-2: Tiled object layer 기반 퀘스트 트리거 대기

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
- `map.css`
- `server/src/server.js`

## Tiled Object Layer property

Object Layer의 object에는 아래 property를 사용할 수 있습니다.

| property | 설명 |
| --- | --- |
| `ydhKind` | `npc`, `monster`, `portal`, `marker` 등 배치 종류 |
| `ydhEntityId` | 연결할 NPC/몬스터 ID |
| `ydhName` | 표시 이름 override |
| `ydhTargetMapId` | 포탈 대상 맵 ID |
| `ydhTargetMapIndex` | 포탈 대상 맵 index |
| `ydhDialogue` | NPC/marker 설명 또는 대사 |

## 자동 로드 흐름

`index.html`은 아래 순서로 로드합니다.

```html
<script src="data/maps.js"></script>
<script src="data/tiled-map-registry.js"></script>
<script src="data/tiled-map-loader.js"></script>
<script src="tiled-map-bootstrap.js"></script>
<script src="map-engine.js"></script>
```

## 22-7 custom map 관리 UI/서버 저장 API

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

## 22-8A 서버 custom map 가져오기/삭제 UI

서버 맵 카드 기능:

- `클라이언트로 가져오기`: `GET /api/maps/custom/:id`로 전체 map을 받아 `YDH_MAPS.maps`에 등록하고 localStorage custom map으로 저장
- `서버삭제`: `DELETE /api/maps/custom/:id` 호출 후 서버 목록에서 제거
- 서버 맵 카드는 클라이언트 맵 카드와 분리된 `서버 맵` 영역에 표시

## 22-8B-1 Object Layer marker 정보창

Tiled Object Layer에서 `ydhKind=marker`인 object는 rows에는 합성하지 않고, 해당 타일 위에 정보 아이콘으로 표시합니다.

동작:

1. `map-engine.js`가 현재 타일의 marker placement를 탐색
2. 타일 우측 상단에 `ⓘ` marker 배지 표시
3. marker 클릭 시 `map-marker-info` 패널 표시
4. 패널에는 marker 이름, 좌표, layer, kind, id, `ydhDialogue` 설명 표시
5. marker 클릭 이벤트는 이동 이벤트와 분리되어 인접 이동을 발생시키지 않음

샘플맵 marker:

```text
달문 석비 · marker · X:4 Y:4
```

관련 파일:

- `map-engine.js`
- `map.css`
- `data/tiled/moon-gate-sample.json`

## 안정성 기준

- 기존 문자 타일맵은 제거하지 않습니다.
- Object Layer는 Tiled 맵에만 적용됩니다.
- marker는 이동/전투/포탈 타일 코드로 변환하지 않습니다.
- marker 정보창은 화면 우하단 고정 패널이며 모바일에서는 전체 폭에 맞춰 표시됩니다.

## 22-8B-2 다음 작업

다음 단계에서 진행합니다.

1. Tiled object layer 기반 퀘스트 트리거
2. marker 기반 퀘스트 시작/완료 조건
3. 서버 custom map 자동 동기화 옵션
4. 서버 custom map을 계정/캐릭터별로 분리 저장
5. custom map MySQL 저장소 연동
