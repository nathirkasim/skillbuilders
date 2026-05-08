/* Shared header renderer */
function renderHeader({ active = '', loggedIn = false } = {}) {
  const navLinks = loggedIn ? `
    <a href="user-enrolled-courses.html" class="${active==='courses'?'active':''}">My Courses</a>
    <a href="instructor-login.html" class="${active==='teach'?'active':''}">Teach</a>
    <a href="about-us.html" class="${active==='about'?'active':''}">About</a>
    <a href="user-cart-courses.html" class="icon-btn" title="Cart">
      <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></svg>
    </a>
    <a href="user-favourite-courses.html" class="icon-btn" title="Favourites">
      <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z"/></svg>
    </a>
    <a href="user-page.html" class="icon-btn" title="Profile">
      <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
    </a>
  ` : `
    <a href="user-login.html" class="${active==='courses'?'active':''}">My Courses</a>
    <a href="instructor-login.html" class="${active==='teach'?'active':''}">Teach</a>
    <a href="about-us.html" class="${active==='about'?'active':''}">About</a>
    <a href="user-cart-courses.html" class="icon-btn" title="Cart">
      <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></svg>
    </a>
    <a href="user-login.html" class="sb-btn sb-btn-outline btn-login hide-mobile">Log in</a>
    <a href="user-signup.html" class="sb-btn sb-btn-gold btn-signup">Sign up</a>
  `;

  return `
  <header class="sb-header">
    <a href="index.html" class="brand">
      <div class="brand-icon">S</div>
      <span class="brand-name">Skill<span>Builders</span></span>
    </a>
    <div class="sb-search">
      <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
      <input type="text" placeholder="Search courses…" id="globalSearch" autocomplete="off">
    </div>
    <nav class="sb-nav">${navLinks}</nav>
  </header>`;
}

function renderFooter() {
  return `
  <footer class="sb-footer">
    <div class="sb-footer-inner">
      <div>
        <div class="brand-name">Skill<span>Builders</span></div>
        <p>Empowering learners worldwide.</p>
      </div>
      <div style="font-size:.85rem;color:rgba(255,255,255,.5)">
        <p>📧 skillbuilders@gmail.com</p>
        <p>📞 6587135646</p>
      </div>
    </div>
    <div class="sb-footer-copy">Copyright &copy; 2024 SkillBuilders. All rights reserved.</div>
  </footer>`;
}

// Search redirect
document.addEventListener('DOMContentLoaded', () => {
  const search = document.getElementById('globalSearch');
  if (search) {
    search.addEventListener('keydown', e => {
      if (e.key === 'Enter' && search.value.trim()) {
        sessionStorage.setItem('searchQuery', search.value.trim());
        window.location.href = 'search-courses.html';
      }
    });
  }
});
