package com.hust.baseweb.applications.mail.service;

import org.springframework.mail.*;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author Le Anh Tuan
 */
public interface MailService {

    /**
     * Create simple mail message. You just need this method when you want to send multiple mails.
     *
     * @param to      receivers' mail address, should not be empty
     * @param cc      nullable
     * @param bcc     nullable
     * @param subject nullable
     * @param body    nullable
     * @param replyTo nullable
     * @return
     * @see #sendSimpleMail(String[], String[], String[], String, String, String)
     */
    SimpleMailMessage createSimpleMail(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String body,
        String replyTo
    );

    /**
     * Create simple mail message. You just need this method when you want to send multiple mails.
     *
     * @return
     * @see #sendSimpleMail(String[], String, String, String)
     */
    SimpleMailMessage createSimpleMail(String[] to, String subject, String body, String replyTo);

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    /**
     * Send simple mail message. All mail address must be valid RFC-5321 address.
     *
     * @param to      The mail address of recipients , must not be null or empty array.
     * @param cc      nullable array of valid RFC-5321 addresses.
     * @param bcc     nullable
     * @param subject nullable
     * @param replyTo may be null. When somebody hits Reply, the To field of their “reply” email will be set to the {@code replyTo} address you set in your account, rather than your From Address.
     * @throws MailParseException          in case of failure when parsing the message
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending the message
     */
    void sendSimpleMail(String[] to, String[] cc, String[] bcc, String subject, String body, String replyTo);

    /**
     * Send simple mail message. All mail address must be valid RFC-5321 address.
     *
     * @param to      The mail address of recipients , must not be null or empty array.
     * @param subject nullable
     * @param replyTo may be null. When somebody hits Reply, the To field of their “reply” email will be set to the {@code replyTo} address you set in your account, rather than your From Address.
     * @throws MailParseException          in case of failure when parsing the message
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending the message
     */
    void sendSimpleMail(String[] to, String subject, String body, String replyTo);

    /**
     * Send the given array of simple mail messages in batch.
     *
     * @param simpleMessages the messages to send
     * @throws MailParseException          in case of failure when parsing a message
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending a message
     */
    void sendMultipleSimpleMail(SimpleMailMessage... simpleMessages) throws MailException;

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    /**
     * Create mime message. You just need this method when you want to send multiple mails.
     *
     * @return
     * @throws MessagingException
     * @see #sendMailWithAttachments(String[], String[], String[], String, String, boolean, String, String, MultipartFile[])
     */
    MimeMessageHelper createMimeMessage(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String body,
        boolean isHtml,
        String replyTo,
        String replyPersonal
    ) throws MessagingException;

    /**
     * Create mime message. You just need this method when you want to send multiple mails.
     *
     * @return
     * @throws MessagingException
     * @see #createMimeMessage(String[], String[], String[], String, String, boolean, String, String)
     */
    MimeMessageHelper createMimeMessage(
        String[] to,
        String subject,
        String body,
        boolean isHtml
    ) throws MessagingException;

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    /**
     * Create mime message. You just need this method when you want to send multiple mails.
     *
     * @return
     * @throws MessagingException
     * @see #sendMailWithAttachments(String[], String[], String[], String, String, String, String, String, MultipartFile[])
     */
    MimeMessageHelper createMimeMessage(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String plainText,
        String htmlText,
        String replyTo,
        String replyPersonal
    ) throws MessagingException;

    /**
     * Create mime message. You just need this method when you want to send multiple mails.
     *
     * @return
     * @throws MessagingException
     * @see #createMimeMessage(String[], String[], String[], String, String, String, String, String)
     */
    MimeMessageHelper createMimeMessage(
        String[] to,
        String subject,
        String plainText,
        String htmlText
    ) throws MessagingException;

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    /**
     * Add attachments to the MimeMessage, taking the content from a
     * {@code org.springframework.web.multipart.MultipartFile}.
     * All {@link MultipartFile} object with no file has been chosen in multipart-form will be omitted.
     *
     * @param messageHelper
     * @param attachments
     * @throws MessagingException
     */
    void addAttachments(MimeMessageHelper messageHelper, MultipartFile[] attachments) throws MessagingException;

