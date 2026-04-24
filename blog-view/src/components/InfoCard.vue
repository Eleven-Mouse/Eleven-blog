<template>
  <div class="sidebar-info modern-card">
    <div class="sidebar-info__avatar">
      <img src="/avatar.png" alt="Avatar" />
    </div>
    <h3 class="sidebar-info__name">Eleven-Mouse</h3>
    <p class="sidebar-info__bio">热爱编程，热爱生活</p>
    <div class="sidebar-info__social">
      <a href="https://github.com" target="_blank" aria-label="GitHub">
        <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.604-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.464-1.11-1.464-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.294 2.747-1.025 2.747-1.025.546 1.377.202 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.163 22 16.418 22 12c0-5.523-4.477-10-10-10z"/></svg>
      </a>
      <a href="#" aria-label="Email">
        <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="M22 4l-10 8L2 4"/></svg>
      </a>
    </div>
  </div>
</template>

<style scoped>
.sidebar-info {
  padding: 32px 22px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.sidebar-info::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: var(--hero-gradient);
  opacity: 0.5;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.sidebar-info__avatar {
  margin-bottom: 14px;
  position: relative;
  z-index: 1;
}

.sidebar-info__avatar img {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--bg-card);
  box-shadow: 0 0 0 2px var(--accent);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
}

.sidebar-info__avatar img:hover {
  transform: scale(1.05);
  box-shadow: 0 0 0 2px var(--accent), var(--shadow-glow);
}

.sidebar-info__name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px;
  letter-spacing: -0.01em;
}

.sidebar-info__bio {
  font-size: 13px;
  color: var(--text-muted);
  margin: 0 0 18px;
  line-height: 1.6;
}

.sidebar-info__social {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.sidebar-info__social a {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  color: var(--text-muted);
  background: var(--bg-secondary);
  transition: all var(--transition-fast);
}

.sidebar-info__social a:hover {
  color: #fff;
  background: var(--accent);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow);
}
</style>
