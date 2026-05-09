#!/usr/bin/env node
import { createHash } from 'node:crypto';
import { existsSync } from 'node:fs';
import { mkdir, writeFile } from 'node:fs/promises';
import { deflateSync } from 'node:zlib';
import { execFile } from 'node:child_process';
import { promisify } from 'node:util';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const execFileAsync = promisify(execFile);
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rootDir = path.resolve(__dirname, '..');
const outDir = path.join(rootDir, 'assets', 'atlas');

const TILE = 64;
const COLUMNS = 6;
const ROWS = 1;
const WIDTH = TILE * COLUMNS;
const HEIGHT = TILE * ROWS;
const PNG_PATH = path.join(outDir, 'tiles-atlas.png');
const WEBP_PATH = path.join(outDir, 'tiles-atlas.webp');
const META_PATH = path.join(outDir, 'tiles-atlas.meta.json');

const tiles = [
  { code: 'G', name: 'grass', fill: [61, 148, 82], accent: [39, 108, 58] },
  { code: 'R', name: 'road', fill: [153, 103, 58], accent: [199, 152, 86] },
  { code: 'S', name: 'stone', fill: [105, 116, 132], accent: [181, 188, 199] },
  { code: 'T', name: 'tree', fill: [32, 80, 43], accent: [104, 67, 38] },
  { code: 'W', name: 'water', fill: [39, 112, 172], accent: [151, 224, 255] },
  { code: 'P', name: 'portal', fill: [42, 35, 105], accent: [247, 200, 95] }
];

function index(x, y) {
  return (y * WIDTH + x) * 4;
}

function setPixel(buffer, x, y, rgba) {
  if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) return;
  const i = index(x, y);
  buffer[i] = rgba[0];
  buffer[i + 1] = rgba[1];
  buffer[i + 2] = rgba[2];
  buffer[i + 3] = rgba[3] ?? 255;
}

function lerp(a, b, t) {
  return Math.round(a + (b - a) * t);
}

function mix(a, b, t) {
  return [lerp(a[0], b[0], t), lerp(a[1], b[1], t), lerp(a[2], b[2], t), 255];
}

function fillTile(buffer, tileIndex, tile) {
  const ox = tileIndex * TILE;
  for (let y = 0; y < TILE; y += 1) {
    for (let x = 0; x < TILE; x += 1) {
      const noise = ((x * 17 + y * 29 + tileIndex * 13) % 31) / 100;
      const grad = (x + y) / (TILE * 2);
      setPixel(buffer, ox + x, y, mix(tile.fill, tile.accent, Math.min(0.65, grad + noise)));
    }
  }

  if (tile.code === 'G') drawGrass(buffer, ox);
  if (tile.code === 'R') drawRoad(buffer, ox);
  if (tile.code === 'S') drawStone(buffer, ox);
  if (tile.code === 'T') drawTree(buffer, ox);
  if (tile.code === 'W') drawWater(buffer, ox);
  if (tile.code === 'P') drawPortal(buffer, ox);
}

function drawGrass(buffer, ox) {
  for (let i = 0; i < 55; i += 1) {
    const x = ox + ((i * 23) % 60) + 2;
    const y = ((i * 31) % 50) + 8;
    setPixel(buffer, x, y, [171, 226, 143, 210]);
    setPixel(buffer, x + 1, y - 1, [171, 226, 143, 180]);
  }
}

function drawRoad(buffer, ox) {
  for (let y = 8; y < 56; y += 1) {
    const left = Math.floor(12 + Math.sin(y / 8) * 5);
    const right = Math.floor(51 + Math.cos(y / 10) * 4);
    for (let x = left; x < right; x += 1) {
      setPixel(buffer, ox + x, y, [171, 119, 67, 255]);
    }
  }
}

function drawStone(buffer, ox) {
  const blocks = [[6, 8, 20, 16], [30, 7, 25, 18], [10, 32, 24, 22], [40, 34, 17, 19]];
  blocks.forEach(([x, y, w, h]) => {
    for (let yy = y; yy < y + h; yy += 1) for (let xx = x; xx < x + w; xx += 1) setPixel(buffer, ox + xx, yy, [141, 151, 165, 255]);
    for (let xx = x; xx < x + w; xx += 1) setPixel(buffer, ox + xx, y, [222, 226, 232, 255]);
    for (let yy = y; yy < y + h; yy += 1) setPixel(buffer, ox + x, yy, [57, 64, 74, 255]);
  });
}

function drawTree(buffer, ox) {
  for (let y = 36; y < 61; y += 1) for (let x = 29; x < 36; x += 1) setPixel(buffer, ox + x, y, [111, 67, 37, 255]);
  drawCircle(buffer, ox + 31, 22, 19, [46, 129, 63, 255]);
  drawCircle(buffer, ox + 19, 33, 15, [37, 105, 55, 255]);
  drawCircle(buffer, ox + 45, 33, 15, [31, 93, 49, 255]);
}

