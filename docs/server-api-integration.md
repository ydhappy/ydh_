# YDH Chronicle 서버/API 연동 준비 문서

이 문서는 현재 정적 웹 게임의 `localStorage` 저장 구조를 서버/API 저장 구조로 옮기기 위한 기준입니다.

## 현재 상태

현재 게임은 서버 없이 브라우저 `localStorage`에 저장합니다.

사용 중인 주요 저장 키:

| 저장 영역 | localStorage key |
| --- | --- |
| 캐릭터/전투 | `ydh-chronicle-save-v1` |
| 맵 위치 | `ydh-chronicle-map-v1` |
| 챕터 퀘스트 | `ydh-chapter-quests-v1` |
| 도감 해금 | `ydh-codex-unlocks-v1` |
| GM 콘솔 상태 | `ydh-gm-console-open` |

## 추가된 파일

| 파일 | 역할 |
| --- | --- |
| `data/save-schema.js` | 저장 키, API endpoint, 서버 모델 정의 |
| `server-sync.js` | localStorage 데이터를 하나의 snapshot으로 묶는 API 어댑터 |
| `server-sync.css` | 서버 연동 패널 스타일 |
| `server-sync-panel.js` | 스냅샷 보기/내보내기/전송 테스트 UI |

## Snapshot 구조

`window.YDH_SERVER_SYNC.buildSnapshot()`은 아래 형태의 데이터를 만듭니다.

```json
{
  "schemaVersion": 1,
  "app": "YDH Chronicle",
  "generatedAt": "ISO-8601",
  "client": {
    "userAgent": "string",
    "language": "string",
    "timezone": "string"
  },
  "saves": {
    "character": {},
    "map": {},
    "chapterQuests": {},
    "codexUnlocks": {},
    "gmConsoleOpen": "0|1|null"
  }
}
```

## 권장 API endpoint

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `POST` | `/api/save/snapshot` | 전체 저장 스냅샷 저장 |
| `POST` | `/api/save/character` | 캐릭터/전투 상태 저장 |
| `POST` | `/api/save/map` | 현재 맵/좌표 저장 |
| `POST` | `/api/save/quests` | 챕터 퀘스트 저장 |
| `POST` | `/api/save/codex` | 도감 해금 상태 저장 |
| `GET` | `/api/save/restore` | 서버 저장 데이터 복원 |

## Java/Spring Boot 예시 모델

```java
public record SaveSnapshotRequest(
    int schemaVersion,
    String app,
    String generatedAt,
    ClientInfo client,
    SavePayload saves
) {}

public record ClientInfo(
    String userAgent,
    String language,
    String timezone
) {}

public record SavePayload(
    Map<String, Object> character,
    Map<String, Object> map,
    Map<String, Object> chapterQuests,
    Map<String, Object> codexUnlocks,
    String gmConsoleOpen
) {}
```

## Node/Express 예시

```js
app.post('/api/save/snapshot', express.json(), async (req, res) => {
  const snapshot = req.body;
  // TODO: validate schemaVersion, accountId, characterId
  // TODO: save to DB
  res.json({ ok: true, savedAt: new Date().toISOString() });
});
```

## DB 테이블 후보

### accounts

| column | type |
| --- | --- |
| id | varchar / uuid |
| provider | varchar |
| display_name | varchar |
| created_at | timestamp |
| last_login_at | timestamp |

### characters

| column | type |
| --- | --- |
| id | varchar / uuid |
| account_id | varchar / uuid |
| name | varchar |
| level | int |
| exp | bigint |
| hp | int |
| mp | int |
| atk | int |
| def | int |
| gold | bigint |
| wave | int |
| inventory_json | json/text |
| cooldowns_json | json/text |
| updated_at | timestamp |

### character_map_state

| column | type |
| --- | --- |
| character_id | varchar / uuid |
| map_index | int |
| x | int |
| y | int |
| direction | int |
| steps | bigint |
| last_target_json | json/text |
| updated_at | timestamp |

### character_quests

| column | type |
| --- | --- |
| character_id | varchar / uuid |
| quest_id | varchar |
| objectives_json | json/text |
| reward_claimed | boolean |
| updated_at | timestamp |

### character_codex

| column | type |
| --- | --- |
| character_id | varchar / uuid |
| unlocked_json | json/text |
| last_unlock_json | json/text |
| updated_at | timestamp |

## 현재 UI 사용법

페이지의 `서버연동` 섹션에서 다음을 사용할 수 있습니다.

- `스냅샷 보기`: 현재 저장 데이터를 JSON으로 확인
- `JSON 내보내기`: 파일로 다운로드
- `클립보드 복사`: JSON 복사
- `서버 전송 테스트`: `/api/save/snapshot`으로 POST 테스트

실제 서버가 없으면 `서버 전송 테스트`는 실패하는 것이 정상입니다. 실패해도 로컬 저장과 게임 실행에는 영향이 없습니다.
