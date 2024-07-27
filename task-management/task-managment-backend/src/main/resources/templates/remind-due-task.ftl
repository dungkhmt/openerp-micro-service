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
    
    h2 {
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
  <h2>Xin chào ${name}, bạn có nhiệm vụ sẽ đến hạn trong ngày mai</h2>
  <p>Dưới đây là danh sách nhiệm vụ sẽ đến hạn trong ngày mai:</p>
  <ul>
    <#list tasks as task>
      <li><a href="${appUrl}/project/${task.projectId}/task/${task.id}/"><strong>${task.name}</strong></a></li>
    </#list>
  </ul>
  <p>Vui lòng đăng nhập vào hệ thống quản lý để cập nhật thông tin chi tiết. Đừng quên hoàn thành nhiệm vụ của mình đúng thời hạn.</p>
  <br />
  <p>Trân trọng,</p>
  <p>OpenERP Team</p>
</body>
</html>