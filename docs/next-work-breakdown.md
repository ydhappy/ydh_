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

### 26-2: 생성형 클래스/NPC/몬스터/환경 에셋 GitHub 반영

상태: 완료

작업 파일:

- `assets/generated/classes-showcase.svg`
- `assets/generated/npc-showcase.svg`
- `assets/generated/monster-showcase.svg`
- `assets/generated/environment-showcase.svg`
- `data/resource-catalog.js`
- `generated-asset-gallery.js`
- `index.html`
- `docs/next-work-breakdown.md`

완료 내용:

- 클래스 6종 생성 이미지 컨셉을 generated asset preview로 등록
- NPC 8종 생성 이미지 컨셉을 generated asset preview로 등록
- 몬스터 8종 생성 이미지 컨셉을 generated asset preview로 등록
- 환경 타일/오브젝트 생성 이미지 컨셉을 generated asset preview로 등록
- `resource-catalog.js` version 2로 갱신
- `generatedConcepts` 그룹 추가
- generated concept 4종을 opportunistic preload 대상에 추가
- 페이지 내 `GENERATED ASSET CONCEPTS` 갤러리 패널 추가
- asset 카드 클릭 시 개별 preview SVG를 새 창에서 확인 가능
- 현재 연결 도구의 binary 업로드 한계로 원본 PNG 직접 커밋 대신 경량 SVG preview card 방식 적용
- 추후 binary 업로드 경로가 안정화되면 같은 asset id/path 체계로 PNG/WebP 교체 가능

### 26-3: 생성 에셋 상세 카탈로그와 제작 사양 연결

상태: 완료

작업 파일:

- `data/generated-asset-detail-catalog.js`
- `generated-asset-gallery.js`
- `index.html`
- `docs/next-work-breakdown.md`

완료 내용:

- 16방향 direction order와 degree 기준 정의
- 클래스/NPC/몬스터/타일별 sprite sheet target cell size 정의
- 클래스 6종 역할, 장비, 팔레트, 실루엣, 기본 스탯, 스킬, sprite priority 정의
- NPC 8종 역할, interaction, service, anchor map 정의
- 몬스터 8종 rank, family, habitat, AI, drop, 기본 전투 스탯 정의
- 환경 terrain/props/animated/collision 분류 정의
- production checklist 추가
- 갤러리 상세 패널에 제작 사양 표시
- 갤러리 상세 패널에 클래스/NPC/몬스터/환경 요약 카드 표시
- `index.html`에 상세 카탈로그 로드 순서 연결

### 26-4: 아이콘/아이템/스킬 이미지 세트 보강

상태: 완료

작업 파일:

- `assets/icons/dark-fantasy-icon-atlas.svg`
- `data/icon-item-skill-catalog.js`
- `icon-item-skill-gallery.js`
- `generated-asset-gallery.js`

완료 내용:

- 다크 판타지 SVG 아이콘 atlas 36칸 추가
- 아이템 18종 catalog 추가
- 스킬 18종 catalog 추가
- 아이템/스킬 갤러리 UI 추가
- atlas background-position 기반 아이콘 출력
- rarity별 카드 테두리 스타일 적용
- generated asset gallery에서 icon catalog/gallery 자동 로드

### 26-5: 아이콘 실제 UI 연결

상태: 완료

작업 파일:

- `icon-runtime-bindings.js`
- `generated-asset-gallery.js`
- `docs/next-work-breakdown.md`

완료 내용:

- 기존 `game.js` 직접 수정 없이 런타임 보조 레이어로 연결
- skill card의 기존 emoji icon을 atlas icon으로 자동 치환
- 기본 스킬 alias 매핑 추가
- inventory list item을 atlas icon + text 구조로 자동 치환
- 아이템 이름/키워드 alias 매핑 추가
- MutationObserver로 스킬/인벤토리 재렌더링 대응
- 800ms fallback interval로 동적 렌더링 누락 보정
- generated asset gallery에서 `icon-runtime-bindings.js` 자동 로드

## 27차: APK 패키징 준비

상태: 준비 완료

작업 파일:

- `package.json`
- `capacitor.config.json`
- `.github/workflows/build-android-apk.yml`
- `docs/android-apk-build.md`
- `docs/next-work-breakdown.md`

완료 내용:

- Capacitor Android dependency/script 추가
- Android app id `com.ydhappy.ydhchronicle` 정의
- app name `YDH Chronicle` 정의
- 정적 웹 루트 `webDir: .` 설정
- Android debug/release APK 빌드 script 추가
- GitHub Actions 수동 APK build workflow 추가
- debug/release build_type 선택 지원
- workflow artifact로 APK 업로드 설정
- APK 빌드 문서 추가

실제 APK 생성 절차:

```bash
npm install
npm run cap:add:android
npm run cap:sync
npm run android:debug-apk
```

GitHub Actions:

```text
Actions → Build Android APK → Run workflow → build_type=debug
```

## 전체 상태

상태: 8차~25차 완료, 26-1~26-5 완료, 27차 APK 준비 완료

현재 남은 고도화 후보:

1. GitHub Actions APK workflow 수동 실행 후 artifact 확인
2. Android 앱 아이콘/splash 리소스 추가
3. 실제 android/ 프로젝트 생성 결과 커밋
4. 캐릭터/몬스터/NPC 스프라이트 데이터 연결 고도화
5. 맵 오브젝트/환경 이펙트 추가
6. 운영용 관리자 저장 삭제/정리 API
7. 원격 아바타 클릭 정보창
8. MySQL 정규화 테이블 기반 캐릭터별 최신 저장 조회
9. custom map 파일→MySQL 마이그레이션 도구
10. 인증 세션 관리 UI

## 원칙

- 한 번에 대규모 파일 수정 금지
- 데이터 파일 → UI 파일 → 연결 파일 순서
- 기존 플레이 가능 상태 유지
- 모바일 화면 우선
