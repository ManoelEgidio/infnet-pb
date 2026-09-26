# ==========================================
# Multi-Stage Dockerfile for React SPA Frontend
# Stage 1: Build static assets with Bun
# ==========================================
FROM oven/bun:alpine AS builder
WORKDIR /app

# Copy dependency descriptors
COPY package.json bun.lock ./
RUN bun install --frozen-lockfile

# Copy frontend source and compile production bundle
COPY . .
RUN bun run build

# ==========================================
# Stage 2: Serve with Nginx Alpine
# ==========================================
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Clean default nginx website
RUN rm -rf ./*

# Copy compiled production bundle
COPY --from=builder /app/dist ./

# Copy reverse proxy and SPA routing configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/healthz || exit 1

CMD ["nginx", "-g", "daemon off;"]
