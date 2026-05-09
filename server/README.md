# YDH Chronicle Save API Server

정적 웹 게임 `YDH Chronicle`의 localStorage 저장 데이터를 서버로 받을 수 있는 경량 Node/Express 서버입니다.

## 실행 방법

```bash
cd server
npm install
npm start
```

기본 주소:

```text
http://localhost:3000
```

서버는 저장 API, custom Tiled map API, WebSocket 위치 동기화, 정적 파일 제공을 함께 처리합니다.

## 환경 변수

| 변수 | 기본값 | 설명 |
| --- | --- | --- |
| `PORT` | `3000` | 서버 포트 |
| `YDH_DATA_DIR` | `server/data` | 파일 저장 위치 |
| `YDH_MAX_CUSTOM_MAPS` | `100` | 서버 custom map 최대 저장 개수 |
| `YDH_PUBLIC_DIR` | repository root | 정적 파일 제공 위치 |
| `YDH_STORAGE` | `file` | `file` 또는 `mysql` |
| `YDH_AUTH_REQUIRED` | `false` | `true`이면 주요 API에 Bearer token 필요 |
| `YDH_AUTH_SHARED_SECRET` | empty | 로그인 시 요구할 공유 secret |
| `YDH_AUTH_SECRET` | `YDH_AUTH_SHARED_SECRET` 또는 개발 기본값 | HMAC token 서명 secret |
| `YDH_AUTH_TOKEN_TTL_SECONDS` | `604800` | token 만료 시간 |
| `MYSQL_HOST` | `127.0.0.1` | MySQL 호스트 |
| `MYSQL_PORT` | `3306` | MySQL 포트 |
| `MYSQL_USER` | `root` | MySQL 계정 |
| `MYSQL_PASSWORD` | empty | MySQL 비밀번호 |
| `MYSQL_DATABASE` | `ydh_chronicle` | MySQL DB명 |

## 선택 인증 모드

기본값은 인증 선택 모드입니다.

```bash
cd server
npm start
```

보호 모드 실행:

```bash
cd server
YDH_AUTH_REQUIRED=true \
YDH_AUTH_SHARED_SECRET=change-me \
YDH_AUTH_SECRET=server-signing-secret \
npm start
```

Windows PowerShell:

```powershell
cd server
$env:YDH_AUTH_REQUIRED="true"
$env:YDH_AUTH_SHARED_SECRET="change-me"
$env:YDH_AUTH_SECRET="server-signing-secret"
npm start
```

보호 대상:

```text
/api/maps/custom
/api/accounts
/api/characters
/api/save/*
```

비보호 대상:

```text
/api/health
/api/auth/status
/api/auth/login
/api/realtime/stats
정적 파일
WebSocket 위치 동기화
```

## Auth API

### 상태 확인

```bash
curl http://localhost:3000/api/auth/status
```

### 로그인/token 발급

```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"accountId":"acc_demo","displayName":"YDH Player","secret":"change-me"}'
```

응답의 `token`을 Bearer token으로 사용합니다.

```bash
curl http://localhost:3000/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

브라우저에서는 `SERVER AUTH` 패널에서 로그인하면 token이 localStorage에 저장되고 `/api/*` 요청에 자동으로 `Authorization: Bearer ...`가 붙습니다.

## MySQL 5.5 저장소 사용

MySQL 5.5는 `JSON` 타입이 없으므로 저장 스냅샷과 custom map은 `LONGTEXT` 컬럼에 JSON 문자열로 저장합니다.

```bash
mysql -u root -p < server/sql/mysql55-schema.sql
```

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

## 주요 API

### Health check

```bash
curl http://localhost:3000/api/health
```

응답에는 저장소 상태, 인증 상태, custom map 저장 상태, 실시간 접속자 통계가 포함됩니다.

### Custom Tiled map list

```bash
curl "http://localhost:3000/api/maps/custom?accountId=acc_xxx&characterId=char_xxx" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Custom Tiled map save

```bash
curl -X POST "http://localhost:3000/api/maps/custom?accountId=acc_xxx&characterId=char_xxx" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"map":{"id":"test-map","name":"테스트 맵","rows":["GGG","GPG","GGG"],"start":{"x":1,"y":1}}}'
```

### Save snapshot

```bash
curl -X POST http://localhost:3000/api/save/snapshot \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d @sample-snapshot.json
```

## 브라우저 UI 사용 흐름

1. 서버 실행
2. `http://localhost:3000/index.html` 접속
3. `계정` 섹션에서 로컬 계정명 저장
4. `SERVER AUTH` 패널에서 로그인
5. 캐릭터 슬롯 생성 또는 선택
6. `TILED MAP MANAGER`에서 custom Tiled JSON 붙여넣기 또는 기존 맵 선택
7. custom map 카드에서 `서버저장` 사용
8. `SERVER CUSTOM MAP SYNC`에서 `지금 동기화` 또는 `자동 동기화` 사용
9. `서버연동` 섹션에서 저장/복원 사용

## 저장 방식

파일 모드:

```text
server/data/saves.json
server/data/custom-maps.json
```

MySQL 모드:

```text
ydh_accounts
ydh_character_slots
ydh_save_snapshots
ydh_custom_maps
ydh_schema_meta
```

스냅샷은 `ydh_save_snapshots.snapshot_json`에 원본 그대로 보관하고, custom map은 `ydh_custom_maps.map_json`에 원본 그대로 보관합니다.

## 다음 고도화 후보

1. 운영용 관리자 저장 삭제/정리 API
2. 원격 아바타 클릭 정보창
3. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회
4. refresh token / 세션 저장소
