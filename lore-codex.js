(() => {
  'use strict';

  const lore = window.YDH_LORE_CONTENT;
  if (!lore) return;

  const unlocks = () => window.YDH_CODEX_UNLOCKS;

  const tabConfig = [
    { key: 'chapters', label: '챕터', icon: '📖', hint: '관련 지역 방문 시 해금' },
    { key: 'items', label: '아이템', icon: '🎒', hint: '아이템 획득 시 해금' },
    { key: 'maps', label: '맵', icon: '🗺️', hint: '포탈/이동으로 지역 방문 시 해금' },
    { key: 'npcs', label: 'NPC', icon: '💬', hint: 'NPC와 대화 시 해금' },
    { key: 'monsters', label: '몬스터', icon: '👹', hint: '조우 또는 처치 시 해금' },
    { key: 'skills', label: '스킬', icon: '✨', hint: '스킬 사용 시 해금' }
  ];

  let activeKey = 'chapters';

  function escapeHtml(value) {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function itemId(item) {
    return item?.id || item?.name || item?.title;
  }

  function isUnlocked(key, item) {
    const service = unlocks();
    if (!service) return true;
    return service.isUnlocked(key, item);
  }

  function labelOf(key, item, unlocked) {
    if (!unlocked) return 'locked';
    if (key === 'chapters') return item.mapId || 'chapter';
    if (key === 'items') return item.grade || item.type || 'item';
    if (key === 'maps') return item.role || 'map';
    if (key === 'npcs') return item.role || 'npc';
    if (key === 'monsters') return item.grade || 'monster';
    if (key === 'skills') return item.type || 'skill';
    return key;
  }

  function titleOf(key, item, unlocked) {
    if (!unlocked) return '미확인 기록';
    if (key === 'chapters') return item.title;
    return item.name || item.title || item.id;
  }

  function bodyOf(key, item, unlocked) {
    if (!unlocked) return tabConfig.find((tab) => tab.key === key)?.hint || '플레이를 통해 기록을 해금하세요.';
    return item.summary || item.story || item.dialogue || item.premise || '기록이 아직 해금되지 않았습니다.';
  }

  function metaOf(key, item, unlocked) {
    const meta = [];
    if (!unlocked) {
      meta.push(`단서: ${itemId(item)}`);
      return meta;
    }
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

  function renderProgress() {
    const host = document.getElementById('loreUnlockProgress');
    if (!host) return;
    const service = unlocks();
    host.innerHTML = tabConfig.map((tab) => {
      const count = service?.counts(tab.key) || { opened: lore[tab.key]?.length || 0, total: lore[tab.key]?.length || 0 };
      return `<span>${tab.icon} ${tab.label} ${count.opened}/${count.total}</span>`;
    }).join('') + '<button class="btn small lore-reset-unlocks" type="button" id="resetCodexUnlocks">해금 초기화</button>';
    document.getElementById('resetCodexUnlocks')?.addEventListener('click', () => {
      service?.reset();
      renderProgress();
      renderCards(activeKey);
    });
  }

  function renderCards(key) {
    activeKey = key;
    renderProgress();
    const list = Array.isArray(lore[key]) ? lore[key] : [];
    const grid = document.getElementById('loreGrid');
    if (!grid) return;
    if (!list.length) {
      grid.innerHTML = '<div class="lore-empty">표시할 기록이 없습니다.</div>';
      return;
    }

    grid.innerHTML = list.map((item) => {
      const unlocked = isUnlocked(key, item);
      const meta = metaOf(key, item, unlocked);
      return `
        <article class="lore-card ${unlocked ? 'unlocked' : 'locked'}" data-codex-key="${escapeHtml(key)}:${escapeHtml(itemId(item))}">
          <small>${escapeHtml(labelOf(key, item, unlocked))}</small>
          <h3>${escapeHtml(titleOf(key, item, unlocked))}</h3>
          <p>${escapeHtml(bodyOf(key, item, unlocked))}</p>
          ${meta.length ? `<div class="lore-meta">${meta.map((m) => `<span>${escapeHtml(m)}</span>`).join('')}</div>` : ''}
          ${unlocked ? '' : `<div class="unlock-hint">${escapeHtml(tabConfig.find((tab) => tab.key === key)?.hint || '조건 달성 시 해금')}</div>`}
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
        <div class="lore-codex-badge">소설 콘텐츠<br />해금형 도감</div>
      </div>
      <p class="lore-premise">${escapeHtml(lore.premise || '')}</p>
      <div class="lore-unlock-progress" id="loreUnlockProgress"></div>
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
    window.addEventListener('ydh-codex-updated', () => {
      renderProgress();
      renderCards(activeKey);
    });
    setActiveTab('chapters');
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', createCodex);
  else createCodex();
})();
