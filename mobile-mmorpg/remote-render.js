(() => {
  'use strict';

  const saveKey = 'ydh-mobile-mmorpg-v1';
  const tile = 48;
  const mapW = 32;
  const mapH = 24;
  const classColor = {
    knight: '#f7c85f',
    wizard: '#73a7ff',
    ranger: '#69dda0',
    rogue: '#ad82ff'
  };
  const classMark = {
    knight: 'K',
    wizard: 'W',
    ranger: 'E',
    rogue: 'R'
  };

  const layer = document.createElement('canvas');
  layer.id = 'remotePlayersLayer';
  layer.style.cssText = 'position:absolute;inset:0;width:100%;height:100%;pointer-events:none;z-index:3;';
  document.getElementById('app')?.appendChild(layer);
  const ctx = layer.getContext('2d');

  function resize() {
    const dpr = Math.max(1, Math.min(2, window.devicePixelRatio || 1));
    layer.width = Math.floor(innerWidth * dpr);
    layer.height = Math.floor(innerHeight * dpr);
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  }

  function readLocal() {
    try { return JSON.parse(localStorage.getItem(saveKey) || '{}'); }
    catch { return {}; }
  }

  function cameraFor(local) {
    let x = Number(local.x || tile * 14) - innerWidth / 2;
    let y = Number(local.y || tile * 14) - innerHeight / 2;
    x = Math.max(0, Math.min(x, mapW * tile - innerWidth));
    y = Math.max(0, Math.min(y, mapH * tile - innerHeight));
    return { x, y };
  }

  function drawRemote(peer, camera) {
    const px = Number(peer.x || 0) - camera.x;
    const py = Number(peer.y || 0) - camera.y;
    if (px < -80 || py < -80 || px > innerWidth + 80 || py > innerHeight + 80) return;
    const color = classColor[peer.classId] || '#eef4ff';
    const mark = classMark[peer.classId] || 'P';
    const hp = Math.max(0, Math.min(1, Number(peer.hp || peer.maxHp || 1) / Math.max(1, Number(peer.maxHp || 1))));

    ctx.fillStyle = 'rgba(0,0,0,.35)';
    ctx.beginPath();
    ctx.ellipse(px, py + 22, 24, 7, 0, 0, Math.PI * 2);
    ctx.fill();

    ctx.strokeStyle = 'rgba(255,255,255,.7)';
    ctx.lineWidth = 3;
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(px, py, 20, 0, Math.PI * 2);
    ctx.fill();
    ctx.stroke();

    ctx.fillStyle = '#07101a';
    ctx.font = 'bold 13px system-ui';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(mark, px, py);

    ctx.fillStyle = 'rgba(0,0,0,.62)';
    ctx.fillRect(px - 26, py - 38, 52, 6);
    ctx.fillStyle = '#69dda0';
    ctx.fillRect(px - 26, py - 38, 52 * hp, 6);

    ctx.font = 'bold 11px system-ui';
    ctx.fillStyle = '#eef4ff';
    ctx.textBaseline = 'bottom';
    ctx.fillText(`${peer.name || 'player'} Lv.${peer.lv || 1}`, px, py - 42);
  }

  function draw() {
    ctx.clearRect(0, 0, innerWidth, innerHeight);
    const net = window.YDH_MMO_NET;
    if (!net || !net.peers) {
      requestAnimationFrame(draw);
      return;
    }
    const camera = cameraFor(readLocal());
    for (const peer of net.peers.values()) drawRemote(peer, camera);
    requestAnimationFrame(draw);
  }

  addEventListener('resize', resize);
  resize();
  requestAnimationFrame(draw);
})();
