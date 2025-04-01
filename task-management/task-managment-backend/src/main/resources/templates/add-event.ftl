<!DOCTYPE html>
<html>
<head>
  <style>
    /* CSS styles */
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
    }
    
    h1 {
      color: #333;
    }
    
    p {
      margin-bottom: 10px;
    }
    
    ul {
      list-style-type: disc;
      margin-left: 20px;
    }
    
    .footer {
      background-color: #f5f5f5;
      padding: 10px;
      text-align: center;
    }
  </style>
</head>
<body>
  <h1>Bạn được thêm vào sự kiện mới!</h1>
  <p>Xin chào ${participantName},</p>
  <p>Bạn đã được thêm vào sự kiện <strong>${eventName}</strong>.</p>
  <p>Dưới đây là chi tiết sự kiện:</p>
  <ul>
    <li>Tên sự kiện:<strong>${eventName}</strong>.</li>
    <li>Sự kiện sẽ diễn ra vào: <strong>${eventDate}</strong>.</li>
  </ul>
  <p>Vui lòng đăng nhập vào hệ thống quản lý công việc hoặc ấn vào <a href=${link}>link này</a> để xem chi tiết.</p>
  <p>Trân trọng,</p>
  <p>Team dự án</p>
</body>
</html>