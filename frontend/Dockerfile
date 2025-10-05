# ===== Build stage =====
FROM node:22 AS build

WORKDIR /app

# Aligner la version de npm dans l'image sur celle qui génère le lock
RUN corepack enable && corepack prepare npm@11.6.1 --activate

# Copier *uniquement* les manifests avant l’install
COPY package*.json ./

# Regénère au besoin package-lock.json
RUN if [ -f package-lock.json ]; then \
      npm ci --no-audit --no-fund ; \
    else \
      npm install --no-audit --no-fund ; \
    fi
    
COPY . ./
RUN npm run build

# ===== Run stage (Nginx) =====
FROM nginx:1.27-alpine
ENV API_BASE_URL=http://localhost:8080
COPY --from=build /app/dist/chat-frontend/ /usr/share/nginx/html/
RUN printf '#!/bin/sh\n\n: "+${API_BASE_URL:=http://localhost:8080}"\nfind /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|http://localhost:8080|${API_BASE_URL}|g" {} \;\nexec nginx -g "daemon off;"\n' > /docker-entrypoint.sh \
  && chmod +x /docker-entrypoint.sh
EXPOSE 80
ENTRYPOINT ["/docker-entrypoint.sh"]
