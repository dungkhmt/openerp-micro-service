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
  <h1>Thông báo Cập nhật Vai trò!</h1>
  <p>Xin chào ${memberFirstName} ${memberLastName},</p>
  <p>Vai trò của bạn trong dự án "${projectName}" đã được cập nhật.</p>
  <p>Dưới đây là chi tiết dự án:</p>
  <ul>
    <li><strong>Tên dự án:</strong> ${projectName}</li>
    <li><strong>Quản lý dự án:</strong> ${projectManager}</li>
    <li><strong>Vai trò mới:</strong> ${newRole}</li>
  </ul>
  <p>Chúng tôi mong rằng bạn sẽ tiếp tục cống hiến và đóng góp trong vai trò mới này.</p>
  <p>Trân trọng,</p>
  <p>Team dự án</p>
</body>
</html>
