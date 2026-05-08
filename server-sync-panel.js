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
          <h2>저장 데이터 서버 연동</h2>
          <p>localStorage 저장 데이터를 서버로 보내고, 서버의 최신 저장 데이터를 다시 브라우저로 복원합니다.</p>
        </div>
        <div class="sync-status-badge">${status.mode || 'local-only'}<br />${status.lastRestoreAt ? `복원 ${new Date(status.lastRestoreAt).toLocaleTimeString('ko-KR')}` : status.lastSyncAt ? `저장 ${new Date(status.lastSyncAt).toLocaleTimeString('ko-KR')}` : '대기'}</div>
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
        <button type="button" id="testPushSnapshot">서버 저장</button>
        <button type="button" id="listServerSaves">서버 저장목록</button>
        <button type="button" id="restoreLatestSave">최신 저장 복원</button>
        <button type="button" id="restoreLatestAndReload">복원 후 새로고침</button>
      </div>
      <pre class="sync-output" id="syncOutput"></pre>
    `;

    const output = document.getElementById('syncOutput');

    document.getElementById('previewSnapshot')?.addEventListener('click', () => {
      output.textContent = safeJson(sync().buildSnapshot());
    });

    document.getElementById('exportSnapshot')?.addEventListener('click', () => {
      const exported = sync().exportSnapshot();
      output.textContent = `JSON 내보내기 완료\n${safeJson({ generatedAt: exported.generatedAt, schemaVersion: exported.schemaVersion })}`;
      renderSoon();
    });

    document.getElementById('copySnapshot')?.addEventListener('click', async () => {
      const text = safeJson(sync().buildSnapshot());
      try {
        await navigator.clipboard.writeText(text);
        output.textContent = '스냅샷 클립보드 복사 완료';
      } catch (error) {
        output.textContent = `클립보드 복사 실패: ${error.message}`;
      }
    });

    document.getElementById('testPushSnapshot')?.addEventListener('click', async () => {
      output.textContent = '서버 저장 중... Node 서버가 실행 중이어야 합니다.';
      const result = await sync().pushSnapshot();
      output.textContent = safeJson(result.ok ? result.result : { error: result.error, note: '서버가 꺼져 있으면 실패합니다.' });
      renderSoon();
    });

    document.getElementById('listServerSaves')?.addEventListener('click', async () => {
      output.textContent = '서버 저장 목록 조회 중...';
      const result = await sync().listServerSaves();
      output.textContent = safeJson(result.ok ? result.saves : { error: result.error, note: '서버가 꺼져 있으면 실패합니다.' });
      renderSoon();
    });

    document.getElementById('restoreLatestSave')?.addEventListener('click', async () => {
      output.textContent = '최신 저장 복원 중...';
      const result = await sync().restoreLatestSnapshot({ reload: false });
      output.textContent = safeJson(result.ok ? { restoredId: result.record.id, receivedAt: result.record.receivedAt, note: '복원 완료. 새로고침하면 모든 화면이 저장 상태로 다시 렌더링됩니다.' } : { error: result.error });
      renderSoon();
    });

    document.getElementById('restoreLatestAndReload')?.addEventListener('click', async () => {
      output.textContent = '최신 저장 복원 후 새로고침 중...';
      const result = await sync().restoreLatestSnapshot({ reload: true });
      if (!result.ok) output.textContent = safeJson({ error: result.error });
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
    window.addEventListener('ydh-server-restore-applied', renderSoon);
    setInterval(render, 3000);
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', create);
  else create();
})();
