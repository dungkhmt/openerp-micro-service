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
      color: #d9534f;
    }

    ul {
      list-style-type: disc;
      margin-left: 20px;
    }
  </style>
</head>
<body>
  <h2>Thông báo đóng đăng ký cuộc họp</h2>
  <p>Chào ${meetingCreator},</p>
  <p>Hệ thống xin thông báo rằng thời hạn đăng ký cho cuộc họp <strong>${meetingName}</strong> đã kết thúc.</p>
  <ul>
   	<li><strong>Mô tả:</strong> ${meetingDescription}</li>
    <li><strong>Địa điểm:</strong> ${location}</li>
    <li><strong>Hạn đăng ký:</strong> ${registrationDeadline}</li>
  </ul>
  <p>Vui lòng đăng nhập vào hệ thống hoặc nhấn vào <a href="${meetingLink}">link này</a> để xem chi tiết.</p>
  <p>Trân trọng,</p>
  <p>Team dự án</p>
</body>
</html>
