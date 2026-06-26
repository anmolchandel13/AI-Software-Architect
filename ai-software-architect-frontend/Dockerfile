# =============================================================
# Build Stage — Compiles the React/Vite production build
# =============================================================
FROM node:18-alpine AS build
WORKDIR /app

# Copy dependency definitions and install packages
COPY package.json ./
RUN npm install

# Copy source code and bundle
COPY . .
RUN npm run build

# =============================================================
# Run Stage — Serve build via Nginx web server
# =============================================================
FROM nginx:alpine

# Copy the bundled assets from the build stage to Nginx directory
COPY --from=build /app/dist /usr/share/nginx/html

# Replace default Nginx configuration with custom proxy setup
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose HTTP port
EXPOSE 80

# Launch Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
