FROM node:22-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

ARG GITHUB_CONTENT_OWNER
ARG GITHUB_CONTENT_REPO
ARG GITHUB_CONTENT_BRANCH=main
ARG GITHUB_CONTENT_ROOT
ARG GITHUB_CONTENT_TOKEN

ENV GITHUB_CONTENT_OWNER=${GITHUB_CONTENT_OWNER}
ENV GITHUB_CONTENT_REPO=${GITHUB_CONTENT_REPO}
ENV GITHUB_CONTENT_BRANCH=${GITHUB_CONTENT_BRANCH}
ENV GITHUB_CONTENT_ROOT=${GITHUB_CONTENT_ROOT}
ENV GITHUB_CONTENT_TOKEN=${GITHUB_CONTENT_TOKEN}

RUN npm run build

FROM nginx:alpine

RUN rm /etc/nginx/conf.d/default.conf

COPY --from=builder /app/dist/ /usr/share/nginx/html/
COPY nginx-view.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
