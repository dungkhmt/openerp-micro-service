package com.hust.openerp.taskmanagement.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Page;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hust.openerp.taskmanagement.dto.NotificationProjection;

import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;

public interface NotificationService {

    String SSE_EVENT_HEARTBEAT = "HEARTBEAT";

    String SSE_EVENT_NEW_NOTIFICATION = "NEW_NOTIFICATION";

    // Use concurrent instead of synchronized collection because of performance and
    // thread-safe
    ConcurrentHashMap<String, List<SseEmitter>> subscriptions = new ConcurrentHashMap<>();

    Page<NotificationProjection> getNotifications(String toUser, UUID fromId, int page, int size);

    long countNumUnreadNotification(String toUser);

    void createInAppNotification(String fromUser, String toUser, String content, String url);

    void createMailNotification(String fromEmail, String toEmail, String subject, String content, boolean isHtml);

    void createMailNotification(String fromEmail, String toEmail, String subject, String templateName,
            Map<String, Object> model)
            throws IOException, TemplateException, MessagingException;

    void updateStatus(UUID notificationId, String status);

    void updateMultipleNotificationsStatus(String userId, String status, Date beforeOrAt);
}
