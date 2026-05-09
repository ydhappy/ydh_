(() => {
  'use strict';

  const auth = window.YDH_SERVER_AUTH;
  const schema = window.YDH_SAVE_SCHEMA;
  if (!auth) return;

  function readJson(key, fallback = null) {
    try {
      return JSON.parse(localStorage.getItem(key) || 'null') ?? fallback;
    } catch {
      return fallback;
    }
  }

  function defaultAccount() {
    const keys = schema?.localStorageKeys || {};
    return readJson(keys.accountProfile || 'ydh-account-profile-v1', {}) || {};
  }

  function injectStyles() {
    if (document.getElementById('serverAuthPanelStyles')) return;
    const style = document.createElement('style');
    style.id = 'serverAuthPanelStyles';
    style.textContent = `
      .server-auth-panel{margin:14px 0 22px;padding:18px;border-radius:20px;background:rgba(0,0,0,.2);border:1px solid rgba(247,200,95,.22)}
      .server-auth-head{display:flex;justify-content:space-between;gap:14px;align-items:flex-start;margin-bottom:12px}.server-auth-head h3{margin:0 0 6px;color:#eef4ff}.server-auth-head p{margin:0;color:var(--muted);line-height:1.55}.server-auth-badge{padding:9px 12px;border-radius:999px;background:rgba(247,200,95,.12);color:#f7c85f;font-weight:900;white-space:nowrap}.server-auth-badge.on{background:rgba(105,221,160,.12);color:#69dda0}.server-auth-form{display:grid;grid-template-columns:1fr 1fr 1fr auto auto auto;gap:8px;align-items:center}.server-auth-form input{min-width:0;border-radius:14px;border:1px solid var(--line);background:rgba(0,0,0,.24);color:#eef4ff;padding:10px 12px}.server-auth-form button{border:1px solid var(--line);border-radius:999px;background:rgba(255,255,255,.06);color:var(--muted);padding:9px 12px;font-weight:900;cursor:pointer}.server-auth-form button:hover{background:var(--gold);color:#1b1207}.server-auth-status{margin-top:10px;color:var(--muted);font-size:.82rem;line-height:1.55}.server-auth-status.error{color:#ff7b7b}.server-auth-session{margin-top:10px;display:grid;gap:4px;color:var(--muted);font-size:.78rem;line-height:1.45}.server-auth-session code{color:#eef4ff}@media(max-width:1100px){.server-auth-form{grid-template-columns:1fr}.server-auth-head{flex-direction:column}.server-auth-badge{width:100%;box-sizing:border-box}}
    `;
    document.head.appendChild(style);
  }

  function badgeText() {
    const current = auth.current();
    if (current.hasToken && current.hasRefreshToken) return 'ACCESS + REFRESH ON';
    if (current.hasToken) return 'ACCESS TOKEN ON';
    if (current.hasRefreshToken) return 'REFRESH TOKEN ON';
    const required = current.status?.status?.required || current.status?.required;
    return required ? 'AUTH REQUIRED' : 'AUTH OPTIONAL';
  }

  function statusText() {
    const current = auth.current();
    const user = current.auth;
    const required = current.status?.status?.required || current.status?.required;
    if (user?.accountId) return `로그인됨: ${user.displayName || user.accountId} / ${user.accountId}`;
    if (required) return '서버 인증이 켜져 있습니다. 계정 ID와 공유 secret을 입력해 로그인하세요.';
    return '서버 인증은 선택 상태입니다. 로그인하면 저장/맵 API 요청에 Bearer token이 자동 첨부됩니다.';
  }

  function sessionText() {
    const current = auth.current();
    const session = current.session;
    const status = current.status?.status || current.status || {};
    const refresh = status.refresh || {};
    return [
      `access token: ${current.hasToken ? '있음' : '없음'}`,
      `refresh token: ${current.hasRefreshToken ? '있음' : '없음'}`,
      `session: ${session?.sessionId || '-'}`,
      `expires: ${session?.expiresAt || '-'}`,
      `access ttl: ${status.tokenTtlSeconds || '-'}s / refresh ttl: ${refresh.refreshTtlSeconds || '-'}s`
    ];
  }

  function render(error = '') {
    if (!document.body) return;
    injectStyles();
    let panel = document.getElementById('serverAuthPanel');
    if (!panel) {
      panel = document.createElement('section');
      panel.id = 'serverAuthPanel';
      panel.className = 'server-auth-panel';
      const account = document.getElementById('account');
      if (account?.parentNode) account.parentNode.insertBefore(panel, account.nextSibling);
      else document.querySelector('main')?.prepend(panel);
    }
    const account = defaultAccount();
    const current = auth.current();
    panel.innerHTML = `
      <div class="server-auth-head">
        <div>
          <p class="eyebrow">SERVER AUTH</p>
          <h3>서버 계정 인증</h3>
          <p>access token은 짧게 쓰고 refresh token 세션으로 재발급합니다. /api 요청 401 발생 시 자동 refresh 후 1회 재시도합니다.</p>
        </div>
        <div class="server-auth-badge ${current.hasToken ? 'on' : ''}">${escapeHtml(badgeText())}</div>
      </div>
      <form class="server-auth-form" id="serverAuthForm">
        <input id="serverAuthAccountId" placeholder="accountId" value="${escapeHtml(account.accountId || current.auth?.accountId || 'local')}" />
        <input id="serverAuthDisplayName" placeholder="displayName" value="${escapeHtml(account.displayName || current.auth?.displayName || 'YDH Player')}" />
        <input id="serverAuthSecret" type="password" placeholder="shared secret" autocomplete="current-password" />
        <button type="submit">로그인</button>
        <button type="button" id="serverAuthRefresh">재발급</button>
        <button type="button" id="serverAuthLogout">로그아웃</button>
      </form>
      <div class="server-auth-status ${error ? 'error' : ''}">${escapeHtml(error || statusText())}</div>
      <div class="server-auth-session">${sessionText().map((line) => `<span>${escapeHtml(line)}</span>`).join('')}</div>
    `;

    panel.querySelector('#serverAuthForm')?.addEventListener('submit', async (event) => {
      event.preventDefault();
      const accountId = panel.querySelector('#serverAuthAccountId')?.value || 'local';
      const displayName = panel.querySelector('#serverAuthDisplayName')?.value || 'YDH Player';
      const secret = panel.querySelector('#serverAuthSecret')?.value || '';
      try {
        await auth.login({ accountId, displayName, secret });
        render();
      } catch (err) {
        render(err.message);
      }
    });

    panel.querySelector('#serverAuthRefresh')?.addEventListener('click', async () => {
      try {
        await auth.refresh();
        render('access token을 재발급했습니다.');
      } catch (err) {
        render(err.message);
      }
    });

    panel.querySelector('#serverAuthLogout')?.addEventListener('click', async () => {
      await auth.logout();
      render();
    });
  }

  function escapeHtml(value) {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  function boot() {
    render();
    window.addEventListener('ydh-server-auth-changed', () => render());
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', boot);
  else boot();
})();
