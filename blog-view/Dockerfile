FROM node:22-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:alpine

RUN rm /etc/nginx/conf.d/default.conf

COPY --from=builder /app/dist/ /usr/share/nginx/html/
COPY nginx-view.conf /etc/nginx/conf.d/default.conf

EXPOSE 80 8080

CMD ["nginx", "-g", "daemon off;"]
