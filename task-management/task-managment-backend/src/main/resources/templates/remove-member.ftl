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
  <h1>Thông báo Xóa khỏi Dự án</h1>
  <p>Xin chào ${memberFirstName} ${memberLastName},</p>
  <p>Bạn đã bị xóa khỏi dự án "${projectName}".</p>
  <p>Dưới đây là chi tiết dự án:</p>
  <ul>
    <li><strong>Tên dự án:</strong> ${projectName}</li>
    <li><strong>Người quản lý dự án:</strong> ${projectManager}</li>
  </ul>
  <p>Chúng tôi cảm ơn những đóng góp của bạn trong thời gian qua.</p>
  <p>Trân trọng,</p>
  <p>Team dự án</p>
</body>
</html>