    /**
     * Add attachments to the MimeMessage, taking the content from a
     * {@code java.io.File}. All {@code null} file will be omitted.
     *
     * @param messageHelper
     * @param attachments
     * @throws MessagingException
     */
    void addAttachments(MimeMessageHelper messageHelper, File[] attachments) throws MessagingException;

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    /**
     * Send a JavaMail MIME message. All mail address must be valid RFC-5321 address.
     *
     * @param to            The mail address of recipients , must not be null or empty array.
     * @param cc            nullable array of valid RFC-5321 addresses.
     * @param bcc           nullable
     * @param subject       nullable
     * @param body          nullable
     * @param isHtml        whether to apply content type "text/html" for an HTML mail, using default content type ("text/plain") else
     * @param replyTo       may be null. When somebody hits Reply, the To field of their “reply” email will be set to the {@code replyTo} address you set in your account, rather than your From Address.
     * @param replyPersonal personal names that accompany mail addresses, may be null
     * @param attachments   nullable
     * @throws org.springframework.mail.MailAuthenticationException in case of authentication failure
     * @throws org.springframework.mail.MailSendException           in case of failure when sending the message
     */
    void sendMailWithAttachments(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String body,
        boolean isHtml,
        String replyTo,
        String replyPersonal,
        MultipartFile[] attachments
    );

    /**
     * Send a JavaMail MIME message. All mail address must be valid RFC-5321 address.
     *
     * @param to          The mail address of recipients , must not be null or empty array.
     * @param subject     nullable
     * @param body        nullable
     * @param isHtml      whether to apply content type "text/html" for an HTML mail, using default content type ("text/plain") else
     * @param attachments nullable
     * @throws org.springframework.mail.MailAuthenticationException in case of authentication failure
     * @throws org.springframework.mail.MailSendException           in case of failure when sending the message
     */
    void sendMailWithAttachments(
        String[] to,
        String subject,
        String body,
        boolean isHtml,
        MultipartFile[] attachments
    );

    /**
     * Send a JavaMail MIME message. All mail address must be valid RFC-5321 address.
     *
     * @param to            The mail address of recipients , must not be null or empty array.
     * @param cc            nullable array of valid RFC-5321 addresses.
     * @param bcc           nullable
     * @param subject       nullable
     * @param plainText     the plain text for the message, may be {@code null}
     * @param htmlText      the HTML text for the message, may be {@code null}
     * @param replyTo       may be null. When somebody hits Reply, the To field of their “reply” email will be set to the {@code replyTo} address you set in your account, rather than your From Address.
     * @param replyPersonal personal names that accompany mail addresses, may be {@code null}
     * @param attachments   nullable
     * @throws org.springframework.mail.MailAuthenticationException in case of authentication failure
     * @throws org.springframework.mail.MailSendException           in case of failure when sending the message
     */
    void sendMailWithAttachments(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String plainText,
        String htmlText,
        String replyTo,
        String replyPersonal,
        MultipartFile[] attachments
    );

    /**
     * Send a JavaMail MIME message. All mail address must be valid RFC-5321 address.
     *
     * @param to          The mail address of recipients , must not be null or empty array.
     * @param subject     nullable
     * @param plainText   the plain text for the message, may be {@code null}
     * @param htmlText    the HTML text for the message, may be {@code null}
     * @param attachments nullable
     * @throws org.springframework.mail.MailAuthenticationException in case of authentication failure
     * @throws org.springframework.mail.MailSendException           in case of failure when sending the message
     */
    void sendMailWithAttachments(
        String[] to,
        String subject,
        String plainText,
        String htmlText,
        MultipartFile[] attachments
    );

