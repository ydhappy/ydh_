(() => {
  'use strict';

  const sync = () => window.YDH_SERVER_SYNC;
  const schema = window.YDH_SAVE_SCHEMA;
  if (!schema) return;

  let cachedServerSaves = [];

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

  function escapeHtml(value) {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
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
          <p>localStorage 저장 데이터를 서버로 보내고, 서버 저장 목록에서 원하는 슬롯을 선택해 복원합니다.</p>
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
        <button type="button" id="restoreLatestAndReload">최신 복원+새로고침</button>
      </div>
      <div class="sync-save-list" id="syncSaveList">${renderSaveCards(cachedServerSaves)}</div>
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
      await refreshServerSaves(false);
      renderSoon();
    });

    document.getElementById('listServerSaves')?.addEventListener('click', async () => {
      output.textContent = '서버 저장 목록 조회 중...';
      const result = await refreshServerSaves(false);
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

    document.querySelectorAll('[data-restore-save-id]').forEach((button) => {
      button.addEventListener('click', async () => {
        output.textContent = `선택 저장 복원 중: ${button.dataset.restoreSaveId}`;
        const result = await sync().restoreSnapshotById(button.dataset.restoreSaveId, { reload: false });
        output.textContent = safeJson(result.ok ? { restoredId: result.record.id, receivedAt: result.record.receivedAt, characterName: result.record.characterName, note: '선택 저장 복원 완료. 필요하면 새로고침하세요.' } : { error: result.error });
        renderSoon();
      });
    });

    document.querySelectorAll('[data-restore-reload-save-id]').forEach((button) => {
      button.addEventListener('click', async () => {
        output.textContent = `선택 저장 복원 후 새로고침 중: ${button.dataset.restoreReloadSaveId}`;
        const result = await sync().restoreSnapshotById(button.dataset.restoreReloadSaveId, { reload: true });
        if (!result.ok) output.textContent = safeJson({ error: result.error });
      });
    });
  }

  function renderSaveCards(saves) {
    if (!saves.length) return '<div class="sync-empty-list">서버 저장목록을 누르면 저장 슬롯이 표시됩니다.</div>';
    return saves.map((save) => `
      <article class="sync-save-card">
        <div>
          <strong>${escapeHtml(save.characterName || '검은 기사')}</strong>
          <small>${escapeHtml(save.accountName || 'YDH Player')} · ${escapeHtml(save.classId || 'knight')} · Lv.${save.level || 1}</small>
          <small>${escapeHtml(save.receivedAt || '')} · map ${save.mapIndex ?? 0}</small>
        </div>
        <div class="sync-save-actions">
          <button type="button" data-restore-save-id="${escapeHtml(save.id)}">복원</button>
          <button type="button" data-restore-reload-save-id="${escapeHtml(save.id)}">복원+새로고침</button>
        </div>
      </article>
    `).join('');
  }

  async function refreshServerSaves(updateOutput = true) {
    const result = await sync().listServerSaves();
    if (result.ok) cachedServerSaves = result.saves || [];
    if (updateOutput) {
      const output = document.getElementById('syncOutput');
      if (output) output.textContent = safeJson(result.ok ? result.saves : { error: result.error });
    }
    return result;
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
