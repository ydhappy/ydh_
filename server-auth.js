(() => {
  'use strict';

  const TOKEN_KEY = 'ydh-server-auth-token-v1';
  const REFRESH_KEY = 'ydh-server-auth-refresh-token-v1';
  const AUTH_KEY = 'ydh-server-auth-user-v1';
  const SESSION_KEY = 'ydh-server-auth-session-v1';
  const STATUS_KEY = 'ydh-server-auth-status-v1';
  const originalFetch = window.fetch.bind(window);
  let refreshing = null;

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

  function refreshToken() {
    return localStorage.getItem(REFRESH_KEY) || '';
  }

  function setToken(value, auth = null, session = null) {
    if (value) localStorage.setItem(TOKEN_KEY, value);
    else localStorage.removeItem(TOKEN_KEY);
    writeJson(AUTH_KEY, auth);
    if (session !== undefined) writeJson(SESSION_KEY, session);
    window.dispatchEvent(new CustomEvent('ydh-server-auth-changed', { detail: { token: !!value, auth, session } }));
  }

  function setRefreshToken(value) {
    if (value) localStorage.setItem(REFRESH_KEY, value);
    else localStorage.removeItem(REFRESH_KEY);
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

  function isAuthEndpoint(input) {
    const url = typeof input === 'string' ? input : input?.url || '';
    try {
      const parsed = new URL(url, window.location.origin);
      return parsed.pathname.startsWith('/api/auth/');
    } catch {
      return String(url).startsWith('/api/auth/');
    }
  }

  function withAuthHeaders(input, init = {}) {
    const headers = new Headers(init.headers || (typeof input !== 'string' ? input.headers : undefined) || {});
    const currentToken = token();
    if (currentToken && isApiRequest(input) && !headers.has('Authorization')) {
      headers.set('Authorization', `Bearer ${currentToken}`);
    }
    return { ...init, headers };
  }

  async function refreshAccessToken() {
    const currentRefresh = refreshToken();
    if (!currentRefresh) return null;
    if (refreshing) return refreshing;

    refreshing = (async () => {
      const response = await originalFetch('/api/auth/refresh', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: currentRefresh })
      });
      const result = await response.json().catch(() => ({}));
      if (!response.ok || !result.ok) {
        setToken('', null, null);
        setRefreshToken('');
        throw new Error(result.error || `HTTP ${response.status}`);
      }
      setToken(result.token, result.auth, result.session || null);
      setRefreshToken(result.refreshToken || currentRefresh);
      writeJson(STATUS_KEY, result.status || null);
      return result;
    })().finally(() => {
      refreshing = null;
    });

    return refreshing;
  }

  async function authFetch(input, init = {}) {
    const firstInit = withAuthHeaders(input, init);
    const first = await originalFetch(input, firstInit);
    if (first.status !== 401 || !isApiRequest(input) || isAuthEndpoint(input) || init.__ydhRetried) return first;

    try {
      await refreshAccessToken();
      const retryInit = withAuthHeaders(input, { ...init, __ydhRetried: true });
      delete retryInit.__ydhRetried;
      return originalFetch(input, retryInit);
    } catch {
      return first;
    }
  }

  window.fetch = authFetch;

  async function status() {
    const response = await authFetch('/api/auth/status');
    const result = await response.json();
    writeJson(STATUS_KEY, result);
    return result;
  }

  async function login({ accountId, displayName, secret } = {}) {
    const response = await originalFetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ accountId, displayName, secret })
    });
    const result = await response.json().catch(() => ({}));
    if (!response.ok || !result.ok) throw new Error(result.error || `HTTP ${response.status}`);
    setToken(result.token, result.auth, result.session || null);
    setRefreshToken(result.refreshToken || '');
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

  async function refresh() {
    return refreshAccessToken();
  }

  async function logout() {
    const currentRefresh = refreshToken();
    if (currentRefresh) {
      await originalFetch('/api/auth/logout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: currentRefresh })
      }).catch(() => null);
    }
    setToken('', null, null);
    setRefreshToken('');
    return true;
  }

  function current() {
    return {
      token: token(),
      refreshToken: refreshToken(),
      hasToken: !!token(),
      hasRefreshToken: !!refreshToken(),
      auth: readJson(AUTH_KEY, null),
      session: readJson(SESSION_KEY, null),
      status: readJson(STATUS_KEY, null)
    };
  }

  window.YDH_SERVER_AUTH = { TOKEN_KEY, REFRESH_KEY, AUTH_KEY, SESSION_KEY, STATUS_KEY, token, refreshToken, setToken, setRefreshToken, login, logout, refresh, me, status, current, fetch: authFetch };

  status().catch(() => null);
})();
