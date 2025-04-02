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
  <h2>Bạn đã được phân công phiên họp</h2>
  <p>Chào ${userName},</p>
  <p>Bạn vừa được phân công vào phiên họp trong cuộc họp <strong>${meetingName}</strong> tạo bởi ${meetingCreator} với thông tin chi tiết như sau:</p>
   <ul>
    <li><strong>Thời gian:</strong> ${assignedSession}</li>
    <li><strong>Địa điểm:</strong> ${location}</li>
    <li><strong>Mô tả:</strong> ${meetingDescription}</li>
  </ul>
  <p>Vui lòng đăng nhập vào hệ thống hoặc nhấn vào <a href="${meetingLink}">link này</a> để xem chi tiết.</p>
  <p>Trân trọng,</p>
  <p>Team dự án</p> 
</body>
</html>
