# /frontend/Dockerfile

# Giai đoạn 1: Build
FROM node:20 as react-build
WORKDIR /app

ARG NODE_ENV
ENV NODE_ENV=$NODE_ENV

COPY package*.json ./
RUN npm i --legacy-peer-deps
COPY . .
# Bây giờ, lệnh "npm run build" sẽ đọc biến REACT_APP_API_URL và đưa vào code
RUN npm run build

# Giai đoạn 2: Serve
FROM leanhtuan/nginx-with-brotli:latest
COPY default.conf /etc/nginx/nginx.conf
COPY --from=react-build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]