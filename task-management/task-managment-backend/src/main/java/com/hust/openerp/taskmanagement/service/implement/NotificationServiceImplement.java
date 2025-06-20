package com.hust.openerp.taskmanagement.service.implement;

import static com.hust.openerp.taskmanagement.config.rabbitmq.RabbitMqConfig.NOTIFICATION_HEADERS_EXCHANGE;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.openerp.taskmanagement.dto.NotificationMessage;
import com.hust.openerp.taskmanagement.dto.NotificationProjection;
import com.hust.openerp.taskmanagement.dto.rabbitmq.Channel;
import com.hust.openerp.taskmanagement.dto.rabbitmq.InAppChannel;
import com.hust.openerp.taskmanagement.dto.rabbitmq.MailChannel;
import com.hust.openerp.taskmanagement.dto.rabbitmq.OperationType;
import com.hust.openerp.taskmanagement.entity.Notification;
import com.hust.openerp.taskmanagement.repository.NotificationRepository;
import com.hust.openerp.taskmanagement.service.NotificationService;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class NotificationServiceImplement implements NotificationService {

    private final NotificationRepository notificationRepo;

    private final FreeMarkerConfigurer freeMarkerConfigurer;

    private RabbitTemplate template;

    @Override
    public Page<NotificationProjection> getNotifications(String toUser, UUID fromId, int page, int size) {
        Pageable sortedByCreatedStampDsc = PageRequest.of(page, size,
                Sort.by(fromId == null ? "createdStamp" : "created_stamp").descending());

        return fromId == null
                ? notificationRepo.findAllNotifications(toUser, sortedByCreatedStampDsc)
                : notificationRepo.findNotificationsFromId(toUser, fromId, sortedByCreatedStampDsc);
    }

    @Override
    public long countNumUnreadNotification(String toUser) {
        return notificationRepo.countByToUserAndStatusId(toUser, Notification.STATUS_CREATED);
    }

    @Override
    public void createInAppNotification(String fromUser, String toUser, String content, String url) {
        Notification notification = new Notification();

        Date createdStamp = new Date();

        notification.setFromUser(fromUser);
        notification.setToUser(toUser);
        notification.setContent(content);
        notification.setUrl(url);
        notification.setStatusId(Notification.STATUS_CREATED);
        notification.setCreatedStamp(createdStamp);
        notification.setLastUpdatedStamp(createdStamp);

        notification = notificationRepo.save(notification);

        // Publish message to queue
        NotificationMessage message = new NotificationMessage();

        message.setUserId(toUser);
        InAppChannel inApp = new InAppChannel(
                List.of(notificationRepo.findNotificationById(notification.getId()).toDTO()), OperationType.CREATE);
        message.setChannels(new Channel(inApp, null));

        publishMessage(message);
    }

    private void publishMessage(NotificationMessage message) {
        message.setOrganizationCode(OrganizationContext.getCurrentOrganizationCode());
        
        Map<String, Object> headers = new HashMap<>();
        headers.put("channels.inApp", message.getChannels().getInApp() != null);
        headers.put("channels.email", message.getChannels().getEmail() != null);

        template.convertAndSend(NOTIFICATION_HEADERS_EXCHANGE, "", message, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().getHeaders().putAll(headers);
            return messagePostProcessor;
        });
    }

    @Override
    public void updateStatus(UUID notificationId, String status) {
        Notification notification = notificationRepo.findById(notificationId).orElse(null);

        if (null != notification) {
            notification.setStatusId(Notification.STATUS_READ);
            notificationRepo.save(notification);
        }
    }

    @Override
    public void updateMultipleNotificationsStatus(
            String userId,
            String status,
            Date beforeOrAt) {
        Iterable<Notification> notifications = notificationRepo.findUnreadAndBeforeOrAtTime(userId, beforeOrAt);

        // TODO: upgrade this method to check valid status according to notification
        // status transition.
        for (Notification notification : notifications) {
            notification.setStatusId(status);
        }

        notificationRepo.saveAll(notifications);
    }

    @Override
    public void createMailNotification(String fromMail, String toMail, String subject, String content, boolean isHtml) {
        // Publish message to queue
        NotificationMessage message = new NotificationMessage();

        message.setUserId(fromMail);
        MailChannel mailChannel = MailChannel.builder().to(new String[] { toMail }).cc(new String[] { fromMail })
                .subject(subject).body(content)
                .isHtml(isHtml)
                .build();
        message.setChannels(new Channel(null, mailChannel));

        publishMessage(message);
    }

    @Override
    public void createMailNotification(String fromMail, String toMail, String subject, String templateName,
            Map<String, Object> model)
            throws IOException, TemplateException, MessagingException {
        Template freeMarkerTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(templateName + ".ftl");
        String body = FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerTemplate, model);

        createMailNotification(fromMail, toMail, subject, body, true);
    }
}
