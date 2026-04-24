<template>
  <article class="article-card modern-card stagger-item" @click="goArticle">
    <div class="article-card__body">
      <h3 class="article-card__title">
        <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
      </h3>

      <p class="article-card__summary">{{ article.summary }}</p>

      <div class="article-card__meta">
        <span class="article-card__date">
          <svg viewBox="0 0 16 16" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="8" cy="8" r="6"/><path d="M8 4v4l2.5 1.5"/></svg>
          {{ formattedDate }}
        </span>
        <span v-if="article.categoryName" class="article-card__cat">
          <svg viewBox="0 0 16 16" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M2 4h12v9H2z"/><path d="M6 4V2h4v2"/></svg>
          {{ article.categoryName }}
        </span>
        <span v-if="article.viewCount" class="article-card__views">
          <svg viewBox="0 0 16 16" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M1 8s3-5 7-5 7 5 7 5-3 5-7 5-7-5-7-5z"/><circle cx="8" cy="8" r="2"/></svg>
          {{ article.viewCount }}
        </span>
        <span class="article-card__meta-tags" v-if="tagList.length">
          <span v-for="tag in tagList.slice(0, 3)" :key="tag" class="tag--sm">{{ tag }}</span>
        </span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  article: { type: Object, required: true },
})

const router = useRouter()

const formattedDate = computed(() => {
  if (!props.article.publishTime) return ''
  return new Date(props.article.publishTime).toLocaleDateString('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric',
  })
})

const tagList = computed(() => {
  const tags = props.article.tags
  if (!tags) return []
  if (Array.isArray(tags)) return tags
  if (typeof tags === 'string') return tags.split(',').map(t => t.trim()).filter(Boolean)
  return []
})

const goArticle = () => {
  router.push(`/article/${props.article.id}`)
}
</script>

<style scoped>
.article-card {
  cursor: pointer;
  border-radius: var(--radius-lg);
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  transition: transform var(--transition-normal), box-shadow var(--transition-normal),
    border-color var(--transition-normal);
}

.article-card:hover {
  transform: translateY(-4px) scale(1.005);
  border-color: rgba(var(--accent-rgb), 0.18);
}

.article-card__body {
  padding: 22px 26px;
  display: flex;
  flex-direction: column;
}

.article-card__title {
  margin: 0 0 10px;
  font-size: 1.15rem;
  font-weight: 650;
  line-height: 1.45;
  letter-spacing: -0.01em;
}

.article-card__title a {
  color: var(--text-primary);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.article-card:hover .article-card__title a {
  color: var(--accent);
}

.article-card__summary {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.75;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-card__meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: var(--text-muted);
}

.article-card__meta-tags {
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.tag--sm {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  background: var(--tag-bg);
  color: var(--accent);
  white-space: nowrap;
  font-weight: 500;
}

.article-card__meta span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 768px) {
  .article-card__body {
    padding: 16px;
  }

  .article-card__title {
    font-size: 1.05rem;
  }

  .article-card__summary {
    -webkit-line-clamp: 2;
  }

  .article-card__meta {
    flex-wrap: wrap;
    gap: 10px;
    font-size: 12px;
  }
}
</style>
