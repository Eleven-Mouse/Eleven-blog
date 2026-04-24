import { defineStore } from 'pinia'

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme)
  document.documentElement.classList.toggle('md-editor-dark', theme === 'dark')
}

export const useThemeStore = defineStore('theme', {
  state: () => ({
    theme: localStorage.getItem('theme') || 'light',
  }),
  actions: {
    toggleTheme() {
      this.theme = this.theme === 'light' ? 'dark' : 'light';
      localStorage.setItem('theme', this.theme);
      applyTheme(this.theme);
    },
    initTheme() {
      applyTheme(this.theme);
    }
  },
});

