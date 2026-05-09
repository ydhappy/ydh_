#!/usr/bin/env node
import { mkdir, writeFile, copyFile, readdir, stat, rm } from 'node:fs/promises';
import { existsSync } from 'node:fs';
import path from 'node:path';
import { spawn } from 'node:child_process';

const root = process.cwd();
const androidDir = path.join(root, 'android');
const appDir = path.join(androidDir, 'app');
const mainDir = path.join(appDir, 'src', 'main');
const javaDir = path.join(mainDir, 'java', 'com', 'ydhappy', 'ydhchronicle');
const resDir = path.join(mainDir, 'res');
const assetsDir = path.join(mainDir, 'assets', 'public');

async function ensureDir(dir) {
  await mkdir(dir, { recursive: true });
}

async function copyRecursive(src, dest) {
  if (!existsSync(src)) return;
  const info = await stat(src);
  if (info.isDirectory()) {
    await ensureDir(dest);
    for (const entry of await readdir(src)) {
      if (['.git', 'node_modules', 'android', 'server', '.github'].includes(entry)) continue;
      await copyRecursive(path.join(src, entry), path.join(dest, entry));
    }
    return;
  }
  await ensureDir(path.dirname(dest));
  await copyFile(src, dest);
}

function runNodeScript(scriptPath) {
  return new Promise((resolve, reject) => {
    const child = spawn(process.execPath, [scriptPath], { cwd: root, stdio: 'inherit' });
    child.on('exit', (code) => code === 0 ? resolve() : reject(new Error(`${scriptPath} failed with ${code}`)));
  });
}

async function main() {
  await ensureDir(javaDir);
  await ensureDir(path.join(resDir, 'values'));
  await ensureDir(path.join(resDir, 'drawable'));
  await ensureDir(assetsDir);

  await writeFile(path.join(root, 'settings.gradle'), `pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }\ndependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }\nrootProject.name='YDHChronicle'\ninclude ':app'\nproject(':app').projectDir=file('android/app')\n`, 'utf8');

  await writeFile(path.join(root, 'build.gradle'), `plugins { id 'com.android.application' version '8.7.3' apply false }\n`, 'utf8');

  await writeFile(path.join(appDir, 'build.gradle'), `plugins { id 'com.android.application' }\n\nandroid {\n    namespace 'com.ydhappy.ydhchronicle'\n    compileSdk 35\n    defaultConfig { applicationId 'com.ydhappy.ydhchronicle'; minSdk 23; targetSdk 35; versionCode 1; versionName '1.0.0' }\n}\n`, 'utf8');

  await writeFile(path.join(mainDir, 'AndroidManifest.xml'), `<manifest xmlns:android="http://schemas.android.com/apk/res/android">\n  <uses-permission android:name="android.permission.INTERNET" />\n  <application android:theme="@style/AppTheme" android:label="YDH Chronicle" android:icon="@mipmap/ic_launcher" android:roundIcon="@mipmap/ic_launcher_round" android:usesCleartextTraffic="true">\n    <activity android:name=".MainActivity" android:exported="true" android:screenOrientation="portrait">\n      <intent-filter>\n        <action android:name="android.intent.action.MAIN" />\n        <category android:name="android.intent.category.LAUNCHER" />\n      </intent-filter>\n    </activity>\n  </application>\n</manifest>\n`, 'utf8');

  await writeFile(path.join(resDir, 'values', 'styles.xml'), `<resources>\n  <style name="AppTheme" parent="android:style/Theme.Material.Light.NoActionBar">\n    <item name="android:windowNoTitle">true</item>\n    <item name="android:windowActionBar">false</item>\n    <item name="android:windowFullscreen">true</item>\n  </style>\n</resources>\n`, 'utf8');

  await writeFile(path.join(javaDir, 'MainActivity.java'), `package com.ydhappy.ydhchronicle;\n\nimport android.app.Activity;\nimport android.os.Bundle;\nimport android.webkit.WebSettings;\nimport android.webkit.WebView;\nimport android.webkit.WebViewClient;\n\npublic class MainActivity extends Activity {\n    private WebView webView;\n    @Override public void onCreate(Bundle savedInstanceState) {\n        super.onCreate(savedInstanceState);\n        webView = new WebView(this);\n        WebSettings settings = webView.getSettings();\n        settings.setJavaScriptEnabled(true);\n        settings.setDomStorageEnabled(true);\n        settings.setDatabaseEnabled(true);\n        settings.setAllowFileAccess(true);\n        settings.setAllowContentAccess(true);\n        webView.setWebViewClient(new WebViewClient());\n        setContentView(webView);\n        webView.loadUrl("file:///android_asset/public/index.html");\n    }\n    @Override public void onBackPressed() {\n        if (webView != null && webView.canGoBack()) webView.goBack();\n        else super.onBackPressed();\n    }\n}\n`, 'utf8');

  await rm(assetsDir, { recursive: true, force: true });
  await ensureDir(assetsDir);
  const files = ['index.html', 'styles.css', 'map.css', 'visual-polish.css', 'game.js', 'map-engine.js'];
  for (const file of files) await copyRecursive(path.join(root, file), path.join(assetsDir, file));
  for (const dir of ['assets', 'data']) await copyRecursive(path.join(root, dir), path.join(assetsDir, dir));
  const jsFiles = (await readdir(root)).filter((file) => file.endsWith('.js'));
  for (const file of jsFiles) await copyRecursive(path.join(root, file), path.join(assetsDir, file));

  await runNodeScript(path.join(root, 'tools', 'apply-android-branding.mjs'));
  console.log('Android project generated at android/app');
}

main().catch((error) => { console.error(error); process.exit(1); });
