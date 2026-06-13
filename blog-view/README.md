# vue-blog

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd) 
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Compile and Minify for Production

```sh
npm run build
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```

## Static content mode

The frontend can now read article data from `public/content/site.json` before falling back to `/api`.

1. Copy `public/content/site.example.json` to `public/content/site.json`, or configure the GitHub build envs below
2. Keep `VITE_CONTENT_SOURCE=auto` to prefer static content when the file exists
3. Set `VITE_CONTENT_SOURCE=static` to force static mode
4. Set `VITE_CONTENT_SOURCE=api` to force the old backend API mode

If you want Vercel to generate `site.json` directly from GitHub during build, set:

- `GITHUB_CONTENT_OWNER`
- `GITHUB_CONTENT_REPO`
- `GITHUB_CONTENT_BRANCH`
- `GITHUB_CONTENT_ROOT` (optional)
- `GITHUB_CONTENT_TOKEN` (optional, recommended for private repos or rate limits)
- `BLOG_STATIC_CONFIG_JSON` (optional JSON string for blog config)

The build now runs `node scripts/generate-static-site.mjs` before `vite build`.

Static mode currently covers:

- article detail
- homepage featured article
- categories/topics
- tags
- archive
- blog config

These features still need the backend:

- comments
- GitHub OAuth
- view count
- manual sync
