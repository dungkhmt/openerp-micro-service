<h1 align="center">Baseweb</h1>

<div align="center">

Base infrastructure for web application.

[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

</div>

## Installation on Window

Bạn cần có những thứ sau được cài đặt và cấu hình sẵn trước khi bắt đầu cài đặt
project: [Apache Maven](https://maven.apache.org/), [PostgreSQL](https://www.postgresql.org/)
, [MongoDB](https://www.mongodb.com/), [Redis](https://redis.io/). Nếu chưa cài đặt, vui lòng xem hướng dẫn sau:

* [Hướng dẫn cài đặt Apache Maven](https://drive.google.com/file/d/1xhdd8rBQWeVe0koZb5ditL8kCWaJm-lM/view?usp=sharing)
* [Hướng dẫn cài đặt PostgreSQL](https://drive.google.com/file/d/1o15E-QNNgHeZK5F1N7h4FfxYpT3B9S92/view?usp=sharing)
* [Hướng dẫn cài đặt MongoDB](https://drive.google.com/file/d/1pNgZmw8TBU3uSzaAwZiXiVW5dp6Pjw7i/view?usp=sharing)

### Tài nguyên

Ở thời điểm hiện tại, các công nghệ sử dụng đã phát hành các phiên bản mới với nhiều bổ sung, nâng cấp. Tuy nhiên, những
nâng cấp đó không phải lúc nào cũng đảm bảo được tính tương thích ngược, dẫn đến việc cài đặt theo hướng dẫn này có thể
gặp những lỗi phát sinh không cần thiết. Vì vậy, nên sử dụng các bộ cài đặt được cung cấp ở đây:

* [Installers](https://drive.google.com/drive/folders/1r4VCwCz2JZGg9-LxQFPNw1aTZJl9gYp3?usp=sharing)

Khi đã sẵn sàng cho quá trình cài đặt project, thực hiện lần lượt các bước 1 đến 4:

### 1. Cài đặt Database

### 1.1. PostgreSQL

* Từ <b>Searchbar</b> trên thanh <b>Taskbar</b>, gõ <b>pgAdmin4</b> để tìm kiếm, chọn <b>pgAdmin4</b> từ danh sách kết
  quả để khởi động <b>pgAdmin</b>
* Trong <b>pgAdmin</b>, tạo mới một Database với tên tuỳ ý
* Sau khi tạo xong Database, click chuột phải vào Database vừa tạo, chọn <b>restore</b>, một hộp thoại sẽ mở ra
* Ở trường <b>Filename</b>, browse đến nơi tải xuống và
  chọn [file backup](https://drive.google.com/drive/u/0/folders/1_F9I-ctSx0Wj1m5vcz-MnWXJBApbqBbO) (chú ý: chọn Format
  là <b>All Files</b> khi browse)
* Ở trường <b>Role name</b>, chọn <b>postgres</b> (option nằm ở cuối), sau đó chọn <b>Restore</b>
* Có thể backup và restore DB postgres bằng command line:

```
Backup: pg_dump.exe -U postgres -W -F p baseweb > "D:\projects\baseweb\db\baseweb20211201"
```

```
Restore: psql.exe -U postgres -d baseweb -f "D:\projects\baseweb\db\baseweb20211201"
```

### 1.2. Cấu hình Mongo Replica Set

Với `{version}` là phiên bản MongoDB được cài đặt, ví dụ: 4.2, thực hiện lần lượt các bước sau:

* Click chuột phải vào thanh <b>Taskbar</b> → chọn <b>Task Manager</b> → chọn <b>Services</b> → tìm và click chuột phải
  vào <b>MongoDB</b> → chọn <b>Stop</b>
* Tạo thư mục: <b>C:\data\db</b> trong ổ C
* Trong thư mục <b>db</b> tạo lần lượt 3 thư mục con: <b>mongo27017</b>, <b>mongo27018</b>, <b>mongo27019</b>
* Mở một Command Prompt (cmd), chạy lần lượt 2 lệnh: <br/>

```
cd C:\Program Files\MongoDB\Server\{version}\bin
```

```
mongod --port 27017 --dbpath C:\data\db\mongo27017 --replSet rs0
```

* Mở thêm một cmd mới, chạy lần lượt 2 lệnh: <br/>

```
cd C:\Program Files\MongoDB\Server\{version}\bin
```

```
mongod --port 27018 --dbpath C:\data\db\mongo27018 --replSet rs0
```

* Mở thêm một cmd mới, chạy lần lượt 2 lệnh: <br/>

```
cd C:\Program Files\MongoDB\Server\{version}\bin
```

```
mongod --port 27019 --dbpath C:\data\db\mongo27019 --replSet rs0
```

* Mở thêm một cmd mới, chạy lần lượt 5 lệnh: <br/>

```
cd C:\Program Files\MongoDB\Server\{version}\bin
```

```
mongo --port 27017
```

```
rs.initiate()
```

```
rs.add(“localhost:27018”)
```

```
rs.add(“localhost:27019”)
```

### 2. Cấu hình project

* Import project vào <b>IntelliJ IDEA</b> hoặc <b>Eclipse</b> (optional, có thể không làm tại bước này)
* Copy [file cấu hình](https://drive.google.com/file/d/1cxurrBoNn6cNgOx_Q9i22meYtMP02iJN/view?usp=sharing) vào thư
  mục: <b>src\main\resources</b>
* Trong file cấu hình, điền mật khẩu và tên PostgreSQL Database được tạo ở 1.1 tương ứng cho các thuộc tính <b>
  SQL_DB_PASS</b> và <b>POSTGRES_DB</b>, điền đường dẫn đến thư mục project <b>baseweb</b> cho thuộc tính <b>
  filesystemRoot</b> (ví dụ: D:/openerp/baseweb/)

### 3. Build project

### 3.1. Khởi động các dịch vụ (redis, mongo replica set)

* Chạy file <b>redis-server.exe</b> trong thư
  mục [redis-2.4.5](https://drive.google.com/drive/folders/1WilP451UfPN33uM1RSUreCX9rJmVVbMK?usp=sharing)<b>\64bit</b>
  để khởi động redis
* Chạy file [openerp.bat](https://drive.google.com/file/d/1D5ZRsY0S8-hAPjEZX6x2DwDrjZs7NqLQ/view?usp=sharing) để khởi
  động mongo replica set (lưu ý: nếu phiên bản MongoDB được cài đặt khác 4.2 thì cần thay thế tất cả 4.2 trong nội dung
  file này thành phiên bản đươc cài, ví dụ: 4.4)

[comment]: <> (### 3.2. Cài đặt Google-ORTools)

[comment]: <> (Đối với hệ điều hành Windows:)

[comment]: <> (* Mở Git Bash tại thư mục project - <b>baseweb</b> &#40;hoặc mở Git Bash tại thư mục khác và `cd` đến thư mục project&#41;)

[comment]: <> (* Chạy lệnh: `mvn clean`)

[comment]: <> (* Mở Git Bash tại thư mục <b>libs\ortools\Windows</b> &#40;hoặc mở Git Bash tại thư mục khác và `cd` đến thư mục này&#41;)

[comment]: <> (* Chạy lần lượt 2 lệnh: <br/>)

[comment]: <> (```)

[comment]: <> (mvn install:install-file -Dfile=ortools-win32-x86-64-8.0.8283.jar -DpomFile=pom-runtime.xml)

[comment]: <> (```)

[comment]: <> (```)

[comment]: <> (mvn install:install-file -Dfile=ortools-java-8.0.8283.jar -DpomFile=pom-local.xml)

[comment]: <> (```)

### 3.2. Build

* Mở Git Bash tại thư mục project - <b>baseweb</b> (hoặc mở Git Bash tại thư mục khác và `cd` đến thư mục project)
* Chạy lệnh: `mvn package`
* Chờ đến khi quá trình build thành công và xuất hiện thông báo <b>BUILD_SUCCESS</b>

### 4. Chạy project

* Trong thư mục: <b>src\main\java\com\hust\baseweb</b>, chạy file <b>BasewebApplication.java</b>

Sau lần chạy thành công đầu tiên, ở các lần chạy sau chỉ cần thực hiện lần lượt bước 3.1 và 4

### 5. API documentation

Sau khi ứng dụng khởi động thành công, truy cập URL sau để xem documentation của các REST API trong hệ
thống: http://localhost:8080/api/swagger-ui/

### 6. Một số lỗi thường gặp

### 6.1. You need to run build with JDK or have tools.jar on the classpath

* Copy file <b>tools.jar</b> ở thư mục <b>C:\Program Files\Java\jdk1.8.0_251\lib</b> vào thư mục <b>C:\Program
  Files\Java\jre1.8.0_251\lib</b>

### 7. Tips cấu hình IDEs

* [Tips cấu hình IDEs](https://drive.google.com/file/d/1fKf7MTXCSlk1VpL6iACoHWCvqeE6Ldgc/view?usp=sharing)

### 8. Một số dịch vụ chung trong hệ thống

* <b>Gửi Mail từ hệ thống tới người dùng:</b> src/main/java/com/hust/baseweb/applications/mail/service/MailService.java
* <b>Thông báo:</b> src/main/java/com/hust/baseweb/applications/notifications/service/NotificationsService.java

### 9. Một số lưu ý và yêu cầu dành cho nhà phát triển

Nhằm mục đích giữ cho code base đạt chất lượng, hiệu năng cao, chuyên nghiệp, thống nhất, có khả năng bảo trì, mỗi nhà
phát triển cần đảm bảo những yêu cầu <b>QUAN TRỌNG</b> và <b>BẮT BUỘC</b> sau:

* Refactor, format code trước khi commit. IntelliJ đã cung cấp sẵn tính năng để thực hiện việc này
* Viết documentation (Java doc) đầy đủ cho các phương thức, lớp, API mà mình tạo ra
* Trong một module, để triển một tính năng mới có thể sẽ cần sử dụng một tính năng thuộc trách nhiệm của module
  khác hoặc chính module đó. Vì vậy, cần phải tìm hiểu, trao đổi với những nhà phát triển khác trước khi quyết định tự
  cài đặt. Việc này nhằm tái sử dụng tối đa những logic, service đã được phát triển, kiểm thử trước đó và tránh lặp code
    
* Tuân thủ các nguyên tắc của công nghệ, giao thức, ngôn ngữ lập trình,... Ví dụ:
  * HTTP:  sử dụng phù hợp các phương thức HTTP,... 
  * REST API: quy tắc đặt URI cho API, validate API,...
  * Java: đặt tên biến, phương thức, lớp; nguyên lý OOP, SOLID,...
  

