FROM node:20 as react-build

WORKDIR /app

COPY package.json .
RUN npm i --force
COPY . ./
RUN npm run build

FROM leanhtuan/nginx-with-brotli:latest
COPY default.conf /etc/nginx/nginx.conf
COPY --from=react-build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
