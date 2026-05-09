(() => {
  'use strict';

  const catalog = window.YDH_ICON_CATALOG;
  if (!catalog) return;

  function injectStyles() {
    if (document.getElementById('iconItemSkillGalleryStyles')) return;
    const style = document.createElement('style');
    style.id = 'iconItemSkillGalleryStyles';
    style.textContent = `
      .icon-skill-gallery{margin:18px 0;padding:22px;border-radius:24px;background:rgba(0,0,0,.22);border:1px solid rgba(115,167,255,.22);overflow:hidden}.icon-skill-gallery h2{margin:0 0 8px;color:#eef4ff}.icon-skill-gallery p{margin:0;color:var(--muted);line-height:1.6}.icon-skill-group{margin-top:18px}.icon-skill-group h3{margin:0 0 10px;color:#f7c85f}.icon-grid{display:grid;grid-template-columns:repeat(6,minmax(0,1fr));gap:10px}.icon-card{border:1px solid rgba(255,255,255,.1);border-radius:16px;background:rgba(255,255,255,.045);padding:10px;min-width:0}.icon-sprite{width:64px;height:64px;border-radius:14px;background-image:var(--atlas);background-size:685.714% 685.714%;background-position:var(--pos);margin:0 auto 8px;border:1px solid rgba(247,200,95,.18);box-shadow:0 10px 24px rgba(0,0,0,.24)}.icon-card b{display:block;text-align:center;color:#eef4ff;font-size:.82rem;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.icon-card span{display:block;text-align:center;color:var(--muted);font-size:.72rem;margin-top:3px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.rarity-common{border-color:rgba(170,181,199,.22)}.rarity-uncommon{border-color:rgba(105,221,160,.3)}.rarity-rare{border-color:rgba(115,167,255,.36)}.rarity-epic{border-color:rgba(173,130,255,.42)}@media(max-width:1100px){.icon-grid{grid-template-columns:repeat(4,minmax(0,1fr))}}@media(max-width:640px){.icon-skill-gallery{padding:16px}.icon-grid{grid-template-columns:repeat(3,minmax(0,1fr))}.icon-sprite{width:56px;height:56px}}
    `;
    document.head.appendChild(style);
  }

  function bgPos(x, y) {
    const colMax = Math.max(1, (catalog.atlas.columns || 6) - 1);
    const rowMax = Math.max(1, (catalog.atlas.rows || 6) - 1);
    return `${(x / colMax) * 100}% ${(y / rowMax) * 100}%`;
  }

  function card(item, mode) {
    return `
      <div class="icon-card rarity-${escapeHtml(item.rarity || 'common')}">
        <div class="icon-sprite" style="--atlas:url('${escapeAttr(catalog.atlas.image)}');--pos:${bgPos(item.x, item.y)}"></div>
        <b>${escapeHtml(item.nameKo || item.id)}</b>
        <span>${escapeHtml(mode === 'skill' ? `${item.classId} · ${item.type}` : `${item.type} · ${item.rarity}`)}</span>
      </div>
    `;
  }

  function render() {
    const assetsSection = document.getElementById('generatedAssetGallery') || document.getElementById('assets');
    const main = document.querySelector('main');
    if (!main || document.getElementById('iconItemSkillGallery')) return;
    injectStyles();
    const section = document.createElement('section');
    section.id = 'iconItemSkillGallery';
    section.className = 'icon-skill-gallery panel';
    section.innerHTML = `
      <p class="eyebrow">ITEM & SKILL ICONS</p>
      <h2>아이템/스킬 아이콘 세트</h2>
      <p>다크 판타지 모바일 MMORPG용 SVG atlas 기반 아이콘입니다. 아이템 ${catalog.items.length}종, 스킬 ${catalog.skills.length}종을 catalog 데이터로 등록했습니다.</p>
      <div class="icon-skill-group"><h3>아이템</h3><div class="icon-grid">${catalog.items.map((item) => card(item, 'item')).join('')}</div></div>
      <div class="icon-skill-group"><h3>스킬</h3><div class="icon-grid">${catalog.skills.map((item) => card(item, 'skill')).join('')}</div></div>
    `;
    if (assetsSection?.parentNode) assetsSection.parentNode.insertBefore(section, assetsSection.nextSibling);
    else main.appendChild(section);
  }

  function escapeHtml(value) {
    return String(value ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\"/g, '&quot;').replace(/'/g, '&#039;');
  }

  function escapeAttr(value) {
    return String(value ?? '').replace(/'/g, '%27').replace(/\)/g, '%29');
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', render);
  else render();
})();
