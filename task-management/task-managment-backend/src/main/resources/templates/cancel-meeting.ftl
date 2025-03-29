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
  <h2>Thông báo hủy cuộc họp</h2>
  <p>Chào ${userName},</p>
  <p>Chúng tôi xin thông báo rằng cuộc họp <strong>${meetingName}</strong> đã bị hủy.</p>
  <ul>
    <li><strong>Người tạo:</strong> ${meetingCreator}</li>
    <li><strong>Mô tả:</strong> ${meetingDescription}</li>
    <li><strong>Địa điểm:</strong> ${location}</li>
  </ul>
  <p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với người tạo cuộc họp.</p>
  <p>Trân trọng,</p>
  <p>Team dự án</p>
</body>
</html>
