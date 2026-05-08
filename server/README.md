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

서버는 저장 API와 함께 저장소 루트의 정적 파일도 제공합니다.

```text
http://localhost:3000/index.html
```

## 환경 변수

| 변수 | 기본값 | 설명 |
| --- | --- | --- |
| `PORT` | `3000` | 서버 포트 |
| `YDH_DATA_DIR` | `server/data` | 파일 저장 위치 |
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

### 3. 확인

```bash
curl http://localhost:3000/api/health
```

응답에 아래처럼 표시되면 MySQL 저장소가 활성화된 것입니다.

```json
{
  "ok": true,
  "storageMode": "mysql"
}
```

## API

### Health check

```bash
curl http://localhost:3000/api/health
```

### Save snapshot

브라우저의 `서버연동` 섹션에서 `서버 저장` 버튼을 누르면 이 API로 POST됩니다.

```bash
curl -X POST http://localhost:3000/api/save/snapshot \
  -H "Content-Type: application/json" \
  -d @sample-snapshot.json
```

### Save list

브라우저의 `서버 저장목록` 버튼과 연결됩니다.

```bash
curl http://localhost:3000/api/save/list
```

### Restore latest save

브라우저의 `최신 저장 복원`, `최신 복원+새로고침` 버튼과 연결됩니다.

```bash
curl http://localhost:3000/api/save/restore
```

### Restore selected save

저장 목록에서 특정 저장 ID를 선택 복원할 때 사용합니다.

```bash
curl http://localhost:3000/api/save/save_1234567890_abcd
```

브라우저 `서버연동` 섹션에서는 `서버 저장목록`을 누른 뒤 각 저장 카드의 `복원`, `복원+새로고침` 버튼으로 호출됩니다.

## 브라우저 UI 사용 흐름

1. 서버 실행
2. `http://localhost:3000/index.html` 접속
3. `계정` 섹션에서 로컬 계정명 저장
4. 캐릭터 슬롯 생성 또는 선택
5. 게임 진행
6. `서버연동` 섹션에서 `서버 저장` 클릭
7. `서버 저장목록`으로 저장 카드 확인
8. 원하는 저장 카드에서 `복원` 또는 `복원+새로고침` 클릭

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

저장 목록 응답에는 `accountName`, `characterName`, `classId`, `slotCount` 요약값이 포함됩니다.

## 저장 방식

기본은 파일 저장입니다.

```text
server/data/saves.json
```

MySQL 모드는 아래 테이블을 사용합니다.

```text
ydh_save_snapshots
ydh_schema_meta
```

## 다음 고도화 후보

1. 서버 계정 인증 추가
2. MySQL 계정/캐릭터 정규화 테이블 분리
3. WebSocket 위치 동기화
4. Tiled Map Editor JSON import
5. 실제 PNG/WebP atlas 교체
