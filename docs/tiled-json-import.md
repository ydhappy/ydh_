# Tiled JSON Import

## 상태

현재는 22-1 ~ 22-8A까지 완료된 상태입니다.

- 22-1: Tiled JSON 샘플맵 + 변환기 추가 완료
- 22-2: index 로드 연결 + 샘플맵 자동 등록 완료
- 22-3: 맵 선택/검증 UI 완료
- 22-4: 다중 Tiled JSON 등록/붙여넣기 import 완료
- 22-5: Object Layer 배치 데이터 추출 + GM 콘솔 표시 완료
- 22-6: Object Layer 기반 실제 배치 적용 완료
- 22-7: custom map 관리 UI/서버 저장 API 완료
- 22-8A: 서버 custom map 가져오기/삭제 UI 완료
- 22-8B: marker 정보창/퀘스트 트리거 대기

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

## 22-8A 서버 custom map 가져오기/삭제 UI

TILED MAP MANAGER의 `서버 맵 목록` 버튼으로 서버 저장 custom map 요약 목록을 조회합니다.

서버 맵 카드 기능:

- `클라이언트로 가져오기`: `GET /api/maps/custom/:id`로 전체 map을 받아 `YDH_MAPS.maps`에 등록하고 localStorage custom map으로 저장
- `서버삭제`: `DELETE /api/maps/custom/:id` 호출 후 서버 목록에서 제거
- 서버 맵 카드는 클라이언트 맵 카드와 분리된 `서버 맵` 영역에 표시

검증/오류 UI:

- 검증 실패는 `실패 N건`으로 요약 표시
- `실패 상세 보기`를 펼치면 실패 URL/API와 오류 메시지를 확인 가능

## 안정성 기준

- 기존 문자 타일맵은 제거하지 않습니다.
- Object Layer는 Tiled 맵에만 적용됩니다.
- 적용 불가능한 Object는 게임을 중단하지 않고 `skippedPlacements`에 기록합니다.
- 서버 custom map은 현재 파일 저장 방식입니다.
- 22-8A는 서버 저장 맵을 수동으로 가져오는 기능까지이며, 자동 동기화는 아직 적용하지 않습니다.

## 22-8B 다음 작업

다음 단계에서 진행합니다.

1. Object Layer marker 정보창
2. Tiled object layer 기반 퀘스트 트리거
3. 서버 custom map 자동 동기화 옵션
4. 서버 custom map을 계정/캐릭터별로 분리 저장
5. custom map MySQL 저장소 연동
