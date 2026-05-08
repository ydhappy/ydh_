(() => {
  'use strict';

  const gradeMeta = window.YDH_ITEM_GRADE_META || {};
  const slots = window.YDH_EQUIPMENT_SLOTS || [];
  const catalog = typeof window.YDH_buildItemCatalog === 'function' ? window.YDH_buildItemCatalog() : [];
  let filter = 'all';

  function normalize(value) {
    return String(value || '').replace(/\[[^\]]+\]/g, '').replace(/공격\+\d+/g, '').replace(/방어\+\d+/g, '').replace(/x\d+/g, '').trim();
  }

  function findItem(raw) {
    const clean = normalize(raw);
    const matched = catalog.find((item) => clean.includes(item.name) || item.name.includes(clean));
    if (matched) return { ...matched, raw };
    return inferItem(raw, clean);
  }

  function inferItem(raw, clean) {
    const gradeMatch = String(raw).match(/\[(.+?)\]/);
    const atkMatch = String(raw).match(/공격\+(\d+)/);
    const defMatch = String(raw).match(/방어\+(\d+)/);
    const name = clean || raw;
    let slot = 'artifact';
    let type = 'material';
    if (/검|장검|달검|소드/.test(name)) { slot = 'weapon'; type = 'weapon'; }
    else if (/갑옷|망토|아머/.test(name)) { slot = 'armor'; type = 'armor'; }
    else if (/반지|인장|링/.test(name)) { slot = 'accessory'; type = 'accessory'; }
    else if (/물약/.test(name)) { slot = 'artifact'; type = 'consumable'; }
    return {
      id: name,
      name,
      raw,
      slot,
      type,
      grade: gradeMatch?.[1] || 'normal',
      atk: Number(atkMatch?.[1] || 0),
      def: Number(defMatch?.[1] || 0)
    };
  }

  function iconFor(item) {
    if (item.slot === 'weapon') return '⚔️';
    if (item.slot === 'armor') return '🛡️';
    if (item.slot === 'accessory') return '💍';
    if (item.type === 'consumable') return '🧪';
    if (item.grade === 'quest') return '📜';
    return '🔮';
  }

  function gradeLabel(grade) {
    return gradeMeta[grade]?.label || grade || '일반';
  }

  function score(item) {
    const gradeWeight = gradeMeta[item.grade]?.weight || 1;
    return gradeWeight * 100 + (item.atk || 0) * 10 + (item.def || 0) * 8;
  }

  function parseInventory() {
    const list = document.getElementById('inventoryList');
    if (!list) return [];
    return [...list.querySelectorAll('li')].map((li) => findItem(li.textContent));
  }

  function bestBySlot(items, slotId) {
    return items.filter((item) => item.slot === slotId).sort((a, b) => score(b) - score(a))[0];
  }

  function renderEquipment(items) {
    const host = document.getElementById('equipmentSlots');
    if (!host) return;
    host.innerHTML = slots.map((slot) => {
      const item = bestBySlot(items, slot.id);
      return `
        <div class="equipment-slot">
          <strong>${slot.icon} ${slot.label}</strong>
          <span>${item ? `${item.name} · ${gradeLabel(item.grade)} · ATK+${item.atk || 0} DEF+${item.def || 0}` : '장착 후보 없음'}</span>
        </div>
      `;
    }).join('');
  }

  function renderInventoryItems(items) {
    const list = document.getElementById('inventoryList');
    if (!list) return;
    const filtered = filter === 'all' ? items : items.filter((item) => item.slot === filter || item.grade === filter || item.type === filter);
    list.innerHTML = filtered.slice(-12).map((item) => `
      <li class="grade-${item.grade || 'normal'}" data-icon="${iconFor(item)}">
        <span>
          <span class="item-name">${item.name}</span>
          <span class="item-meta">${gradeLabel(item.grade)} · ${item.type || item.slot} · ATK+${item.atk || 0} DEF+${item.def || 0}</span>
        </span>
      </li>
    `).join('');
  }

  function enhance() {
    const inventory = document.querySelector('.inventory');
    const list = document.getElementById('inventoryList');
    if (!inventory || !list) return;
    inventory.classList.add('enhanced');
    if (!document.getElementById('equipmentPanel')) {
      const panel = document.createElement('div');
      panel.className = 'equipment-panel';
      panel.id = 'equipmentPanel';
      panel.innerHTML = '<h3>장착 슬롯</h3><div class="equipment-slots" id="equipmentSlots"></div>';
      inventory.parentNode.insertBefore(panel, inventory);
    }
    if (!document.getElementById('inventoryTools')) {
      const tools = document.createElement('div');
      tools.className = 'inventory-tools';
      tools.id = 'inventoryTools';
      tools.innerHTML = `
        <button type="button" data-filter="all" class="active">전체</button>
        <button type="button" data-filter="weapon">무기</button>
        <button type="button" data-filter="armor">방어구</button>
        <button type="button" data-filter="accessory">장신구</button>
        <button type="button" data-filter="quest">퀘스트</button>
      `;
      inventory.insertBefore(tools, list);
      tools.querySelectorAll('button').forEach((button) => {
        button.addEventListener('click', () => {
          filter = button.dataset.filter;
          tools.querySelectorAll('button').forEach((btn) => btn.classList.toggle('active', btn === button));
          refresh();
        });
      });
    }
    refresh();
  }

  function refresh() {
    const items = parseInventory();
    renderEquipment(items);
    renderInventoryItems(items);
  }

  function boot() {
    enhance();
    const list = document.getElementById('inventoryList');
    if (list && window.MutationObserver) {
      const observer = new MutationObserver(() => {
        if (list.dataset.rendering === '1') return;
        list.dataset.rendering = '1';
        setTimeout(() => {
          refresh();
          list.dataset.rendering = '0';
        }, 30);
      });
      observer.observe(list, { childList: true, subtree: true });
    }
    setInterval(refresh, 1600);
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
