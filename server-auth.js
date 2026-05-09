(() => {
  'use strict';

  const TOKEN_KEY = 'ydh-server-auth-token-v1';
  const AUTH_KEY = 'ydh-server-auth-user-v1';
  const STATUS_KEY = 'ydh-server-auth-status-v1';
  const originalFetch = window.fetch.bind(window);

  function readJson(key, fallback = null) {
    try {
      return JSON.parse(localStorage.getItem(key) || 'null') ?? fallback;
    } catch {
      return fallback;
    }
  }

  function writeJson(key, value) {
    if (value === undefined || value === null) localStorage.removeItem(key);
    else localStorage.setItem(key, JSON.stringify(value));
  }

  function token() {
    return localStorage.getItem(TOKEN_KEY) || '';
  }

  function setToken(value, auth = null) {
    if (value) localStorage.setItem(TOKEN_KEY, value);
    else localStorage.removeItem(TOKEN_KEY);
    writeJson(AUTH_KEY, auth);
    window.dispatchEvent(new CustomEvent('ydh-server-auth-changed', { detail: { token: !!value, auth } }));
  }

  function isApiRequest(input) {
    const url = typeof input === 'string' ? input : input?.url || '';
    if (!url) return false;
    try {
      const parsed = new URL(url, window.location.origin);
      return parsed.origin === window.location.origin && parsed.pathname.startsWith('/api/');
    } catch {
      return String(url).startsWith('/api/');
    }
  }

  async function authFetch(input, init = {}) {
    const headers = new Headers(init.headers || (typeof input !== 'string' ? input.headers : undefined) || {});
    const currentToken = token();
    if (currentToken && isApiRequest(input) && !headers.has('Authorization')) {
      headers.set('Authorization', `Bearer ${currentToken}`);
    }
    return originalFetch(input, { ...init, headers });
  }

  window.fetch = authFetch;

  async function status() {
    const response = await authFetch('/api/auth/status');
    const result = await response.json();
    writeJson(STATUS_KEY, result);
    return result;
  }

  async function login({ accountId, displayName, secret } = {}) {
    const response = await authFetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accountId, displayName, secret })
    });
    const result = await response.json().catch(() => ({}));
    if (!response.ok || !result.ok) throw new Error(result.error || `HTTP ${response.status}`);
    setToken(result.token, result.auth);
    writeJson(STATUS_KEY, result.status || null);
    return result;
  }

  async function me() {
    const response = await authFetch('/api/auth/me');
    const result = await response.json().catch(() => ({}));
    if (!response.ok || !result.ok) throw new Error(result.error || `HTTP ${response.status}`);
    writeJson(AUTH_KEY, result.auth || null);
    return result;
  }

  function logout() {
    setToken('', null);
    return true;
  }

  function current() {
    return {
      token: token(),
      hasToken: !!token(),
      auth: readJson(AUTH_KEY, null),
      status: readJson(STATUS_KEY, null)
    };
  }

  window.YDH_SERVER_AUTH = { TOKEN_KEY, AUTH_KEY, STATUS_KEY, token, setToken, login, logout, me, status, current, fetch: authFetch };

  status().catch(() => null);
})();
