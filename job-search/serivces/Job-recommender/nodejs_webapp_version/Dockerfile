# Sử dụng một hình ảnh chứa Node.js
FROM node:18-alpine

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép package.json và package-lock.json (nếu có) vào thư mục làm việc
COPY package*.json ./

# Cài đặt các dependencies
RUN npm install

# Sao chép tất cả các mã nguồn vào thư mục làm việc
COPY . .

# Mở cổng 3000 cho ứng dụng
EXPOSE 80

# Khởi chạy ứng dụng khi container được khởi động
CMD ["node", "index.js"]
