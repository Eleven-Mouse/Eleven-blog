<template>
  <footer class="site-footer">


    <div class="site-footer__inner page-container">
      <div class="site-footer__grid">
        <div class="site-footer__brand">
 
          <p class="site-footer__desc">ERROR 404 — 社交生活未找到。正在用代码填补中...</p>
        </div>
      </div>
      <div class="site-footer__divider" />

      <div class="site-footer__bottom">
        <span>© {{ year }} Eleven-Mouse</span>
        <span class="site-footer__sep">·</span>
        <span>All rights reserved.</span>
      </div>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.site-footer {
  margin-top: 80px;
  background: var(--bg-card);
  border-top: 1px solid var(--border-light);
  display: flex;
  flex-direction: column;
  transition: background 0.3s;
}

.site-footer__accent {
  height: 2px;
  background: linear-gradient(90deg, var(--accent), rgba(var(--accent-rgb), 0.4) 60%, transparent);
}

.site-footer__inner {
  padding: 44px 24px 28px;
}

.site-footer__grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 36px;
  align-items: start;
}

.site-footer__brand {
  max-width: 400px;
}

.site-footer__logo {
  font-size: 22px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.03em;
}

.site-footer__dot {
  color: var(--accent);
}

.site-footer__desc {
  margin: 12px 0 0;
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.75;
}

.site-footer__divider {
  margin: 28px 0 20px;
  height: 1px;
  background: var(--border-light);
}

.site-footer__bottom {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-muted);
}

.site-footer__sep {
  opacity: 0.4;
}

@media (max-width: 768px) {
  .site-footer__grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .site-footer__inner {
    padding: 28px 16px 20px;
  }

  .site-footer {
    margin-top: 48px;
  }

  .site-footer__brand {
    max-width: 100%;
  }
}
</style>
