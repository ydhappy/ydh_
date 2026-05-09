# Android APK Build Guide

YDH Chronicle 웹 게임을 Capacitor Android wrapper로 패키징합니다.

## 요구사항

- Node.js 20 이상 권장
- JDK 17
- Android Studio 또는 Android SDK/Gradle 환경

Capacitor Android는 기존 웹 앱을 Android WebView 기반 앱으로 감싸는 방식입니다. 이 저장소는 정적 웹 루트를 그대로 `webDir`로 사용합니다.

## 1. 의존성 설치

```bash
npm install
```

## 2. Android 프로젝트 생성

최초 1회:

```bash
npm run cap:add:android
```

이미 `android/` 폴더가 있으면 다음 명령만 실행합니다.

```bash
npm run cap:sync
```

## 3. Android Studio 열기

```bash
npm run android:open
```

## 4. Debug APK 빌드

```bash
npm run android:debug-apk
```

예상 산출물:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

## 5. Release APK 빌드

```bash
npm run android:release-apk
```

예상 산출물:

```text
android/app/build/outputs/apk/release/app-release-unsigned.apk
```

## 현재 APK 준비 상태

완료:

- `package.json` Capacitor dependency/script 추가
- `capacitor.config.json` 추가
- 정적 웹 루트 기반 Android packaging 구성

남은 단계:

- `npm install`
- `npm run cap:add:android`
- `npm run cap:sync`
- Android Studio 또는 GitHub Actions에서 APK 빌드

## 주의

- 현재 저장소에는 Android binary 프로젝트가 아직 생성되어 있지 않습니다.
- `android/` 폴더는 Capacitor CLI가 생성합니다.
- 실제 APK 파일은 로컬 빌드 또는 GitHub Actions artifact로 생성해야 합니다.
- 서버 API를 모바일 앱에서 외부 서버로 연결하려면 HTTPS 서버 주소와 CORS/인증 정책을 별도 설정해야 합니다.
