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
| `YDH_DATA_DIR` | `server/data` | 저장 파일 위치 |
| `YDH_PUBLIC_DIR` | repository root | 정적 파일 제공 위치 |

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

브라우저의 `최신 저장 복원`, `복원 후 새로고침` 버튼과 연결됩니다.

```bash
curl http://localhost:3000/api/save/restore
```

## 브라우저 UI 사용 흐름

1. 서버 실행
2. `http://localhost:3000/index.html` 접속
3. 게임 진행
4. `서버연동` 섹션에서 `서버 저장` 클릭
5. `서버 저장목록`으로 저장 여부 확인
6. `최신 저장 복원` 또는 `복원 후 새로고침`으로 서버 저장 데이터를 localStorage에 복원

## 저장 방식

초기 구현은 DB가 아니라 파일 저장입니다.

```text
server/data/saves.json
```

최근 50개 스냅샷만 보관합니다.

## 다음 고도화 후보

1. 계정 로그인 추가
2. 캐릭터 슬롯 추가
3. SQLite 또는 MariaDB 저장소 교체
4. WebSocket 위치 동기화
5. Tiled Map Editor JSON import
