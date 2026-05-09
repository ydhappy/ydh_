#!/usr/bin/env node
import { mkdir, writeFile } from 'node:fs/promises';
import path from 'node:path';

const root = process.cwd();
const resDir = path.join(root, 'android', 'app', 'src', 'main', 'res');

async function ensure(dir) {
  await mkdir(dir, { recursive: true });
}

async function write(rel, content) {
  const file = path.join(resDir, rel);
  await ensure(path.dirname(file));
  await writeFile(file, content, 'utf8');
}

const colors = `<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ydh_bg">#070B12</color>
    <color name="ydh_panel">#101827</color>
    <color name="ydh_gold">#F7C85F</color>
    <color name="ydh_blue">#73A7FF</color>
</resources>
`;

const foreground = `<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:fillColor="#101827" android:pathData="M54,8 L88,23 L88,51 C88,73 75,90 54,100 C33,90 20,73 20,51 L20,23 Z"/>
    <path android:strokeColor="#F7C85F" android:strokeWidth="4" android:fillColor="#00000000" android:pathData="M54,8 L88,23 L88,51 C88,73 75,90 54,100 C33,90 20,73 20,51 L20,23 Z"/>
    <path android:fillColor="#F7C85F" android:pathData="M35,34 L47,34 L54,48 L61,34 L73,34 L60,58 L60,76 L48,76 L48,58 Z"/>
    <path android:strokeColor="#73A7FF" android:strokeWidth="3" android:strokeLineCap="round" android:fillColor="#00000000" android:pathData="M34,82 C44,76 64,76 74,82"/>
    <path android:fillColor="#EEF4FF" android:pathData="M54,21 m-4,0 a4,4 0,1 0,8 0 a4,4 0,1 0,-8 0"/>
</vector>
`;

const background = `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#070B12"/>
</shape>
`;

const splash = `<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="#070B12"/>
        </shape>
    </item>
    <item android:gravity="center" android:width="160dp" android:height="160dp" android:drawable="@drawable/ic_launcher_foreground"/>
</layer-list>
`;

const adaptiveIcon = `<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
`;

const styles = `<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="AppTheme" parent="android:style/Theme.Material.Light.NoActionBar">
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowActionBar">false</item>
        <item name="android:windowFullscreen">true</item>
        <item name="android:windowBackground">@drawable/splash_screen</item>
        <item name="android:fontFamily">sans</item>
        <item name="android:colorAccent">@color/ydh_gold</item>
    </style>
</resources>
`;

await write('values/colors.xml', colors);
await write('values/styles.xml', styles);
await write('drawable/ic_launcher_background.xml', background);
await write('drawable/ic_launcher_foreground.xml', foreground);
await write('drawable/splash_screen.xml', splash);
await write('mipmap-anydpi-v26/ic_launcher.xml', adaptiveIcon);
await write('mipmap-anydpi-v26/ic_launcher_round.xml', adaptiveIcon);

console.log('Android branding resources generated.');
