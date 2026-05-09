# YDH Chronicle 남은 작업 분할 계획

## 8차~25차

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
- 서버 계정 인증, refresh token, MySQL 세션 저장소 완료

## 26차: 리소스/에셋/이미지/UI/애니메이션 보강

상태: 분할 진행 중

### 26-1: 리소스 매니페스트와 UI 품질 레이어

상태: 완료

작업 파일:

- `data/resource-catalog.js`
- `assets/ui/ydh-crest.svg`
- `assets/ui/ornament-frame.svg`
- `assets/effects/spark-rune.svg`
- `visual-polish.css`
- `ui-enhancements.js`
- `data/animations.js`
- `index.html`
- `docs/next-work-breakdown.md`

완료 내용:

- 리소스 catalog manifest 추가
- style/data/script/image 그룹 정의
- mobile/script/css/image budget 기준 정의
- low/balanced/high 품질 프리셋 추가
- critical/opportunistic image preload 목록 추가
- YDH crest UI SVG 추가
- ornament frame UI SVG 추가
- spark rune effect SVG 추가
- 전체 UI polish CSS 추가
- hero, panel, button, map tile, battle field glow 보강
- prefers-reduced-motion 대응 추가
- 모바일에서 과도한 hover/고정 장식 완화
- resource quality panel 추가
- 이미지 preload 진행률 표시
- 리소스 품질 low/balanced/high 토글 추가
- 품질 설정 localStorage 저장
- 애니메이션 timing/easing/effect preset 확장
- atlas/resource pipeline 문구 갱신

## 전체 상태

상태: 8차~25차 완료, 26-1 완료

현재 남은 고도화 후보:

1. 26-2 아이콘/아이템/스킬 이미지 세트 보강
2. 26-3 캐릭터/몬스터/NPC 스프라이트 품질 고도화
3. 26-4 맵 오브젝트/환경 이펙트 추가
4. 운영용 관리자 저장 삭제/정리 API
5. 원격 아바타 클릭 정보창
6. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회
7. custom map 파일→MySQL 마이그레이션 도구
8. 인증 세션 관리 UI

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
