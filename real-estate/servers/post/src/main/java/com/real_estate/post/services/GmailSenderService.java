package com.real_estate.post.services;

import com.real_estate.post.models.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class GmailSenderService {
    String url = "http://localhost:2805/public/account/active?token=";
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String token, AccountEntity account) {
        String mailContent = "<p> Hi, " + account.getName() + ", </p>" +
                "<p>Thank you for registering with us," + "" +
                "Please, follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + token + "\">Verify your email to activate your account</a>" +
                "<p> Thank you <br> Users Registration Portal Service";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vykroos285@gmail.com");
        message.setTo(account.getEmail());
        message.setSubject("Xác nhận gmail");
        message.setText(mailContent);
        javaMailSender.send(message);
    }

    public void sendPass(String to, String newPass) {
        String mailContent = "New password of you: " + newPass;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vykroos285@gmail.com");
        message.setTo(to);
        message.setSubject("Reset password");
        message.setText(mailContent);
        javaMailSender.send(message);
    }
}
