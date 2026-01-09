
# 直接用 Nginx 镜像
FROM nginx:alpine

# 设置工作目录
WORKDIR /usr/share/nginx/html

# 把本地生成的 dist 文件夹拷贝进去
COPY dist .

# 2. 拷贝 nginx 配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
