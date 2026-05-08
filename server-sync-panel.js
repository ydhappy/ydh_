(() => {
  'use strict';

  const sync = () => window.YDH_SERVER_SYNC;
  const schema = window.YDH_SAVE_SCHEMA;
  if (!schema) return;

  function safeJson(value) {
    try {
      return JSON.stringify(value, null, 2);
    } catch {
      return String(value);
    }
  }

  function countKeys(value) {
    if (!value || typeof value !== 'object') return 0;
    if (Array.isArray(value)) return value.length;
    return Object.keys(value).length;
  }

  function render() {
    const root = document.getElementById('serverSyncRoot');
    if (!root || !sync()) return;

    const snapshot = sync().buildSnapshot();
    const status = sync().loadStatus();
    const character = snapshot.saves.character || {};
    const map = snapshot.saves.map || {};
    const quests = snapshot.saves.chapterQuests || {};
    const codex = snapshot.saves.codexUnlocks?.unlocked || {};

    root.innerHTML = `
      <div class="server-sync-head">
        <div>
          <p class="eyebrow">SERVER/API READY</p>
          <h2>저장 데이터 서버 연동 준비</h2>
          <p>현재 localStorage 저장 데이터를 서버 API로 보낼 수 있는 스냅샷 형태로 묶습니다. 실제 서버가 없으면 로컬 내보내기로 검증합니다.</p>
        </div>
        <div class="sync-status-badge">${status.mode || 'local-only'}<br />${status.lastSyncAt ? new Date(status.lastSyncAt).toLocaleTimeString('ko-KR') : '대기'}</div>
      </div>
      <div class="sync-grid">
        <div class="sync-card"><small>캐릭터</small><strong>Lv.${character.level || 1} · ${character.gold || 0}G</strong></div>
        <div class="sync-card"><small>맵 위치</small><strong>${map.mapIndex ?? 0} / X:${map.x ?? 0} Y:${map.y ?? 0}</strong></div>
        <div class="sync-card"><small>퀘스트 저장</small><strong>${countKeys(quests)}개</strong></div>
        <div class="sync-card"><small>도감 해금</small><strong>${countKeys(codex)}개</strong></div>
      </div>
      <div class="sync-actions">
        <button type="button" id="previewSnapshot">스냅샷 보기</button>
        <button type="button" id="exportSnapshot">JSON 내보내기</button>
        <button type="button" id="copySnapshot">클립보드 복사</button>
        <button type="button" id="testPushSnapshot">서버 전송 테스트</button>
      </div>
      <pre class="sync-output" id="syncOutput"></pre>
    `;

    document.getElementById('previewSnapshot')?.addEventListener('click', () => {
      document.getElementById('syncOutput').textContent = safeJson(sync().buildSnapshot());
    });

    document.getElementById('exportSnapshot')?.addEventListener('click', () => {
      const snapshot = sync().exportSnapshot();
      document.getElementById('syncOutput').textContent = `JSON 내보내기 완료\n${safeJson({ generatedAt: snapshot.generatedAt, schemaVersion: snapshot.schemaVersion })}`;
      renderSoon();
    });

    document.getElementById('copySnapshot')?.addEventListener('click', async () => {
      const text = safeJson(sync().buildSnapshot());
      try {
        await navigator.clipboard.writeText(text);
        document.getElementById('syncOutput').textContent = '스냅샷 클립보드 복사 완료';
      } catch (error) {
        document.getElementById('syncOutput').textContent = `클립보드 복사 실패: ${error.message}`;
      }
    });

    document.getElementById('testPushSnapshot')?.addEventListener('click', async () => {
      document.getElementById('syncOutput').textContent = '서버 전송 테스트 중... 실제 /api/save/snapshot 서버가 없으면 실패하는 것이 정상입니다.';
      const result = await sync().pushSnapshot();
      document.getElementById('syncOutput').textContent = safeJson(result.ok ? result.result : { error: result.error, note: '서버 미구현 상태면 정상적인 실패입니다.' });
      renderSoon();
    });
  }

  function renderSoon() {
    setTimeout(render, 80);
  }

  function create() {
    const section = document.createElement('section');
    section.className = 'panel server-sync-panel';
    section.id = 'server-sync';
    section.innerHTML = '<div id="serverSyncRoot"></div>';
    const roadmap = document.getElementById('roadmap');
    if (roadmap?.parentNode) roadmap.parentNode.insertBefore(section, roadmap);
    else document.querySelector('main')?.appendChild(section);
    render();
    window.addEventListener('ydh-server-sync-status', renderSoon);
    setInterval(render, 3000);
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', create);
  else create();
})();
