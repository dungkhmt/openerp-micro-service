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
  <h2>Bạn đã được thêm vào một cuộc họp mới</h2>
  <p>Chào ${userName},</p>
  <p>Bạn vừa được thêm vào cuộc họp với thông tin chi tiết như sau:</p>
  <ul>
    <li><strong>Tên cuộc họp:</strong> ${meetingName}</li>
    <li><strong>Người tạo:</strong> ${meetingCreator}</li>
    <li><strong>Mô tả:</strong> ${meetingDescription}</li>
    <li><strong>Địa điểm:</strong> ${location}</li>
    <li><strong>Hạn đăng ký:</strong> ${registrationDeadline}</li>
  </ul>
  <p>Vui lòng đăng nhập vào hệ thống hoặc nhấn vào <a href="${meetingLink}">link này</a> để xem chi tiết.</p>
  <p>Trân trọng,</p>
  <p>Team dự án</p>
</body>
</html>
