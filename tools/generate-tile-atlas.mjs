#!/usr/bin/env node
import { createHash } from 'node:crypto';
import { existsSync } from 'node:fs';
import { mkdir, readFile, writeFile } from 'node:fs/promises';
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
const CONFIG_PATH = path.join(outDir, 'tile-atlas-config.json');

const DEFAULT_CONFIG = {
  version: 1,
  tileSize: 64,
  columns: 6,
  rows: 1,
  webpQuality: 90,
  style: {
    noiseStrength: 0.31,
    gradientStrength: 0.65,
    grassBlades: 72,
    waterWaves: 4,
    portalSparkles: 28,
    stoneHighlight: true,
    roadCurve: true,
    treeCanopyRadius: 19
  },
  tiles: [
    { code: 'G', name: 'grass', fill: [58, 154, 82], accent: [27, 105, 55], detail: [175, 232, 149] },
    { code: 'R', name: 'road', fill: [145, 95, 52], accent: [210, 163, 95], detail: [116, 72, 39] },
    { code: 'S', name: 'stone', fill: [103, 115, 132], accent: [190, 198, 210], detail: [48, 55, 66] },
    { code: 'T', name: 'tree', fill: [28, 74, 41], accent: [43, 135, 66], detail: [108, 67, 38] },
    { code: 'W', name: 'water', fill: [34, 104, 173], accent: [142, 222, 255], detail: [64, 152, 213] },
    { code: 'P', name: 'portal', fill: [38, 31, 105], accent: [247, 200, 95], detail: [238, 244, 255] }
  ]
};

async function loadConfig() {
  if (!existsSync(CONFIG_PATH)) return DEFAULT_CONFIG;
  const parsed = JSON.parse(await readFile(CONFIG_PATH, 'utf8'));
  return {
    ...DEFAULT_CONFIG,
    ...parsed,
    style: { ...DEFAULT_CONFIG.style, ...(parsed.style || {}) },
    qualityLimits: { ...(DEFAULT_CONFIG.qualityLimits || {}), ...(parsed.qualityLimits || {}) },
    tiles: Array.isArray(parsed.tiles) && parsed.tiles.length ? parsed.tiles : DEFAULT_CONFIG.tiles
  };
}

let config;
let TILE;
let COLUMNS;
let ROWS;
let WIDTH;
let HEIGHT;
let PNG_PATH;
let WEBP_PATH;
let META_PATH;

function configure(loaded) {
  config = loaded;
  TILE = Number(config.tileSize || 64);
  COLUMNS = Number(config.columns || config.tiles.length || 6);
  ROWS = Number(config.rows || 1);
  WIDTH = TILE * COLUMNS;
  HEIGHT = TILE * ROWS;
  PNG_PATH = path.join(outDir, 'tiles-atlas.png');
  WEBP_PATH = path.join(outDir, 'tiles-atlas.webp');
  META_PATH = path.join(outDir, 'tiles-atlas.meta.json');
}

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

function mix(a, b, t, alpha = 255) {
  return [lerp(a[0], b[0], t), lerp(a[1], b[1], t), lerp(a[2], b[2], t), alpha];
}

function fillTile(buffer, tileIndex, tile) {
  const ox = tileIndex * TILE;
  const noiseStrength = Number(config.style.noiseStrength ?? 0.31);
  const gradientStrength = Number(config.style.gradientStrength ?? 0.65);
  for (let y = 0; y < TILE; y += 1) {
    for (let x = 0; x < TILE; x += 1) {
      const noise = ((x * 17 + y * 29 + tileIndex * 13) % 31) / 100 * noiseStrength;
      const grad = (x + y) / (TILE * 2);
      setPixel(buffer, ox + x, y, mix(tile.fill, tile.accent, Math.min(gradientStrength, grad + noise)));
    }
  }

  if (tile.code === 'G') drawGrass(buffer, ox, tile);
  if (tile.code === 'R') drawRoad(buffer, ox, tile);
  if (tile.code === 'S') drawStone(buffer, ox, tile);
  if (tile.code === 'T') drawTree(buffer, ox, tile);
  if (tile.code === 'W') drawWater(buffer, ox, tile);
  if (tile.code === 'P') drawPortal(buffer, ox, tile);
}

function drawGrass(buffer, ox, tile) {
  const count = Number(config.style.grassBlades || 72);
  for (let i = 0; i < count; i += 1) {
    const x = ox + ((i * 23) % (TILE - 4)) + 2;
    const y = ((i * 31) % (TILE - 14)) + 8;
    setPixel(buffer, x, y, [...(tile.detail || [171, 226, 143]), 220]);
    setPixel(buffer, x + 1, y - 1, [...(tile.detail || [171, 226, 143]), 180]);
  }
}

function drawRoad(buffer, ox, tile) {
  for (let y = 8; y < TILE - 8; y += 1) {
    const curve = config.style.roadCurve ? Math.sin(y / 8) * 5 : 0;
    const left = Math.floor(12 + curve);
    const right = Math.floor(TILE - 13 + Math.cos(y / 10) * 4);
    for (let x = left; x < right; x += 1) setPixel(buffer, ox + x, y, [...tile.accent, 255]);
  }
  for (let i = 0; i < 20; i += 1) setPixel(buffer, ox + 10 + ((i * 17) % 44), 13 + ((i * 23) % 38), [...(tile.detail || tile.fill), 130]);
}

