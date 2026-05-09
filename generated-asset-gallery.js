(() => {
  'use strict';

  const resources = window.YDH_RESOURCES || {};
  const details = window.YDH_GENERATED_ASSET_DETAILS || {};
  const concepts = resources.groups?.generatedConcepts || [];

  function loadScriptOnce(src, id) {
    if (document.getElementById(id)) return Promise.resolve();
    return new Promise((resolve) => {
      const script = document.createElement('script');
      script.id = id;
      script.src = src;
      script.onload = resolve;
      script.onerror = resolve;
      document.body.appendChild(script);
    });
  }

  async function loadIconGallery() {
    await loadScriptOnce('data/icon-item-skill-catalog.js', 'iconItemSkillCatalogScript');
    await loadScriptOnce('icon-item-skill-gallery.js', 'iconItemSkillGalleryScript');
    await loadScriptOnce('icon-runtime-bindings.js', 'iconRuntimeBindingsScript');
  }

  if (!concepts.length) {
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', loadIconGallery);
    else loadIconGallery();
    return;
  }

  function injectStyles() {
    if (document.getElementById('generatedAssetGalleryStyles')) return;
    const style = document.createElement('style');
    style.id = 'generatedAssetGalleryStyles';
    style.textContent = `
      .generated-asset-gallery{margin:18px 0;padding:22px;border-radius:24px;background:rgba(0,0,0,.22);border:1px solid rgba(247,200,95,.2);overflow:hidden}.generated-asset-gallery h2{margin:0 0 8px;color:#eef4ff}.generated-asset-gallery p{margin:0;color:var(--muted);line-height:1.6}.generated-asset-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:14px;margin-top:18px}.generated-asset-card{position:relative;border:1px solid var(--line);border-radius:18px;overflow:hidden;background:rgba(255,255,255,.04);text-decoration:none;color:#eef4ff;transition:transform .18s ease,border-color .18s ease,box-shadow .18s ease}.generated-asset-card:hover{transform:translateY(-3px);border-color:rgba(247,200,95,.55);box-shadow:0 18px 45px rgba(0,0,0,.28)}.generated-asset-card img{display:block;width:100%;aspect-ratio:4/3;object-fit:cover;background:#070b12}.generated-asset-card div{padding:12px}.generated-asset-card strong{display:block;margin-bottom:5px;font-size:.98rem}.generated-asset-card span{display:block;color:var(--muted);font-size:.78rem;line-height:1.45}.generated-asset-tag{position:absolute;left:10px;top:10px;padding:5px 8px;border-radius:999px;background:rgba(0,0,0,.62);border:1px solid rgba(247,200,95,.32);color:#f7c85f;font-size:.68rem;font-weight:900;text-transform:uppercase}.generated-detail-panel{margin-top:18px;padding:16px;border:1px solid rgba(255,255,255,.1);border-radius:18px;background:rgba(0,0,0,.18)}.generated-detail-panel h3{margin:0 0 10px;color:#eef4ff}.generated-detail-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:10px}.generated-detail-card{border:1px solid rgba(255,255,255,.09);border-radius:14px;padding:10px;background:rgba(255,255,255,.04)}.generated-detail-card b{display:block;color:#f7c85f;margin-bottom:4px}.generated-detail-card small{display:block;color:var(--muted);line-height:1.45}.generated-spec-line{display:flex;flex-wrap:wrap;gap:6px;margin-top:10px}.generated-spec-line code{padding:4px 7px;border-radius:999px;background:rgba(247,200,95,.1);border:1px solid rgba(247,200,95,.16);color:#f7c85f;font-size:.72rem}@media(max-width:1100px){.generated-asset-grid,.generated-detail-grid{grid-template-columns:repeat(2,minmax(0,1fr))}}@media(max-width:640px){.generated-asset-grid,.generated-detail-grid{grid-template-columns:1fr}.generated-asset-gallery{padding:16px}}
    `;
    document.head.appendChild(style);
  }

  function categoryDetails(category) {
    if (category === 'classes') return (details.classes || []).map((item) => ({ title: item.nameKo || item.id, line1: `${item.role} / ${item.weapon}`, line2: `HP ${item.gameplay?.hp} · ATK ${item.gameplay?.atk} · DEF ${item.gameplay?.def}` }));
    if (category === 'npcs') return (details.npcs || []).map((item) => ({ title: item.nameKo || item.id, line1: `${item.role} / ${item.interaction}`, line2: (item.services || []).slice(0, 3).join(' · ') }));
    if (category === 'monsters') return (details.monsters || []).map((item) => ({ title: item.nameKo || item.id, line1: `${item.rank} / ${item.family}`, line2: `HP ${item.hp} · ATK ${item.atk} · AI ${item.ai}` }));
    if (category === 'environment') {
      const env = details.environment || {};
      return [
        { title: 'Terrain', line1: `${(env.terrain || []).length} terrain tiles`, line2: (env.terrain || []).slice(0, 5).join(' · ') },
        { title: 'Props', line1: `${(env.props || []).length} prop tiles`, line2: (env.props || []).slice(0, 5).join(' · ') },
        { title: 'Animated', line1: `${(env.animated || []).length} animated targets`, line2: (env.animated || []).join(' · ') },
        { title: 'Collision', line1: 'passable / blocked / interactable', line2: `${env.collision?.interactable?.length || 0} interactables` }
      ];
    }
    return [];
  }

  function renderDetailPanel() {
    const spec = details.sheetTargets || {};
    const direction = details.directionSpec || {};
    const cards = concepts.flatMap((concept) => categoryDetails(concept.category).slice(0, 4));
    return `
      <div class="generated-detail-panel">
        <h3>실제 제작 사양</h3>
        <div class="generated-spec-line">
          <code>${escapeHtml(direction.count || 16)} directions</code>
          <code>class ${escapeHtml(spec.playableClass?.cellWidth || 96)}x${escapeHtml(spec.playableClass?.cellHeight || 128)}</code>
          <code>npc ${escapeHtml(spec.npc?.cellWidth || 80)}x${escapeHtml(spec.npc?.cellHeight || 112)}</code>
          <code>monster ${escapeHtml(spec.monster?.cellWidth || 112)}x${escapeHtml(spec.monster?.cellHeight || 112)}</code>
          <code>tile ${escapeHtml(spec.tile?.cellWidth || 64)}x${escapeHtml(spec.tile?.cellHeight || 64)}</code>
        </div>
        <div class="generated-detail-grid">${cards.slice(0, 16).map((item) => `<div class="generated-detail-card"><b>${escapeHtml(item.title)}</b><small>${escapeHtml(item.line1)}</small><small>${escapeHtml(item.line2)}</small></div>`).join('')}</div>
      </div>
    `;
  }

  function render() {
    const assetsSection = document.getElementById('assets');
    const main = document.querySelector('main');
    if (!main) return;
    injectStyles();
    if (document.getElementById('generatedAssetGallery')) return;
    const section = document.createElement('section');
    section.id = 'generatedAssetGallery';
    section.className = 'generated-asset-gallery panel';
    section.innerHTML = `
      <p class="eyebrow">GENERATED ASSET CONCEPTS</p>
      <h2>생성형 리소스 쇼케이스</h2>
      <p>클래스, NPC, 몬스터, 환경 타일/오브젝트 보강 이미지를 프로젝트 리소스 catalog에 등록했습니다. 현재 파일은 저장소 부담을 줄인 preview card이며, 상세 카탈로그는 실제 PNG/WebP sprite sheet 제작 기준으로 사용합니다.</p>
      <div class="generated-asset-grid">${concepts.map((item) => `<a class="generated-asset-card" href="${escapeHtml(item.path)}" target="_blank" rel="noreferrer"><span class="generated-asset-tag">${escapeHtml(item.category)}</span><img src="${escapeHtml(item.path)}" alt="${escapeHtml(item.title)}" loading="lazy" /><div><strong>${escapeHtml(item.title)}</strong><span>${escapeHtml((item.targets || []).slice(0, 6).join(' / '))}</span></div></a>`).join('')}</div>
      ${renderDetailPanel()}
    `;
    if (assetsSection?.parentNode) assetsSection.parentNode.insertBefore(section, assetsSection.nextSibling);
    else main.appendChild(section);
    loadIconGallery();
  }

  function escapeHtml(value) {
    return String(value ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\"/g, '&quot;').replace(/'/g, '&#039;');
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', render);
  else render();
})();