function drawWater(buffer, ox) {
  for (let i = 0; i < 3; i += 1) {
    const y = 18 + i * 15;
    for (let x = 5; x < 58; x += 1) {
      const yy = Math.round(y + Math.sin(x / 5) * 3);
      setPixel(buffer, ox + x, yy, [185, 238, 255, 220]);
      setPixel(buffer, ox + x, yy + 1, [114, 193, 231, 170]);
    }
  }
}

function drawPortal(buffer, ox) {
  drawCircleOutline(buffer, ox + 32, 32, 23, [247, 200, 95, 255], 4);
  drawCircleOutline(buffer, ox + 32, 32, 16, [238, 244, 255, 210], 3);
  for (let i = 0; i < 22; i += 1) {
    const angle = i * 0.62;
    const radius = 5 + i * 0.75;
    setPixel(buffer, ox + 32 + Math.round(Math.cos(angle) * radius), 32 + Math.round(Math.sin(angle) * radius), [255, 255, 255, 230]);
  }
}

function drawCircle(buffer, cx, cy, r, color) {
  for (let y = -r; y <= r; y += 1) for (let x = -r; x <= r; x += 1) if (x * x + y * y <= r * r) setPixel(buffer, cx + x, cy + y, color);
}

function drawCircleOutline(buffer, cx, cy, r, color, width = 1) {
  for (let y = -r - width; y <= r + width; y += 1) {
    for (let x = -r - width; x <= r + width; x += 1) {
      const d = Math.sqrt(x * x + y * y);
      if (d >= r - width && d <= r + width) setPixel(buffer, cx + x, cy + y, color);
    }
  }
}

function crc32(buffer) {
  let crc = -1;
  for (let i = 0; i < buffer.length; i += 1) {
    crc ^= buffer[i];
    for (let j = 0; j < 8; j += 1) crc = (crc >>> 1) ^ (0xedb88320 & -(crc & 1));
  }
  return (crc ^ -1) >>> 0;
}

function chunk(type, data) {
  const typeBuffer = Buffer.from(type, 'ascii');
  const len = Buffer.alloc(4);
  len.writeUInt32BE(data.length, 0);
  const crc = Buffer.alloc(4);
  crc.writeUInt32BE(crc32(Buffer.concat([typeBuffer, data])), 0);
  return Buffer.concat([len, typeBuffer, data, crc]);
}

function encodePng(rgba, width, height) {
  const raw = Buffer.alloc((width * 4 + 1) * height);
  for (let y = 0; y < height; y += 1) {
    raw[y * (width * 4 + 1)] = 0;
    rgba.copy(raw, y * (width * 4 + 1) + 1, y * width * 4, (y + 1) * width * 4);
  }

  const header = Buffer.alloc(13);
  header.writeUInt32BE(width, 0);
  header.writeUInt32BE(height, 4);
  header[8] = 8;
  header[9] = 6;
  header[10] = 0;
  header[11] = 0;
  header[12] = 0;

  return Buffer.concat([
    Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]),
    chunk('IHDR', header),
    chunk('IDAT', deflateSync(raw, { level: 9 })),
    chunk('IEND', Buffer.alloc(0))
  ]);
}

async function maybeGenerateWebp() {
  try {
    await execFileAsync('cwebp', ['-quiet', '-q', '90', PNG_PATH, '-o', WEBP_PATH]);
    return 'cwebp';
  } catch {}

  try {
    const sharp = await import('sharp');
    await sharp.default(PNG_PATH).webp({ quality: 90 }).toFile(WEBP_PATH);
    return 'sharp';
  } catch {}

  return '';
}

async function main() {
  await mkdir(outDir, { recursive: true });
  const rgba = Buffer.alloc(WIDTH * HEIGHT * 4, 255);
  tiles.forEach((tile, tileIndex) => fillTile(rgba, tileIndex, tile));
  const png = encodePng(rgba, WIDTH, HEIGHT);
  await writeFile(PNG_PATH, png);
  const webpTool = await maybeGenerateWebp();
  const meta = {
    generatedAt: new Date().toISOString(),
    width: WIDTH,
    height: HEIGHT,
    tileWidth: TILE,
    tileHeight: TILE,
    columns: COLUMNS,
    rows: ROWS,
    png: path.relative(rootDir, PNG_PATH).replace(/\\/g, '/'),
    pngSha256: createHash('sha256').update(png).digest('hex'),
    webp: existsSync(WEBP_PATH) ? path.relative(rootDir, WEBP_PATH).replace(/\\/g, '/') : null,
    webpTool: webpTool || null,
    tiles: tiles.map((tile, index) => ({ code: tile.code, name: tile.name, x: index, y: 0 }))
  };
  await writeFile(META_PATH, JSON.stringify(meta, null, 2));
  console.log(`Generated ${meta.png}`);
  console.log(meta.webp ? `Generated ${meta.webp} with ${meta.webpTool}` : 'WebP skipped: install cwebp or npm package sharp to enable WebP output.');
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
