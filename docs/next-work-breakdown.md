# YDH Chronicle 남은 작업 분할 계획

## 8차: 소설 챕터 퀘스트 시스템

상태: 완료

## 9차: 아이템 등급/장착 슬롯 UI

상태: 완료

## 10차: 도감 해금 시스템

상태: 완료

## 11차: GM 맵/스폰 관리 콘솔

상태: 완료

## 12차: 전용 소설 NPC/몬스터 16방향 SVG 제작

상태: 완료

## 13차: 서버/API 연동 준비

상태: 완료

## 14차: 실제 Node/Express 저장 서버 구현

상태: 완료

## 15차: 서버 저장 복원 UI 연결

상태: 완료

## 16차: 계정/캐릭터 선택 화면

상태: 완료

## 17차: MySQL 5.5 저장소 고도화

상태: 완료

## 18차: 서버 저장 슬롯 선택 복원 UI

상태: 완료

## 19차: MySQL 계정/캐릭터 정규화 테이블 분리

상태: 완료

## 20차: WebSocket 위치 동기화

상태: 완료

작업 파일:

- `server/package.json`
- `server/src/realtime.js`
- `server/src/server.js`
- `realtime-sync.css`
- `realtime-sync.js`
- `index.html`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- `ws` dependency 추가
- Express 서버를 `http.createServer(app)` 기반으로 변경
- WebSocket endpoint `/ws/position` 추가
- 실시간 접속자 peer registry 추가
- `hello`, `position`, `ping` 메시지 처리 추가
- `connected`, `welcome`, `peer-joined`, `peer-position`, `peer-left`, `error` 응답 메시지 정의
- `/api/realtime/stats` 실시간 접속자 현황 API 추가
- `/api/health` 응답에 realtime 통계 포함
- 클라이언트 `실시간` 섹션 추가
- WebSocket 연결/해제/현재 위치 송신 UI 추가
- `ydh-player-moved` 이벤트 기반 위치 자동 송신 추가
- 다른 접속자의 계정명/캐릭터명/클래스/맵/좌표 표시 카드 추가
- README에 WebSocket 테스트 흐름 문서화

## 전체 상태

상태: 8차~20차 완료

현재 남은 고도화 후보:

1. 실시간 타일맵 위 원격 플레이어 아바타 렌더링
2. Tiled Map Editor JSON import
3. 실제 PNG/WebP atlas 교체
4. 서버 계정 인증 추가
5. 운영용 관리자 저장 삭제/정리 API
6. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