    /*
     -------------------------------------------------------------------------------------
     -------------------------------------------------------------------------------------
    */

    /**
     * Send a JavaMail MIME message. All mail address must be valid RFC-5321 address.
     *
     * @param to            The mail address of recipients , must not be null or empty array.
     * @param cc            nullable array of valid RFC-5321 addresses.
     * @param bcc           nullable
     * @param subject       nullable
     * @param body          nullable
     * @param isHtml        whether to apply content type "text/html" for an HTML mail, using default content type ("text/plain") else
     * @param replyTo       may be null. When somebody hits Reply, the To field of their “reply” email will be set to the {@code replyTo} address you set in your account, rather than your From Address.
     * @param replyPersonal personal names that accompany mail addresses, may be null
     * @param attachments   nullable
     * @throws org.springframework.mail.MailAuthenticationException in case of authentication failure
     * @throws org.springframework.mail.MailSendException           in case of failure when sending the message
     */
    void sendMailWithAttachments(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String body,
        boolean isHtml,
        String replyTo,
        String replyPersonal,
        File[] attachments
    );

    /**
     * Send a JavaMail MIME message. All mail address must be valid RFC-5321 address.
     *
     * @param to          The mail address of recipients , must not be null or empty array.
     * @param subject     nullable
     * @param body        nullable
     * @param isHtml      whether to apply content type "text/html" for an HTML mail, using default content type ("text/plain") else
     * @param attachments nullable
     * @throws org.springframework.mail.MailAuthenticationException in case of authentication failure
     * @throws org.springframework.mail.MailSendException           in case of failure when sending the message
     */
    void sendMailWithAttachments(
        String[] to,
        String subject,
        String body,
        boolean isHtml,
        File[] attachments
    );

    /**
     * Send a JavaMail MIME message. All mail address must be valid RFC-5321 address.
     *
     * @param to            The mail address of recipients , must not be null or empty array.
     * @param cc            nullable array of valid RFC-5321 addresses.
     * @param bcc           nullable
     * @param subject       nullable
     * @param plainText     the plain text for the message, may be {@code null}
     * @param htmlText      the HTML text for the message, may be {@code null}
     * @param replyTo       may be null. When somebody hits Reply, the To field of their “reply” email will be set to the {@code replyTo} address you set in your account, rather than your From Address.
     * @param replyPersonal personal names that accompany mail addresses, may be {@code null}
     * @param attachments   nullable
     * @throws org.springframework.mail.MailAuthenticationException in case of authentication failure
     * @throws org.springframework.mail.MailSendException           in case of failure when sending the message
     */
    void sendMailWithAttachments(
        String[] to,
        String[] cc,
        String[] bcc,
        String subject,
        String plainText,
        String htmlText,
        String replyTo,
        String replyPersonal,
        File[] attachments
    );

    /**
     * Send a JavaMail MIME message. All mail address must be valid RFC-5321 address.
     *
     * @param to          The mail address of recipients , must not be null or empty array.
     * @param subject     nullable
     * @param plainText   the plain text for the message, may be {@code null}
     * @param htmlText    the HTML text for the message, may be {@code null}
     * @param attachments nullable
     * @throws org.springframework.mail.MailAuthenticationException in case of authentication failure
     * @throws org.springframework.mail.MailSendException           in case of failure when sending the message
     */
    void sendMailWithAttachments(
        String[] to,
        String subject,
        String plainText,
        String htmlText,
        File[] attachments
    );

    /**
     * Send the given mime messages in batch.
     *
     * @param mimeMessages the messages to send
     * @throws MailParseException          in case of failure when parsing a message
     * @throws MailAuthenticationException in case of authentication failure
     * @throws MailSendException           in case of failure when sending a message
     */
    void sendMultipleMimeMessages(MimeMessage... mimeMessages) throws MailException;
}
