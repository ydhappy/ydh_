# YDH Chronicle Save API Server

정적 웹 게임 `YDH Chronicle`의 localStorage 저장 데이터를 서버로 받을 수 있는 경량 Node/Express 서버입니다.

## 실행 방법

```bash
cd server
npm install
npm start
```

개발 모드:

```bash
npm run dev
```

기본 주소:

```text
http://localhost:3000
```

서버는 저장 API, custom Tiled map API, WebSocket 위치 동기화, 정적 파일 제공을 함께 처리합니다.

```text
http://localhost:3000/index.html
```

## 환경 변수

| 변수 | 기본값 | 설명 |
| --- | --- | --- |
| `PORT` | `3000` | 서버 포트 |
| `YDH_DATA_DIR` | `server/data` | 파일 저장 위치 |
| `YDH_MAX_CUSTOM_MAPS` | `100` | 서버 custom map 최대 저장 개수 |
| `YDH_PUBLIC_DIR` | repository root | 정적 파일 제공 위치 |
| `YDH_STORAGE` | `file` | `file` 또는 `mysql` |
| `MYSQL_HOST` | `127.0.0.1` | MySQL 호스트 |
| `MYSQL_PORT` | `3306` | MySQL 포트 |
| `MYSQL_USER` | `root` | MySQL 계정 |
| `MYSQL_PASSWORD` | empty | MySQL 비밀번호 |
| `MYSQL_DATABASE` | `ydh_chronicle` | MySQL DB명 |
| `MYSQL_CONNECTION_LIMIT` | `5` | MySQL connection pool 크기 |

## MySQL 5.5 저장소 사용

MySQL 5.5는 `JSON` 타입이 없으므로 저장 스냅샷은 `LONGTEXT` 컬럼에 JSON 문자열로 저장합니다.

### 1. DB/테이블 생성

```bash
mysql -u root -p < server/sql/mysql55-schema.sql
```

또는 MySQL 접속 후 직접 실행:

```sql
SOURCE server/sql/mysql55-schema.sql;
```

### 2. 서버 실행

Linux/macOS:

```bash
cd server
YDH_STORAGE=mysql \
MYSQL_HOST=127.0.0.1 \
MYSQL_PORT=3306 \
MYSQL_USER=root \
MYSQL_PASSWORD=your_password \
MYSQL_DATABASE=ydh_chronicle \
npm start
```

Windows PowerShell:

```powershell
cd server
$env:YDH_STORAGE="mysql"
$env:MYSQL_HOST="127.0.0.1"
$env:MYSQL_PORT="3306"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="your_password"
$env:MYSQL_DATABASE="ydh_chronicle"
npm start
```

## API

### Health check

```bash
curl http://localhost:3000/api/health
```

응답에는 저장소 상태, custom map 저장 상태, 실시간 접속자 통계가 포함됩니다.

### Realtime stats

```bash
curl http://localhost:3000/api/realtime/stats
```

### Custom Tiled map list

```bash
curl http://localhost:3000/api/maps/custom
```

### Custom Tiled map save

```bash
curl -X POST http://localhost:3000/api/maps/custom \
  -H "Content-Type: application/json" \
  -d '{"map":{"id":"test-map","name":"테스트 맵","rows":["GGG","GPG","GGG"],"start":{"x":1,"y":1}}}'
```

### Custom Tiled map read

```bash
curl http://localhost:3000/api/maps/custom/test-map
```

### Custom Tiled map delete

```bash
curl -X DELETE http://localhost:3000/api/maps/custom/test-map
```

### Account list

```bash
curl http://localhost:3000/api/accounts
```

### Character list

```bash
curl http://localhost:3000/api/characters
curl "http://localhost:3000/api/characters?accountId=acc_xxx"
```

### Save snapshot

```bash
curl -X POST http://localhost:3000/api/save/snapshot \
  -H "Content-Type: application/json" \
  -d @sample-snapshot.json
```

### Save list

```bash
curl http://localhost:3000/api/save/list
```

### Restore latest save

```bash
curl http://localhost:3000/api/save/restore
```

### Restore selected save

```bash
curl http://localhost:3000/api/save/save_1234567890_abcd
```

## WebSocket 위치 동기화

WebSocket endpoint:

```text
ws://localhost:3000/ws/position
```

