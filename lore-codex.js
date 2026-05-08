(() => {
  'use strict';

  const lore = window.YDH_LORE_CONTENT;
  if (!lore) return;

  const tabConfig = [
    { key: 'chapters', label: '챕터', icon: '📖' },
    { key: 'items', label: '아이템', icon: '🎒' },
    { key: 'maps', label: '맵', icon: '🗺️' },
    { key: 'npcs', label: 'NPC', icon: '💬' },
    { key: 'monsters', label: '몬스터', icon: '👹' },
    { key: 'skills', label: '스킬', icon: '✨' }
  ];

  function escapeHtml(value) {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function labelOf(key, item) {
    if (key === 'chapters') return item.mapId || 'chapter';
    if (key === 'items') return item.grade || item.type || 'item';
    if (key === 'maps') return item.role || 'map';
    if (key === 'npcs') return item.role || 'npc';
    if (key === 'monsters') return item.grade || 'monster';
    if (key === 'skills') return item.type || 'skill';
    return key;
  }

  function titleOf(key, item) {
    if (key === 'chapters') return item.title;
    return item.name || item.title || item.id;
  }

  function bodyOf(key, item) {
    return item.summary || item.story || item.dialogue || item.premise || '기록이 아직 해금되지 않았습니다.';
  }

  function metaOf(key, item) {
    const meta = [];
    if (item.atk) meta.push(`공격 +${item.atk}`);
    if (item.def) meta.push(`방어 +${item.def}`);
    if (item.hp) meta.push(`HP ${item.hp}`);
    if (item.mp) meta.push(`MP ${item.mp}`);
    if (item.cd) meta.push(`쿨 ${(item.cd / 1000).toFixed(1)}초`);
    if (item.scale) meta.push(`계수 x${item.scale}`);
    if (item.heal) meta.push(`회복 ${item.heal}`);
    if (item.buff) meta.push(`방어 ${item.buff}턴`);
    if (item.mapId) meta.push(item.mapId);
    if (item.id) meta.push(item.id);
    return meta;
  }

  function renderCards(key) {
    const list = Array.isArray(lore[key]) ? lore[key] : [];
    const grid = document.getElementById('loreGrid');
    if (!grid) return;
    if (!list.length) {
      grid.innerHTML = '<div class="lore-empty">표시할 기록이 없습니다.</div>';
      return;
    }

    grid.innerHTML = list.map((item) => {
      const meta = metaOf(key, item);
      return `
        <article class="lore-card">
          <small>${escapeHtml(labelOf(key, item))}</small>
          <h3>${escapeHtml(titleOf(key, item))}</h3>
          <p>${escapeHtml(bodyOf(key, item))}</p>
          ${meta.length ? `<div class="lore-meta">${meta.map((m) => `<span>${escapeHtml(m)}</span>`).join('')}</div>` : ''}
        </article>
      `;
    }).join('');
  }

  function setActiveTab(key) {
    document.querySelectorAll('.lore-tab').forEach((button) => {
      button.classList.toggle('active', button.dataset.key === key);
    });
    renderCards(key);
  }

  function createCodex() {
    const section = document.createElement('section');
    section.className = 'panel lore-codex';
    section.id = 'lore';
    section.innerHTML = `
      <div class="lore-codex-head">
        <div>
          <p class="eyebrow">LORE CODEX</p>
          <h2>${escapeHtml(lore.title || 'YDH Chronicle Codex')}</h2>
          <p>${escapeHtml(lore.subtitle || '세계관 기록')}</p>
        </div>
        <div class="lore-codex-badge">소설 콘텐츠<br />${tabConfig.length}개 분류</div>
      </div>
      <p class="lore-premise">${escapeHtml(lore.premise || '')}</p>
      <div class="lore-tabs" role="tablist" aria-label="소설 도감 분류">
        ${tabConfig.map((tab) => `<button class="lore-tab" type="button" data-key="${tab.key}">${tab.icon} ${tab.label}</button>`).join('')}
      </div>
      <div class="lore-grid" id="loreGrid" aria-live="polite"></div>
    `;

    const ranking = document.getElementById('ranking');
    if (ranking?.parentNode) ranking.parentNode.insertBefore(section, ranking);
    else document.querySelector('main')?.appendChild(section);

    section.querySelectorAll('.lore-tab').forEach((button) => {
      button.addEventListener('click', () => setActiveTab(button.dataset.key));
    });
    setActiveTab('chapters');
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', createCodex);
  else createCodex();
})();
