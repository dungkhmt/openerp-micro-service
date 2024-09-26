package com.hust.baseweb.applications.mail.service;

import com.hust.baseweb.config.MailProperties;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Le Anh Tuan
 */
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MailServiceImpl implements MailService {

    private JavaMailSender mailSender;

    private MailProperties properties;

    private Configuration config;


    public SimpleMailMessage createSimpleMail(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String body,
        String replyTo
    ) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(properties.getUsername());
        mailMessage.setTo(to);
        mailMessage.setCc(cc);
        mailMessage.setBcc(bcc);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailMessage.setSentDate(new Date());
        mailMessage.setReplyTo(replyTo);

        return mailMessage;
    }

    public SimpleMailMessage createSimpleMail(String[] to, String subject, String body, String replyTo) {
        return createSimpleMail(to, null, null, subject, body, replyTo);
    }

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    // TODO: test
    public void sendSimpleMail(String[] to, String[] cc, String[] bcc, String subject, String body, String replyTo) {
        mailSender.send(createSimpleMail(to, cc, bcc, subject, body, replyTo));
    }

    public void sendSimpleMail(String[] to, String subject, String body, String replyTo) {
        sendSimpleMail(to, null, null, subject, body, replyTo);
    }

    // TODO: test
    @Override
    public void sendMultipleSimpleMail(SimpleMailMessage... simpleMessages) throws MailException {
        mailSender.send(simpleMessages);
    }

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    public MimeMessageHelper createMimeMessage(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String body,
        boolean isHtml,
        String replyTo,
        String replyPersonal
    ) throws MessagingException {
        MimeMessageHelper messageHelper = initMimeMessage(to, cc, bcc, subject, replyTo, replyPersonal);

        messageHelper.setText(body, isHtml);

//        messageHelper.setPriority(10);
//        messageHelper.setValidateAddresses(true);
//        ConfigurableMimeFileTypeMap configurableMimeFileTypeMap = new ConfigurableMimeFileTypeMap();
//        messageHelper.setFileTypeMap(configurableMimeFileTypeMap);

        return messageHelper;
    }

    public MimeMessageHelper createMimeMessage(
        String[] to,
        String subject,
        String body,
        boolean isHtml
    ) throws MessagingException {
        return createMimeMessage(to, null, null, subject, body, isHtml, null, null);
    }

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    public MimeMessageHelper createMimeMessage(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String plainText,
        String htmlText,
        String replyTo,
        String replyPersonal
    ) throws MessagingException {
        MimeMessageHelper messageHelper = initMimeMessage(to, cc, bcc, subject, replyTo, replyPersonal);

        messageHelper.setText(plainText, htmlText);
        return messageHelper;
    }

    public MimeMessageHelper createMimeMessage(
        String[] to,
        String subject,
        String plainText,
        String htmlText
    ) throws MessagingException {
        return createMimeMessage(to, null, null, subject, plainText, htmlText, null, null);
    }

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    public void addAttachments(MimeMessageHelper messageHelper, MultipartFile[] attachments) throws MessagingException {
        if (null != attachments) {
            for (MultipartFile attachment : attachments) {
                if (null != attachment && null != attachment.getContentType()) {
                    // Note: name or content type == null will cause exception.
                    messageHelper.addAttachment(
                        null != attachment.getOriginalFilename() ? attachment.getOriginalFilename() : "file",
                        attachment,
                        attachment.getContentType());
                }
            }
        }
    }

    public void addAttachments(MimeMessageHelper messageHelper, File[] attachments) throws MessagingException {
        if (null != attachments) {
            for (File attachment : attachments) {
                if (null != attachment) {
                    messageHelper.addAttachment(
                        attachment.getName().equals("") ? "file" : attachment.getName(),
                        attachment);
                }
            }
        }
    }

    /**
     * Init MimeMessage with all basic information. All mail address must be valid RFC-5321 address.
     *
     * @param to            must not be null or empty array.
     * @param cc            nullable
     * @param bcc           nullable
     * @param subject       nullable
     * @param replyTo       nullable
     * @param replyPersonal personal names that accompany mail addresses, may be null
     * @throws MessagingException
     */
    private MimeMessageHelper initMimeMessage(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String replyTo,
        String replyPersonal
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            messageHelper.setFrom(properties.getUsername(), "Open ERP"); // Personal names that accompany mail addresses

            // Only require not blank.
            if (StringUtils.isNotBlank(replyTo)) {
                if (StringUtils.isNotBlank(replyPersonal)) {
                    messageHelper.setReplyTo(replyTo, replyPersonal);
                } else {
                    messageHelper.setReplyTo(replyTo);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        messageHelper.setTo(Arrays.stream(to).filter(StringUtils::isNotBlank).toArray(String[]::new));

        if (null != cc && cc.length > 0) {
            messageHelper.setCc(Arrays.stream(cc).filter(StringUtils::isNotBlank).toArray(String[]::new));
        }

        if (null != bcc && bcc.length > 0) {
            messageHelper.setBcc(Arrays.stream(bcc).filter(StringUtils::isNotBlank).toArray(String[]::new));
        }

        messageHelper.setSubject(subject);
        messageHelper.setSentDate(new Date());

        return messageHelper;
    }

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    // TODO: test
    public void sendMailWithAttachments(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String body,
        boolean isHtml,
        String replyTo,
        String replyPersonal,
        MultipartFile[] attachments
    ) {
        try {
            MimeMessageHelper messageHelper = createMimeMessage(
                to,
                cc,
                bcc,
                subject,
                body,
                isHtml,
                replyTo,
                replyPersonal);

            addAttachments(messageHelper, attachments);
            mailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMailWithAttachments(
        String[] to,
        String subject,
        String body,
        boolean isHtml,
        MultipartFile[] attachments
    ) {
        sendMailWithAttachments(to, null, null, subject, body, isHtml, null, null, attachments);
    }

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    // TODO: test
    public void sendMailWithAttachments(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String plainText,
        String htmlText,
        String replyTo,
        String replyPersonal,
        MultipartFile[] attachments
    ) {
        try {
            MimeMessageHelper messageHelper = createMimeMessage(
                to,
                cc,
                bcc,
                subject,
                plainText,
                htmlText,
                replyTo,
                replyPersonal);

            addAttachments(messageHelper, attachments);
            mailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMailWithAttachments(
        String[] to,
        String subject,
        String plainText,
        String htmlText,
        MultipartFile[] attachments
    ) {
        sendMailWithAttachments(to, null, null, subject, plainText, htmlText, null, null, attachments);
    }

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    // TODO: test
    public void sendMailWithAttachments(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String body,
        boolean isHtml,
        String replyTo,
        String replyPersonal,
        File[] attachments
    ) {
        try {
            MimeMessageHelper messageHelper = createMimeMessage(
                to,
                cc,
                bcc,
                subject,
                body,
                isHtml,
                replyTo,
                replyPersonal);

            addAttachments(messageHelper, attachments);
            mailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMailWithAttachments(String[] to, String subject, String body, boolean isHtml, File[] attachments) {
        sendMailWithAttachments(to, null, null, subject, body, isHtml, null, null, attachments);
    }

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    // TODO: test
    public void sendMailWithAttachments(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String plainText,
        String htmlText,
        String replyTo,
        String replyPersonal,
        File[] attachments
    ) {
        try {
            MimeMessageHelper messageHelper = createMimeMessage(
                to,
                cc,
                bcc,
                subject,
                plainText,
                htmlText,
                replyTo,
                replyPersonal);

            addAttachments(messageHelper, attachments);
            mailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMailWithAttachments(
        String[] to,
        String subject,
        String plainText,
        String htmlText,
        File[] attachments
    ) {
        sendMailWithAttachments(to, null, null, subject, plainText, htmlText, null, null, attachments);
    }

    @Override
    public void sendMultipleMimeMessages(MimeMessage... mimeMessages) throws MailException {
        mailSender.send(mimeMessages);
    }
}
