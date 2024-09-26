<!DOCTYPE html>

<html>
<head>
  <meta charset="UTF-8">
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
    }

    p {
      margin-bottom: 10px;
    }
    
    h2 {
      color: #333;
    }

    ul {
      list-style-type: disc;
      margin-left: 20px;
    }
  </style>
</head>
<body>
  <h2>Bạn được phân công nhiệm vụ mới</h2>
  <p>Chào ${assigneeName},</p>
  <p>Bạn đã được phân công một công việc mới với các chi tiết sau:</p>
  <ul>
    <li><strong>Tên Công việc:</strong> ${taskName}</li>
    <li><strong>Được giao bởi:</strong> ${assignedBy}</li>
    <li><strong>Hạn chót:</strong> ${dueDate}</li>
  </ul>
  <p>Vui lòng đăng nhập vào hệ thống quản lý công việc hoặc ấn vào <a href=${link}>link này</a> để xem chi tiết.</p>
  <p>Trân trọng,</p>
  <p>Team dự án</p> 
</body>
</html>