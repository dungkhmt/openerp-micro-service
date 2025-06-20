package openerp.notification.service;

import static openerp.notification.config.rabbitmq.RabbitConfig.NOTIFICATION_HEADERS_EXCHANGE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.notification.dto.NotificationProjection;
import openerp.notification.dto.rabbitMessage.Channel;
import openerp.notification.dto.rabbitMessage.InAppChannel;
import openerp.notification.dto.rabbitMessage.NotificationMessage;
import openerp.notification.dto.rabbitMessage.OperationType;
import openerp.notification.entity.Notifications;
import openerp.notification.entity.QNotifications;
import openerp.notification.repo.NotificationsRepo;


@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class NotificationsServiceImpl implements NotificationsService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final NotificationsRepo notificationsRepo;

    private RabbitTemplate template;

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.info(e.toString());
            }
        }));
    }

    @Override
    public Page<NotificationProjection> getNotifications(String toUser, UUID fromId, int page, int size, String orgCode) {
        Pageable sortedByCreatedStampDsc =
                PageRequest.of(page, size, Sort.by("created_stamp").descending());

        return fromId == null
                ? notificationsRepo.findAllNotifications(toUser, orgCode, sortedByCreatedStampDsc)
                : notificationsRepo.findNotificationsFromId(toUser, fromId, orgCode, sortedByCreatedStampDsc);
    }

    @Override
    public long countNumUnreadNotification(String toUser, String orgCode) {
        return notificationsRepo.countByToUserAndStatusIdAndOrganizationCode(toUser,
                Notifications.STATUS_CREATED, orgCode);
    }

    @Override
    public void create(String fromUser, String toUser, String content, String url, String orgCode) {
        Notifications notification = new Notifications();

        notification.setFromUser(fromUser);
        notification.setToUser(toUser);
        notification.setContent(content);
        notification.setUrl(url);
        notification.setStatusId(Notifications.STATUS_CREATED);

        notification = notificationsRepo.save(notification);

        // Publish message to queue
        NotificationMessage message = new NotificationMessage();

        message.setUserId(toUser);
        message.setOrganizationCode(orgCode);
        InAppChannel inApp = new InAppChannel(List.of(notificationsRepo.findNotificationById(notification.getId()).toDTO()), OperationType.CREATE);
        message.setChannels(new Channel(inApp, null));

        publishMessage(message);
    }

    private void publishMessage(NotificationMessage message) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("channels.inApp", message.getChannels().getInApp() != null);
        headers.put("channels.email", message.getChannels().getEmail() != null);

        template.convertAndSend(NOTIFICATION_HEADERS_EXCHANGE, "", message, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().getHeaders().putAll(headers);
            return messagePostProcessor;
        });
    }

    @RabbitListener(queues = "#{queueInApp.name}")
    public void onMessage(NotificationMessage notificationMessage) {
        if (notificationMessage == null || notificationMessage.getUserId() == null ||
                notificationMessage.getOrganizationCode() == null) {
            log.error("Received invalid notification message: userId or organizationCode is null");
            return;
        }

        log.info(
                "Received message for userId: {}, organizationCode: {}",
                notificationMessage.getUserId(),
                notificationMessage.getOrganizationCode()
        );

        // Send new notification.
        String subscriptionKey = notificationMessage.getUserId() + ":" + notificationMessage.getOrganizationCode();
        List<SseEmitter> subscription = subscriptions.get(subscriptionKey);
        if (null != subscription) {
            notificationMessage.getChannels().getInApp().getNotifications().forEach(notification -> send(
                    subscription,
                    SseEmitter.event()
                            .id(notification.getId())
                            .name(SSE_EVENT_NEW_NOTIFICATION)
                            .data(
                                    new Gson().toJson(notification),
                                    MediaType.TEXT_EVENT_STREAM)
                    // TODO: discover reconnectTime() method
            ));
        }
    }

    private void send(List<SseEmitter> subscriptions, SseEmitter.SseEventBuilder event) {
        executor.execute(() -> subscriptions.forEach(subscription -> {
                    try {
                        subscription.send(event);
                    } catch (Exception ignore) {
                        // This is normal behavior when a client disconnects.
                        // onError callback will be automatically fired.
//                                                             try {
//                                                                 subscription.completeWithError(ignore);
//                                                                 log.info("Marked SseEmitter as complete with an error");
//                                                             } catch (Exception completionException) {
//                                                                 log.info("Failed to mark SseEmitter as complete on error");
//                                                             }
                    }
                }
        ));
    }

    @Override
    public void updateStatus(UUID notificationId, String status) {
        Notifications notification = notificationsRepo.findById(notificationId).orElse(null);

        if (null != notification) {
            notification.setStatusId(Notifications.STATUS_READ);
            notificationsRepo.save(notification);
        }
    }

    @Override
    public void updateMultipleNotificationsStatus(
            String userId,
            String status,
            Date beforeOrAt,
            String orgCode
    ) {
        QNotifications qNotifications = QNotifications.notifications;
        BooleanExpression unRead = qNotifications.statusId.eq(Notifications.STATUS_CREATED);
        BooleanExpression toUser = qNotifications.toUser.eq(userId);
        BooleanExpression beforeOrAtTime = qNotifications.createdStamp.loe(beforeOrAt);
        BooleanExpression organizationCode = qNotifications.organizationCode.eq(orgCode);

        Iterable<Notifications> notifications = notificationsRepo.findAll(toUser.and(unRead).
                and(beforeOrAtTime).and(organizationCode));

        // TODO: upgrade this method to check valid status according to notification status transition.
        for (Notifications notification : notifications) {
            notification.setStatusId(status);
        }

        notificationsRepo.saveAll(notifications);
    }
}
