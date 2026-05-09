# Tiled JSON Import

## 상태

현재는 22-1 ~ 22-8B-2까지 완료된 상태입니다.

- 22-1: Tiled JSON 샘플맵 + 변환기 추가 완료
- 22-2: index 로드 연결 + 샘플맵 자동 등록 완료
- 22-3: 맵 선택/검증 UI 완료
- 22-4: 다중 Tiled JSON 등록/붙여넣기 import 완료
- 22-5: Object Layer 배치 데이터 추출 + GM 콘솔 표시 완료
- 22-6: Object Layer 기반 실제 배치 적용 완료
- 22-7: custom map 관리 UI/서버 저장 API 완료
- 22-8A: 서버 custom map 가져오기/삭제 UI 완료
- 22-8B-1: Object Layer marker 정보창 완료
- 22-8B-2: Tiled object layer 기반 퀘스트 트리거 완료

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
- `chapter-quests.js`
- `data/chapter-quests.js`
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
| `ydhQuestType` | 퀘스트 진행 type. 예: `inspectMarker` |
| `ydhQuestTarget` | 퀘스트 objective target |
| `ydhQuestAmount` | 증가량. 기본값 `1` |
| `ydhQuestTrigger` | `ydhQuestType` 대체용 trigger 이름 |

## 자동 로드 흐름

`index.html`은 아래 순서로 로드합니다.

```html
<script src="data/maps.js"></script>
<script src="data/tiled-map-registry.js"></script>
<script src="data/tiled-map-loader.js"></script>
<script src="tiled-map-bootstrap.js"></script>
<script src="map-engine.js"></script>
```

## 22-8B-1 Object Layer marker 정보창

Tiled Object Layer에서 `ydhKind=marker`인 object는 rows에는 합성하지 않고, 해당 타일 위에 정보 아이콘으로 표시합니다.

동작:

1. `map-engine.js`가 현재 타일의 marker placement를 탐색
2. 타일 우측 상단에 `ⓘ` marker 배지 표시
3. marker 클릭 시 `map-marker-info` 패널 표시
4. 패널에는 marker 이름, 좌표, layer, kind, id, `ydhDialogue` 설명 표시
5. marker 클릭 이벤트는 이동 이벤트와 분리되어 인접 이동을 발생시키지 않음

## 22-8B-2 Tiled object layer 기반 퀘스트 트리거

marker 클릭 시 `map-engine.js`가 아래 이벤트를 발행합니다.

```js
window.dispatchEvent(new CustomEvent('ydh-tiled-quest-trigger', {
  detail: {
    type,
    target,
    amount,
    placement,
    map,
    state
  }
}));
```

`chapter-quests.js`는 `ydh-tiled-quest-trigger`를 받아 기존 `progress(type, target, amount)` 흐름으로 퀘스트 진행도를 갱신합니다.

샘플 objective:

```js
{ id: 'inspect-moon-stone', type: 'inspectMarker', target: 'moon-gate-stone', label: '달문 석비 확인', required: 1 }
```

샘플 marker property:

```json
{ "name": "ydhQuestType", "type": "string", "value": "inspectMarker" }
{ "name": "ydhQuestTarget", "type": "string", "value": "moon-gate-stone" }
{ "name": "ydhQuestAmount", "type": "int", "value": 1 }
```

추가 퀘스트:

```text
quest-ch5-moon-gate-marker · 달문 석비의 경고
```

테스트 흐름:

1. TILED MAP MANAGER에서 `달문 광장`으로 이동
2. `달문 석비` marker 클릭
3. 정보창 표시 확인
4. 챕터 퀘스트 `달문 석비 확인` 진행도 1/1 확인

## 안정성 기준

- 기존 문자 타일맵은 제거하지 않습니다.
- Object Layer는 Tiled 맵에만 적용됩니다.
- marker는 이동/전투/포탈 타일 코드로 변환하지 않습니다.
- marker quest trigger는 기존 퀘스트 progress 구조를 재사용합니다.
- property가 없는 marker는 기본 `inspectMarker` + marker id/name으로 이벤트를 발행합니다.

## 다음 작업

1. 서버 custom map 자동 동기화 옵션
2. 서버 custom map을 계정/캐릭터별로 분리 저장
3. custom map MySQL 저장소 연동
4. 실제 PNG/WebP atlas 교체
5. 원격 아바타 클릭 정보창