브라우저에서는 `실시간` 섹션에서 `연결` 버튼을 누르면 연결됩니다. 이동할 때마다 클라이언트가 현재 위치를 서버로 보내고, 서버는 다른 접속자에게 위치를 브로드캐스트합니다.

지원 메시지:

```json
{ "type": "hello", "payload": { "accountName": "YDH Player", "characterName": "검은 기사", "mapIndex": 0, "x": 1, "y": 1, "direction": 12 } }
```

```json
{ "type": "position", "payload": { "mapIndex": 0, "x": 2, "y": 1, "direction": 0 } }
```

서버 응답 메시지:

```text
connected
welcome
peer-joined
peer-position
peer-left
error
```

## 브라우저 UI 사용 흐름

1. 서버 실행
2. `http://localhost:3000/index.html` 접속
3. `계정` 섹션에서 로컬 계정명 저장
4. 캐릭터 슬롯 생성 또는 선택
5. `TILED MAP MANAGER`에서 custom Tiled JSON 붙여넣기 또는 기존 맵 선택
6. custom map 카드에서 `내보내기`, `삭제`, `서버저장` 사용
7. `SERVER CUSTOM MAP SYNC`에서 `지금 동기화` 또는 `자동 동기화` 사용
8. `실시간` 섹션에서 `연결` 클릭
9. 다른 브라우저/기기에서 같은 서버 접속 후 `연결` 클릭
10. 맵 이동 시 서로의 위치 카드가 갱신되고, 같은 맵이면 타일맵 위에 원격 아바타가 표시되는지 확인
11. 저장은 `서버연동` 섹션에서 `서버 저장` 클릭

## Custom map 저장 방식

서버 custom map은 파일 저장입니다.

```text
server/data/custom-maps.json
```

브라우저 custom map은 localStorage에 저장됩니다.

```text
ydh-tiled-custom-maps-v1
```

서버 custom map 자동 동기화 설정은 localStorage에 저장됩니다.

```text
ydh-server-custom-map-auto-sync-v1
```

자동 동기화 패널:

```text
SERVER CUSTOM MAP SYNC
```

지원 기능:

- `지금 동기화`: 서버 custom map 목록을 조회하고 전체 map을 클라이언트로 import
- `자동 동기화`: 페이지 로드 시 서버 custom map을 자동으로 import
- 가져온 map은 `YDH_MAPS.maps`와 localStorage custom map에 함께 저장
- 이미 같은 ID의 map이 있으면 맵 목록 중복 추가 없이 localStorage만 갱신

## 실시간 타일맵 원격 아바타 표시

클라이언트는 `ydh-realtime-peers-updated`와 `ydh-map-rendered` 이벤트를 사용해 같은 `mapIndex`에 있는 다른 접속자를 현재 타일맵 위에 표시합니다.

관련 파일:

```text
realtime-sync.js
realtime-map-peers.js
realtime-map-peers.css
map-engine.js
```

## Snapshot 계정/캐릭터 필드

`/api/save/snapshot`은 기존 저장 데이터 외에 아래 필드를 함께 받을 수 있습니다.

```json
{
  "account": {
    "accountId": "acc_xxx",
    "provider": "local",
    "displayName": "YDH Player"
  },
  "selectedCharacter": {
    "characterId": "char_xxx",
    "accountId": "acc_xxx",
    "slot": 1,
    "name": "검은 기사",
    "classId": "knight"
  },
  "characterSlots": []
}
```

## 저장 방식

기본은 파일 저장입니다.

```text
server/data/saves.json
```

MySQL 모드는 아래 테이블을 사용합니다.

```text
ydh_accounts
ydh_character_slots
ydh_save_snapshots
ydh_schema_meta
```

스냅샷은 `ydh_save_snapshots.snapshot_json`에 원본 그대로 보관하고, 계정/캐릭터 검색용 요약 데이터는 `ydh_accounts`, `ydh_character_slots`에 정규화해서 저장합니다.

## 다음 고도화 후보

1. 서버 custom map을 계정/캐릭터별로 분리 저장
2. custom map MySQL 저장소 연동
3. 실제 PNG/WebP atlas 교체
4. 서버 계정 인증 추가
5. 운영용 관리자 저장 삭제/정리 API
6. 원격 아바타 클릭 정보창
