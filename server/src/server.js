import express from 'express';
import http from 'node:http';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { attachRealtimeServer } from './realtime.js';
import { customMapHealth, deleteCustomMap, getCustomMap, listCustomMaps, saveCustomMap } from './map-storage.js';
import { latestSnapshot, listAccounts, listCharacters, listSnapshots, saveSnapshot, snapshotById, storageHealth, storageMode } from './storage-provider.js';
import { summarizeSnapshot, validateSnapshot } from './validation.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const repoRoot = path.resolve(__dirname, '../..');
const publicDir = process.env.YDH_PUBLIC_DIR || repoRoot;
const port = Number(process.env.PORT || 3000);

const app = express();
const httpServer = http.createServer(app);
const realtime = attachRealtimeServer(httpServer);

app.use(express.json({ limit: '3mb' }));
app.use((req, res, next) => {
  res.setHeader('X-YDH-Server', 'chronicle-save-api');
  next();
});

function mapScopeFromRequest(req) {
  return {
    accountId: req.query.accountId || req.body?.accountId || req.body?.map?.accountId || '',
    characterId: req.query.characterId || req.body?.characterId || req.body?.map?.characterId || ''
  };
}

app.get('/api/health', async (req, res, next) => {
  try {
    const storage = await storageHealth();
    const customMaps = await customMapHealth();
    res.json({ ok: true, app: 'YDH Chronicle API', storageMode, realtime: realtime.stats(), customMaps, storage, now: new Date().toISOString() });
  } catch (error) {
    next(error);
  }
});

app.get('/api/realtime/stats', (req, res) => {
  res.json({ ok: true, realtime: realtime.stats() });
});

app.get('/api/maps/custom', async (req, res, next) => {
  try {
    const scope = mapScopeFromRequest(req);
    res.json({ ok: true, scope, maps: await listCustomMaps(scope) });
  } catch (error) {
    next(error);
  }
});

app.post('/api/maps/custom', async (req, res, next) => {
  try {
    const record = await saveCustomMap({ ...req.body, ...mapScopeFromRequest(req) });
    res.json({ ok: true, map: record });
  } catch (error) {
    next(error);
  }
});

app.get('/api/maps/custom/:id', async (req, res, next) => {
  try {
    const record = await getCustomMap(req.params.id, mapScopeFromRequest(req));
    if (!record) return res.status(404).json({ ok: false, error: 'Custom map not found' });
    res.json({ ok: true, map: record });
  } catch (error) {
    next(error);
  }
});

app.delete('/api/maps/custom/:id', async (req, res, next) => {
  try {
    const result = await deleteCustomMap(req.params.id, mapScopeFromRequest(req));
    if (!result.deleted) return res.status(404).json({ ok: false, error: 'Custom map not found' });
    res.json({ ok: true, ...result });
  } catch (error) {
    next(error);
  }
});

app.get('/api/accounts', async (req, res, next) => {
  try {
    res.json({ ok: true, storageMode, accounts: await listAccounts() });
  } catch (error) {
    next(error);
  }
});

app.get('/api/characters', async (req, res, next) => {
  try {
    res.json({ ok: true, storageMode, characters: await listCharacters(req.query.accountId || '') });
  } catch (error) {
    next(error);
  }
});

app.post('/api/save/snapshot', async (req, res, next) => {
  try {
    const validation = validateSnapshot(req.body);
    if (!validation.ok) {
      return res.status(400).json({ ok: false, errors: validation.errors });
    }
    const record = await saveSnapshot(req.body);
    res.json({
      ok: true,
      savedAt: record.receivedAt,
      id: record.id,
      storageMode,
      summary: summarizeSnapshot(req.body)
    });
  } catch (error) {
    next(error);
  }
});

app.post('/api/save/character', async (req, res, next) => {
  try {
    const snapshot = {
      schemaVersion: 1,
      app: 'YDH Chronicle',
      generatedAt: new Date().toISOString(),
      client: { source: 'character-endpoint' },
      account: null,
      selectedCharacter: null,
      characterSlots: [],
      saves: { character: req.body, map: null, chapterQuests: null, codexUnlocks: null, gmConsoleOpen: null }
    };
    const record = await saveSnapshot(snapshot);
    res.json({ ok: true, id: record.id, savedAt: record.receivedAt, storageMode });
  } catch (error) {
    next(error);
  }
});

app.get('/api/save/list', async (req, res, next) => {
  try {
    res.json({ ok: true, storageMode, saves: await listSnapshots() });
  } catch (error) {
    next(error);
  }
});

app.get('/api/save/restore', async (req, res, next) => {
  try {
    const latest = await latestSnapshot();
    if (!latest) return res.status(404).json({ ok: false, error: 'No save snapshot found' });
    res.json({ ok: true, storageMode, save: latest });
  } catch (error) {
    next(error);
  }
});

app.get('/api/save/:id', async (req, res, next) => {
  try {
    const record = await snapshotById(req.params.id);
    if (!record) return res.status(404).json({ ok: false, error: 'Save snapshot not found' });
    res.json({ ok: true, storageMode, save: record });
  } catch (error) {
    next(error);
  }
});

app.use(express.static(publicDir, { extensions: ['html'] }));

app.use((error, req, res, next) => {
  console.error(error);
  res.status(error.statusCode || 500).json({ ok: false, error: error.message || 'Internal server error' });
});

httpServer.listen(port, () => {
  console.log(`YDH Chronicle server listening on http://localhost:${port}`);
  console.log(`Serving static files from ${publicDir}`);
  console.log(`Storage provider: ${storageMode}`);
  console.log('WebSocket position path: /ws/position');
});
