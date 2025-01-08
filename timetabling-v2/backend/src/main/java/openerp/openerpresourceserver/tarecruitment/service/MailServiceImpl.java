package openerp.openerpresourceserver.tarecruitment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class MailServiceImpl implements MailService{

    private JavaMailSender javaMailSender;
    @Override
    @Async
    public void sendingEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bachspham@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            log.info("Email sent");
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
