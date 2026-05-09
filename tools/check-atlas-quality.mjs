#!/usr/bin/env node
import { readFile, stat, writeFile } from 'node:fs/promises';
import { existsSync } from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rootDir = path.resolve(__dirname, '..');
const atlasDir = path.join(rootDir, 'assets', 'atlas');

const PNG_PATH = path.join(atlasDir, 'tiles-atlas.png');
const WEBP_PATH = path.join(atlasDir, 'tiles-atlas.webp');
const SVG_PATH = path.join(atlasDir, 'tiles-atlas.svg');
const META_PATH = path.join(atlasDir, 'tiles-atlas.meta.json');
const REPORT_PATH = path.join(atlasDir, 'tiles-atlas.quality.json');

const expected = {
  width: 384,
  height: 64,
  tileWidth: 64,
  tileHeight: 64,
  columns: 6,
  rows: 1,
  tileOrder: ['G', 'R', 'S', 'T', 'W', 'P'],
  maxPngBytes: 24 * 1024,
  maxWebpBytes: 16 * 1024
};

function fail(message) {
  const error = new Error(message);
  error.isQualityFailure = true;
  throw error;
}

async function fileInfo(filePath) {
  if (!existsSync(filePath)) return null;
  const data = await readFile(filePath);
  const info = await stat(filePath);
  return { path: path.relative(rootDir, filePath).replace(/\\/g, '/'), bytes: info.size, data };
}

function readPngDimensions(buffer) {
  if (!buffer || buffer.length < 24) fail('PNG file is too small.');
  const signature = buffer.subarray(0, 8).toString('hex');
  if (signature !== '89504e470d0a1a0a') fail('PNG signature mismatch.');
  return {
    width: buffer.readUInt32BE(16),
    height: buffer.readUInt32BE(20)
  };
}

function readWebpInfo(buffer) {
  if (!buffer || buffer.length < 16) return { ok: false, reason: 'WebP file is too small.' };
  const riff = buffer.subarray(0, 4).toString('ascii');
  const webp = buffer.subarray(8, 12).toString('ascii');
  return { ok: riff === 'RIFF' && webp === 'WEBP', riff, webp };
}

async function main() {
  const png = await fileInfo(PNG_PATH);
  const webp = await fileInfo(WEBP_PATH);
  const svg = await fileInfo(SVG_PATH);
  const metaFile = await fileInfo(META_PATH);

  if (!png) fail('Missing assets/atlas/tiles-atlas.png. Run node tools/generate-tile-atlas.mjs first.');
  if (!svg) fail('Missing assets/atlas/tiles-atlas.svg fallback.');
  if (!metaFile) fail('Missing assets/atlas/tiles-atlas.meta.json.');

  const dimensions = readPngDimensions(png.data);
  if (dimensions.width !== expected.width || dimensions.height !== expected.height) {
    fail(`PNG dimension mismatch: expected ${expected.width}x${expected.height}, got ${dimensions.width}x${dimensions.height}`);
  }
  if (png.bytes > expected.maxPngBytes) {
    fail(`PNG too large: ${png.bytes} bytes > ${expected.maxPngBytes} bytes`);
  }

  const meta = JSON.parse(metaFile.data.toString('utf8'));
  if (meta.width !== expected.width || meta.height !== expected.height) fail('meta width/height mismatch.');
  if (meta.tileWidth !== expected.tileWidth || meta.tileHeight !== expected.tileHeight) fail('meta tile size mismatch.');
  if (meta.columns !== expected.columns || meta.rows !== expected.rows) fail('meta grid mismatch.');

  const tileOrder = (meta.tiles || []).map((tile) => tile.code);
  if (tileOrder.join(',') !== expected.tileOrder.join(',')) {
    fail(`tile order mismatch: expected ${expected.tileOrder.join(',')}, got ${tileOrder.join(',')}`);
  }

  let webpInfo = null;
  if (webp) {
    const parsed = readWebpInfo(webp.data);
    if (!parsed.ok) fail(`WebP signature mismatch: ${parsed.reason || `${parsed.riff}/${parsed.webp}`}`);
    if (webp.bytes > expected.maxWebpBytes) fail(`WebP too large: ${webp.bytes} bytes > ${expected.maxWebpBytes} bytes`);
    webpInfo = { path: webp.path, bytes: webp.bytes, ok: true };
  }

  const report = {
    ok: true,
    checkedAt: new Date().toISOString(),
    expected,
    png: { path: png.path, bytes: png.bytes, width: dimensions.width, height: dimensions.height, ok: true },
    webp: webpInfo || { path: 'assets/atlas/tiles-atlas.webp', bytes: 0, ok: false, reason: 'not generated' },
    svg: { path: svg.path, bytes: svg.bytes, ok: true },
    meta: { path: metaFile.path, bytes: metaFile.bytes, ok: true }
  };

  await writeFile(REPORT_PATH, JSON.stringify(report, null, 2), 'utf8');
  console.log(JSON.stringify(report, null, 2));
}

main().catch(async (error) => {
  const report = {
    ok: false,
    checkedAt: new Date().toISOString(),
    error: error.message
  };
  try {
    await writeFile(REPORT_PATH, JSON.stringify(report, null, 2), 'utf8');
  } catch {}
  console.error(error.message);
  process.exit(1);
});
