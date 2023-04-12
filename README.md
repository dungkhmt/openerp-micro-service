<h1 align="center">OpenERP Microservice Sample Project</h1>

<div align="center">

[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

</div>

## Installation on Window

### 1. Installs Nginx

* Tải xuống Nginx [tại đây](https://nginx.org/download/nginx-1.23.4.zip)
* Sau đó giải nén và di chuyển đến thư mục <b>nginx-1.23.4</b>
* Copy [file cấu hình](https://drive.google.com/file/d/1cxQqamvnojM4zCzk3m4a4kkQfHt1OPsp/view?usp=sharing) vào thư
  mục <b>conf</b>

### 2. Build project notification

* Mở Git Bash tại thư mục <b>notification</b>
* Chạy lệnh: `mvn package`
* Chờ đến khi quá trình build thành công và xuất hiện thông báo <b>BUILD_SUCCESS</b>

### 3. Chạy project

### 3.1. Gateway

* Trong thư mục <b>nginx-1.23.4</b>, chạy lệnh: `start nginx`

### 3.2. Services

* Chạy các project: <b>notification</b> và <b>sample/openerp-resource-server</b>
* Chạy projetc <b>sample/openerp-oauth2-client</b>

Sau lần chạy thành công đầu tiên, ở các lần chạy sau chỉ cần thực hiện bước 3
