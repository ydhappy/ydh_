(() => {
  'use strict';

  const catalog = window.YDH_ICON_CATALOG;
  if (!catalog) return;

  const atlasUrl = catalog.atlas?.image || 'assets/icons/dark-fantasy-icon-atlas.svg';
  const columns = catalog.atlas?.columns || 6;
  const rows = catalog.atlas?.rows || 6;
  const itemByName = new Map((catalog.items || []).map((item) => [item.nameKo, item]));
  const skillByName = new Map((catalog.skills || []).map((skill) => [skill.nameKo, skill]));
  const skillAliases = new Map([
    ['파워 스트라이크', '십자 베기'],
    ['파이어 볼트', '혈염 폭발'],
    ['힐', '성역의 빛'],
    ['아이언 스킨', '황금 수호'],
    ['라이트닝 스톰', '공허성']
  ]);
  const itemKeywordAliases = [
    ['검', '붉은 장검'],
    ['장검', '붉은 장검'],
    ['단검', '푸른 단검'],
    ['갑옷', '청은 방패'],
    ['방패', '청은 방패'],
    ['망토', '성스러운 문장'],
    ['반지', '그림자 보석'],
    ['물약', '회복 물약'],
    ['주문서', '마나 주문서'],
    ['보급상자', '황금 상자'],
    ['상자', '황금 상자'],
    ['문장', '성스러운 문장'],
    ['메달', '축복 메달'],
    ['수정', '얼음 수정'],
    ['화염', '화염의 눈물']
  ];

  function injectStyles() {
    if (document.getElementById('iconRuntimeBindingStyles')) return;
    const style = document.createElement('style');
    style.id = 'iconRuntimeBindingStyles';
    style.textContent = `
      .atlas-icon{display:inline-block;width:1.75em;height:1.75em;min-width:1.75em;border-radius:.45em;background-image:var(--icon-atlas);background-size:${columns * 100}% ${rows * 100}%;background-position:var(--icon-pos);vertical-align:middle;box-shadow:0 .35em .9em rgba(0,0,0,.25);border:1px solid rgba(247,200,95,.22)}
      .skill-card .skill-icon.atlas-bound{font-size:0;display:inline-flex;align-items:center;justify-content:center}.skill-card .skill-icon.atlas-bound .atlas-icon{width:42px;height:42px;min-width:42px;border-radius:13px}.inventory li.icon-bound{display:flex;align-items:center;gap:8px}.inventory li.icon-bound .atlas-icon{width:28px;height:28px;min-width:28px}.icon-bound-text{min-width:0;overflow:hidden;text-overflow:ellipsis}
    `;
    document.head.appendChild(style);
  }

  function bgPos(entry) {
    const colMax = Math.max(1, columns - 1);
    const rowMax = Math.max(1, rows - 1);
    return `${(entry.x / colMax) * 100}% ${(entry.y / rowMax) * 100}%`;
  }

  function iconNode(entry) {
    const icon = document.createElement('span');
    icon.className = 'atlas-icon';
    icon.style.setProperty('--icon-atlas', `url('${atlasUrl}')`);
    icon.style.setProperty('--icon-pos', bgPos(entry));
    icon.title = entry.nameKo || entry.id;
    return icon;
  }

  function bindSkillCards() {
    document.querySelectorAll('.skill-card').forEach((card) => {
      const name = card.querySelector('.skill-name')?.textContent?.trim() || '';
      const iconSlot = card.querySelector('.skill-icon');
      if (!name || !iconSlot || iconSlot.dataset.atlasBound === name) return;
      const mappedName = skillAliases.get(name) || name;
      const entry = skillByName.get(mappedName) || (catalog.skills || []).find((skill) => name.includes(skill.nameKo) || skill.nameKo.includes(name));
      if (!entry) return;
      iconSlot.textContent = '';
      iconSlot.appendChild(iconNode(entry));
      iconSlot.classList.add('atlas-bound');
      iconSlot.dataset.atlasBound = name;
      card.dataset.iconId = entry.id;
    });
  }

  function findItemEntry(text) {
    if (itemByName.has(text)) return itemByName.get(text);
    for (const [name, entry] of itemByName.entries()) {
      if (text.includes(name) || name.includes(text)) return entry;
    }
    const alias = itemKeywordAliases.find(([keyword]) => text.includes(keyword));
    return alias ? itemByName.get(alias[1]) : null;
  }

  function bindInventory() {
    document.querySelectorAll('#inventoryList li').forEach((li) => {
      const raw = li.textContent?.trim() || '';
      if (!raw || li.dataset.atlasBound === raw) return;
      const entry = findItemEntry(raw);
      if (!entry) return;
      li.innerHTML = '';
      li.appendChild(iconNode(entry));
      const text = document.createElement('span');
      text.className = 'icon-bound-text';
      text.textContent = raw;
      li.appendChild(text);
      li.classList.add('icon-bound');
      li.dataset.atlasBound = raw;
      li.dataset.iconId = entry.id;
    });
  }

  function bindAll() {
    injectStyles();
    bindSkillCards();
    bindInventory();
  }

  function observe() {
    const targets = ['skillGrid', 'inventoryList'].map((id) => document.getElementById(id)).filter(Boolean);
    targets.forEach((target) => {
      const observer = new MutationObserver(() => bindAll());
      observer.observe(target, { childList: true, subtree: true, characterData: true });
    });
  }

  function boot() {
    bindAll();
    observe();
    setInterval(bindAll, 800);
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();

  window.YDH_ICON_RUNTIME_BINDINGS = { bindAll };
})();
