# ===== Build stage =====
FROM node:22 AS build

WORKDIR /app

# Active npm 11 pour coller au local
RUN corepack enable && corepack prepare npm@11.6.1 --activate

# Copie d’abord uniquement les manifestes
COPY package*.json ./

# Utilise le lockfile => résolution stable
RUN npm ci --no-audit --no-fund

# Puis le reste des sources et build
COPY . .
RUN npm run build

# ===== Run stage (Nginx) =====
FROM nginx:1.27-alpine
ENV API_BASE_URL=http://localhost:9080
COPY --from=build /app/dist/chat-frontend/ /usr/share/nginx/html/
RUN printf '#!/bin/sh\n\n: "+${API_BASE_URL:=http://localhost:8080}"\nfind /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|http://localhost:8080|${API_BASE_URL}|g" {} \;\nexec nginx -g "daemon off;"\n' > /docker-entrypoint.sh \
  && chmod +x /docker-entrypoint.sh
EXPOSE 80
ENTRYPOINT ["/docker-entrypoint.sh"]
