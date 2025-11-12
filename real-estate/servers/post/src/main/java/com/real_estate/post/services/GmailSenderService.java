package com.real_estate.post.services;

import com.real_estate.post.models.AccountEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class GmailSenderService {
    @Value("${frontend.url}")
    String url;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String token, AccountEntity account) {
        try {
            String mailContent = "<p>Chào, " + account.getName() + ",</p>" +
                    "<p>Cảm ơn bạn đã đăng ký với chúng tôi,</p>" +
                    "<p>Vui lòng theo dõi liên kết bên dưới để hoàn tất đăng ký của bạn:</p>" +
                    url + "/public/account/active?token=" + token + " Xác minh email của bạn để kích hoạt tài khoản" +
                    "<p>Cảm ơn<br>Dịch vụ Đăng ký Người dùng</p>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Real Estate");
            helper.setTo(account.getEmail());
            helper.setSubject("Xác nhận email");
            helper.setText(mailContent, true); // Thiết lập tham số thứ hai là 'true' để gửi nội dung HTML

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPass(String to, String newPass) {
        String mailContent = "Mật khẩu mới: " + newPass;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Real Estate");
        message.setTo(to);
        message.setSubject("Reset password");
        message.setText(mailContent);
        javaMailSender.send(message);
    }
}
