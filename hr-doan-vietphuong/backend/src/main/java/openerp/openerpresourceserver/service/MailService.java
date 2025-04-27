package openerp.openerpresourceserver.service;

import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;

import java.util.List;

public interface MailService {
    void sendEmailWithHtmlTemplate(String recipient, List<String> ccList, String subject, String templateName, Context context) throws MessagingException;
}