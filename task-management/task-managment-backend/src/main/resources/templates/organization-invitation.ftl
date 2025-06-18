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

        a.button {
            display: inline-block;
            background-color: #4CAF50;
            color: white;
            padding: 10px 18px;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 12px;
        }
    </style>
</head>
<body>
<h2>Lời mời tham gia tổ chức</h2>
<p>Chào ${userEmail},</p>
<p>Bạn vừa được <strong>${inviterName}</strong> mời tham gia tổ chức <strong>${organizationName}</strong> với vai trò <strong>${roleName}</strong>.</p>

<p>Vui lòng đăng nhập vào hệ thống hoặc nhấn vào <a href="${link}">link này</a> để xem chi tiết.</p>

<p>Lưu ý: Lời mời này sẽ hết hạn vào <strong>${expirationDate}</strong>.</p>

<p>Trân trọng,</p>
<p>Team dự án</p>
</body>
</html>
