(() => {
  'use strict';

  const key = 'ydh-mmo-ws-url';

  function boot() {
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
