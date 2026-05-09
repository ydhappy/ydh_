# YDH Chronicle 남은 작업 분할 계획

## 8차~24차

상태: 완료

요약:

- 소설 챕터 퀘스트 시스템 완료
- 아이템/장착/도감/GM 콘솔 완료
- 계정/캐릭터 선택 완료
- Node/Express 저장 서버 완료
- MySQL 5.5 저장소 완료
- 서버 저장 슬롯 복원 완료
- WebSocket 위치 동기화 완료
- 실시간 타일맵 원격 아바타 렌더링 완료
- Tiled Map Editor JSON import 완료
- 서버 custom map 자동 동기화, scope 분리, MySQL 저장소 완료
- PNG/WebP atlas 생성 파이프라인과 readiness 표시 완료

## 25차: 서버 계정 인증

상태: 완료

### 25-1: 선택형 서버 계정 인증 기반

상태: 완료

작업 파일:

- `server/src/auth.js`
- `server/src/server.js`
- `server-auth.js`
- `server-auth-panel.js`
- `index.html`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- 서버 HMAC access token 유틸 추가
- `/api/auth/status`, `/api/auth/login`, `/api/auth/me` 추가
- `YDH_AUTH_REQUIRED=false` 기본값 유지
- `YDH_AUTH_REQUIRED=true`일 때 주요 API Bearer token 보호
- snapshot/custom map scope에 인증 accountId 우선 반영
- 브라우저 `/api/*` fetch에 Authorization header 자동 첨부
- `SERVER AUTH` 로그인/로그아웃 패널 추가

### 25-2: refresh token / 파일 세션 저장소

상태: 완료

작업 파일:

- `server/src/auth-sessions.js`
- `server/src/auth.js`
- `server/src/server.js`
- `server-auth.js`
- `server-auth-panel.js`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- 파일 기반 refresh session 저장소 추가
- 세션 저장 파일 `server/data/auth-sessions.json` 추가 구조 정의
- 원본 refresh token 대신 SHA-256 hash 저장
- `/api/auth/login` 응답에 `refreshToken`, `session` 추가
- `/api/auth/refresh` 추가
- `/api/auth/logout` 추가
- refresh 성공 시 refresh token 회전 처리
- access token 기본 TTL을 15분으로 조정
- refresh token 기본 TTL을 30일로 설정
- `/api/health`에 refresh session 상태 포함
- 브라우저에서 401 발생 시 refresh 후 원 요청 1회 재시도
- `SERVER AUTH` 패널에 access/refresh/session 상태 표시
- `SERVER AUTH` 패널에 수동 재발급 버튼 추가

### 25-3: refresh session MySQL 저장소

상태: 완료

작업 파일:

- `server/src/mysql-auth-sessions.js`
- `server/src/auth-sessions-file.js`
- `server/src/auth-sessions.js`
- `server/src/auth-session-provider.js`
- `server/sql/mysql55-schema.sql`
- `server/README.md`
- `docs/next-work-breakdown.md`

완료 내용:

- MySQL 전용 refresh session provider 추가
- 기존 파일 세션 provider를 `auth-sessions-file.js`로 분리
- 기존 `auth-sessions.js`를 storage mode delegator로 교체
- `YDH_STORAGE=file`이면 파일 세션 provider 사용
- `YDH_STORAGE=mysql`이면 MySQL 세션 provider 사용
- `auth.js` import 수정 없이 기존 import 경로 그대로 MySQL 전환 가능
- `ydh_auth_sessions` 테이블 schema 추가
- refresh token 원본 대신 `token_hash` 저장
- session 생성/회전/폐기/health/list 함수 구현
- schema version meta 값을 `4`로 갱신

## 전체 상태

상태: 8차~25차 완료

현재 남은 고도화 후보:

1. 운영용 관리자 저장 삭제/정리 API
2. 원격 아바타 클릭 정보창
3. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회
4. custom map 파일→MySQL 마이그레이션 도구
5. 인증 세션 관리 UI

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
