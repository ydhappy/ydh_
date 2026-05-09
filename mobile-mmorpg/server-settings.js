(() => {
  'use strict';

  const key = 'ydh-mmo-ws-url';

  function injectStyles() {
    if (document.getElementById('serverSettingsStyles')) return;
    const style = document.createElement('style');
    style.id = 'serverSettingsStyles';
    style.textContent = `
      .server-form{display:flex;gap:8px;margin:8px 0 5px}.server-form input{flex:1;min-width:0;border:1px solid var(--line);border-radius:14px;background:rgba(255,255,255,.06);color:#eef4ff;padding:11px 12px}.server-form button{border:0;border-radius:14px;background:var(--gold);color:#160e04;padding:0 14px;font-weight:1000}.server-help{display:block;color:var(--muted);line-height:1.45;margin-bottom:12px}
    `;
    document.head.appendChild(style);
  }

  function boot() {
    injectStyles();
    const form = document.getElementById('serverForm');
    const input = document.getElementById('serverUrlInput');
    if (!form || !input) return;
    input.value = localStorage.getItem(key) || '';
    form.addEventListener('submit', (event) => {
      event.preventDefault();
      const value = input.value.trim();
      if (value) localStorage.setItem(key, value);
      else localStorage.removeItem(key);
      const chat = document.getElementById('chatLog');
      if (chat) {
        const p = document.createElement('p');
        p.textContent = value ? `서버 주소 저장: ${value}` : '서버 주소 기본값 사용';
        chat.appendChild(p);
      }
      alert('서버 주소가 저장되었습니다. 앱을 재시작하면 적용됩니다.');
    });
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
