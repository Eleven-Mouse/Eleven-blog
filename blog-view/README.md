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

This deployment uses Vercel for static hosting only. Article data is generated from the local `notes/` directory at build time and comments are hosted by Utterances through GitHub Issues.

Put Markdown articles into `notes/`. Nested folders become topics and relative images or documents are copied into the production build automatically.

```text
notes/
├── Java/
│   ├── 01-基础.md
│   └── image.png
└── Network/
    └── TCP.md
```

Production uses:

```env
VITE_CONTENT_SOURCE=static
VITE_UTTERANCES_REPO=Eleven-Mouse/Eleven-blog
```

The local `notes/` directory takes precedence over GitHub content configuration.

The following environment variables remain available as a GitHub-source fallback:

- `GITHUB_CONTENT_OWNER`
- `GITHUB_CONTENT_REPO`
- `GITHUB_CONTENT_BRANCH`
- `GITHUB_CONTENT_ROOT` (optional)
- `GITHUB_CONTENT_TOKEN` (optional, recommended for private repos or rate limits)
- `BLOG_STATIC_CONFIG_JSON` (optional JSON string for blog config)

The build runs `node scripts/generate-static-site.mjs` before `vite build`.

Before deploying, install the Utterances GitHub App for `Eleven-Mouse/Eleven-blog` from `https://github.com/apps/utterances`.

Deploy settings:

- Root Directory: `blog-view`
- Build Command: `npm run build`
- Output Directory: `dist`

No application backend, database, Redis, or upload service is required.
