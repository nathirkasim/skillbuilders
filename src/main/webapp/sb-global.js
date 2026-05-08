/* ============================================================
   SKILLBUILDERS GLOBAL JS — Toast, Spinner, Session helpers
   ============================================================ */

// ── Toast notifications ──
function sbToast(message, type = 'info', duration = 3500) {
  let container = document.getElementById('sb-toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'sb-toast-container';
    document.body.appendChild(container);
  }
  const icons = { success: '✓', error: '✕', info: 'ℹ' };
  const toast = document.createElement('div');
  toast.className = `sb-toast ${type}`;
  toast.innerHTML = `<span style="font-size:1.1rem;font-weight:700">${icons[type]||'ℹ'}</span><span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.classList.add('out');
    toast.addEventListener('animationend', () => toast.remove());
  }, duration);
}

// ── Loading spinner ──
function sbShowLoading() {
  if (document.getElementById('sb-loading')) return;
  const overlay = document.createElement('div');
  overlay.id = 'sb-loading';
  overlay.className = 'sb-loading-overlay';
  overlay.innerHTML = `<div style="text-align:center">
    <div class="sb-spinner"></div>
    <p style="color:#fcd34d;font-family:'DM Sans',sans-serif;margin-top:14px;font-size:.9rem">Loading…</p>
  </div>`;
  document.body.appendChild(overlay);
}
function sbHideLoading() {
  const el = document.getElementById('sb-loading');
  if (el) el.remove();
}

// ── Skeleton loader helper ──
function sbSkeleton(height = '80px', width = '100%', radius = '8px') {
  return `<div class="sb-skeleton" style="height:${height};width:${width};border-radius:${radius}"></div>`;
}

// ── Session expired handler ──
function sbHandleSessionError(data) {
  if (data && data.status === 'error' && data.message && data.message.toLowerCase().includes('session')) {
    sbToast('Session expired. Redirecting to login…', 'error');
    setTimeout(() => { window.location.href = 'user-login.html'; }, 2200);
    return true;
  }
  return false;
}

// ── Page init: reveal animations ──
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.sb-animate').forEach(el => {
    el.style.opacity = '0';
  });
  requestAnimationFrame(() => {
    document.querySelectorAll('.sb-animate').forEach(el => {
      el.style.opacity = '';
    });
  });
});
