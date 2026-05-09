# YDH Chronicle RunNow Download

## 상태

바로 실행 가능한 모바일 웹게임 패키지를 GitHub Actions artifact로 생성하도록 반영했습니다.

## 포함 파일

- `run-now/index.html`
- `.github/workflows/package-run-now.yml`

## 다운로드 생성 방법

GitHub에서 다음 순서로 실행합니다.

```text
Actions -> Package RunNow ZIP -> Run workflow
```

실행 완료 후 artifact에서 아래 이름을 다운로드합니다.

```text
YDHChronicle-MobileGame-RunNow
```

다운로드 후 압축을 풀고 `index.html`을 실행하면 됩니다.

## 자동 생성 조건

아래 파일이 main 브랜치에 push되면 자동으로 artifact가 생성됩니다.

```text
run-now/**
assets/generated/**
.github/workflows/package-run-now.yml
```

## 실행 방법

```text
1. artifact 다운로드
2. 압축 해제
3. index.html 실행
4. Android에서는 파일관리자 또는 브라우저로 index.html 열기
```

## 포함 기능

- 모바일 터치 전투
- 월드맵 이동
- HP/MP/EXP/레벨/골드
- 스킬 6종
- 인벤토리
- 자동 저장 localStorage
- 클래스/NPC/몬스터/타일 SVG 갤러리

## APK와의 차이

RunNow 패키지는 즉시 실행 가능한 웹게임 패키지입니다.
APK는 별도 `Build WebView APK` workflow에서 생성합니다.
