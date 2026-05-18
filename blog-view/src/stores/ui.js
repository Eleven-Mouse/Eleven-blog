import { defineStore } from 'pinia'

export const useUiStore = defineStore('ui', {
  state: () => ({
    topicTreeOpen: true,
  }),
  actions: {
    toggleTopicTree() {
      this.topicTreeOpen = !this.topicTreeOpen
    },
    setTopicTreeOpen(open) {
      this.topicTreeOpen = !!open
    },
  },
})
