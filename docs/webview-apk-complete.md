# YDH Chronicle WebView APK Conversion

## 상태

YDH Chronicle은 Android WebView APK 빌드 경로로 전환되었습니다.

## 추가된 핵심 파일

```text
settings.gradle
build.gradle
tools/create-android-project.mjs
.github/workflows/build-webview-apk.yml
```

## 로컬 APK 빌드

```bash
npm run android:webview-debug-apk
```

또는 단계별 실행:

```bash
npm run android:generate
gradle app:assembleDebug
```

## APK 산출물

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions APK 빌드

```text
Actions -> Build WebView APK -> Run workflow
```

또는 main 브랜치에 웹/에셋/데이터/생성 스크립트 변경이 push되면 자동 실행됩니다.

## Android WebView 특징

- 웹 게임 전체를 APK 내부 asset으로 포함합니다.
- 시작 URL은 `file:///android_asset/public/index.html`입니다.
- JavaScript, DOM Storage, file access를 활성화합니다.
- 뒤로가기 버튼은 WebView history 우선 처리합니다.

## 다음 고도화

1. 앱 아이콘 PNG density 리소스 자동 생성
2. splash screen native 적용
3. WebView file URL fetch/local asset 호환성 점검
4. 서버 API base URL 설정 패널 추가
5. release signing 설정 추가
