# YDH Chronicle Mobile MMORPG Server

## 목적

모바일 MMORPG 클라이언트가 실제 서버와 연결되어 접속자 상태, 위치, 클래스, 레벨, HP, 채팅을 공유하도록 합니다.

## 추가된 서버 파일

```text
server/mobile-mmo-gateway.js
server/mobile-mmo-server.js
```

## 설치

```bash
npm install
```

## 서버 실행

```bash
npm run mmo:server
```

또는:

```bash
npm run mmo:start
```

기본 포트:

```text
8787
```

접속 URL:

```text
http://127.0.0.1:8787/
```

WebSocket URL:

```text
ws://127.0.0.1:8787/mmo
```

## 모바일에서 접속

같은 Wi-Fi 안에서 PC 서버를 실행한 뒤, 휴대폰 브라우저에서 PC의 내부 IP로 접속합니다.

예시:

```text
http://192.168.0.10:8787/
```

## APK에서 서버 연결

APK 또는 file 실행 시 기본 WebSocket 주소는 다음입니다.

```text
ws://127.0.0.1:8787/mmo
```

실제 휴대폰에서 PC 서버에 연결하려면 브라우저 개발 콘솔 또는 추후 설정 UI에서 아래 값을 저장합니다.

```js
localStorage.setItem('ydh-mmo-ws-url', 'ws://192.168.0.10:8787/mmo');
```

## 현재 동기화 기능

```text
접속자 hello
플레이어 상태 broadcast
위치/클래스/레벨/HP 동기화
채팅 broadcast
접속 종료 leave broadcast
30초 이상 미갱신 peer 정리
```

## 클라이언트 파일

```text
mobile-mmorpg/index-online.html
mobile-mmorpg/game.js
mobile-mmorpg/net.js
mobile-mmorpg/style.css
```

## APK 시작점

Android WebView APK 생성 시 시작점은 다음으로 설정되어 있습니다.

```text
file:///android_asset/public/mobile-mmorpg/index-online.html
```

## 다음 고도화 후보

1. 서버 주소 설정 UI
2. 계정 로그인/캐릭터명 동기화
3. 원격 플레이어를 Canvas 월드에 직접 렌더링
4. 서버 권위형 몬스터/전투 판정
5. 채팅 채널/귓속말/파티
6. MySQL 캐릭터 저장 연동
7. 월드맵 분리와 zone 서버 구조