function drawStone(buffer, ox, tile) {
  const blocks = [[6, 8, 20, 16], [30, 7, 25, 18], [10, 32, 24, 22], [40, 34, 17, 19]];
  blocks.forEach(([x, y, w, h]) => {
    for (let yy = y; yy < y + h; yy += 1) for (let xx = x; xx < x + w; xx += 1) setPixel(buffer, ox + xx, yy, mix(tile.fill, tile.accent, 0.45));
    if (config.style.stoneHighlight) for (let xx = x; xx < x + w; xx += 1) setPixel(buffer, ox + xx, y, [...tile.accent, 255]);
    for (let yy = y; yy < y + h; yy += 1) setPixel(buffer, ox + x, yy, [...(tile.detail || [57, 64, 74]), 255]);
  });
}

function drawTree(buffer, ox, tile) {
  const trunk = tile.detail || [111, 67, 37];
  for (let y = 36; y < TILE - 3; y += 1) for (let x = 29; x < 36; x += 1) setPixel(buffer, ox + x, y, [...trunk, 255]);
  const r = Number(config.style.treeCanopyRadius || 19);
  drawCircle(buffer, ox + 31, 22, r, [...tile.accent, 255]);
  drawCircle(buffer, ox + 19, 33, Math.max(12, r - 4), mix(tile.fill, tile.accent, 0.25));
  drawCircle(buffer, ox + 45, 33, Math.max(12, r - 4), mix(tile.fill, tile.accent, 0.1));
}

function drawWater(buffer, ox, tile) {
  const waves = Number(config.style.waterWaves || 4);
  for (let i = 0; i < waves; i += 1) {
    const y = 12 + i * Math.floor(TILE / (waves + 1));
    for (let x = 5; x < TILE - 6; x += 1) {
      const yy = Math.round(y + Math.sin(x / 5) * 3);
      setPixel(buffer, ox + x, yy, [...tile.accent, 220]);
      setPixel(buffer, ox + x, yy + 1, [...(tile.detail || tile.fill), 170]);
    }
  }
}

function drawPortal(buffer, ox, tile) {
  drawCircleOutline(buffer, ox + 32, 32, 23, [...tile.accent, 255], 4);
  drawCircleOutline(buffer, ox + 32, 32, 16, [...(tile.detail || [238, 244, 255]), 210], 3);
  const sparkles = Number(config.style.portalSparkles || 28);
  for (let i = 0; i < sparkles; i += 1) {
    const angle = i * 0.62;
    const radius = 5 + i * 0.65;
    setPixel(buffer, ox + 32 + Math.round(Math.cos(angle) * radius), 32 + Math.round(Math.sin(angle) * radius), [...(tile.detail || [255, 255, 255]), 230]);
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
  const quality = String(config.webpQuality || 90);
  try {
    await execFileAsync('cwebp', ['-quiet', '-q', quality, PNG_PATH, '-o', WEBP_PATH]);
    return 'cwebp';
  } catch {}

  try {
    const sharp = await import('sharp');
    await sharp.default(PNG_PATH).webp({ quality: Number(quality) }).toFile(WEBP_PATH);
    return 'sharp';
  } catch {}

  return '';
}

async function main() {
  await mkdir(outDir, { recursive: true });
  configure(await loadConfig());
  const rgba = Buffer.alloc(WIDTH * HEIGHT * 4, 255);
  config.tiles.forEach((tile, tileIndex) => fillTile(rgba, tileIndex, tile));
  const png = encodePng(rgba, WIDTH, HEIGHT);
  await writeFile(PNG_PATH, png);
  const webpTool = await maybeGenerateWebp();
  const meta = {
    generatedAt: new Date().toISOString(),
    configVersion: config.version || 1,
    configPath: path.relative(rootDir, CONFIG_PATH).replace(/\\/g, '/'),
    width: WIDTH,
    height: HEIGHT,
    tileWidth: TILE,
    tileHeight: TILE,
    columns: COLUMNS,
    rows: ROWS,
    webpQuality: Number(config.webpQuality || 90),
    png: path.relative(rootDir, PNG_PATH).replace(/\\/g, '/'),
    pngSha256: createHash('sha256').update(png).digest('hex'),
    webp: existsSync(WEBP_PATH) ? path.relative(rootDir, WEBP_PATH).replace(/\\/g, '/') : null,
    webpTool: webpTool || null,
    tiles: config.tiles.map((tile, index) => ({ code: tile.code, name: tile.name, x: index, y: 0 }))
  };
  await writeFile(META_PATH, JSON.stringify(meta, null, 2));
  console.log(`Generated ${meta.png}`);
  console.log(meta.webp ? `Generated ${meta.webp} with ${meta.webpTool}` : 'WebP skipped: install cwebp or npm package sharp to enable WebP output.');
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
