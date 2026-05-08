(() => {
  'use strict';

  function addConsoleLog(message, type = 'loot-text') {
    const list = document.getElementById('logList');
    if (!list) return;
    const li = document.createElement('li');
    li.className = type;
    li.innerHTML = `<time>${new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })}</time><span>${message}</span>`;
    list.prepend(li);
    while (list.children.length > 12) list.lastElementChild.remove();
  }

  window.addEventListener('ydh-map-event', (event) => {
    const message = event.detail?.message || '맵 이벤트가 발생했습니다.';
    addConsoleLog(message, message.includes('조우') ? 'danger-text' : 'loot-text');
  });
})();
