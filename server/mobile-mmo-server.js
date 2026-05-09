import { createServer } from 'node:http';
import { createReadStream, existsSync, statSync } from 'node:fs';
import { extname, join, normalize, resolve } from 'node:path';
import { attachMobileMmoGateway } from './mobile-mmo-gateway.js';

const root = resolve(process.cwd());
const port = Number(process.env.PORT || 8787);

const mime = new Map([
  ['.html', 'text/html; charset=utf-8'],
  ['.css', 'text/css; charset=utf-8'],
  ['.js', 'text/javascript; charset=utf-8'],
  ['.json', 'application/json; charset=utf-8'],
  ['.svg', 'image/svg+xml'],
  ['.png', 'image/png'],
  ['.webp', 'image/webp'],
  ['.jpg', 'image/jpeg'],
  ['.jpeg', 'image/jpeg'],
  ['.txt', 'text/plain; charset=utf-8']
]);

function safePath(urlPath) {
  const clean = decodeURIComponent(urlPath.split('?')[0] || '/');
  const requested = clean === '/' ? '/mobile-mmorpg/index-online.html' : clean;
  const file = normalize(join(root, requested));
  if (!file.startsWith(root)) return null;
  return file;
}

function sendFile(req, res) {
  const file = safePath(req.url || '/');
  if (!file || !existsSync(file) || !statSync(file).isFile()) {
    res.writeHead(404, { 'content-type': 'text/plain; charset=utf-8' });
    res.end('404 Not Found');
    return;
  }
  res.writeHead(200, {
    'content-type': mime.get(extname(file)) || 'application/octet-stream',
    'cache-control': 'no-cache'
  });
  createReadStream(file).pipe(res);
}

const server = createServer(sendFile);
const gateway = attachMobileMmoGateway(server, { path: '/mmo' });

server.listen(port, '0.0.0.0', () => {
  console.log(`YDH Chronicle mobile MMORPG server running`);
  console.log(`Local: http://127.0.0.1:${port}/`);
  console.log(`WebSocket: ws://127.0.0.1:${port}/mmo`);
});

process.on('SIGINT', () => {
  for (const client of gateway.clients.values()) client.ws.close();
  server.close(() => process.exit(0));
});
