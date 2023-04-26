package openerp.notification.service;

import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.notification.dto.NotificationProjection;
import openerp.notification.dto.rabbitMessage.*;
import openerp.notification.entity.Notifications;
import openerp.notification.entity.QNotifications;
import openerp.notification.repo.NotificationsRepo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static openerp.notification.config.rabbitmq.RabbitConfig.NOTIFICATION_HEADERS_EXCHANGE;


@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class NotificationsServiceImpl implements NotificationsService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final NotificationsRepo notificationsRepo;

    private RabbitTemplate template;

    private MessageConverter messageConverter;

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
    public Page<NotificationProjection> getNotifications(String toUser, UUID fromId, int page, int size) {
        Pageable sortedByCreatedStampDsc =
                PageRequest.of(page, size, Sort.by("created_stamp").descending());

        return fromId == null
                ? notificationsRepo.findAllNotifications(toUser, sortedByCreatedStampDsc)
                : notificationsRepo.findNotificationsFromId(toUser, fromId, sortedByCreatedStampDsc);
    }

    @Override
    public long countNumUnreadNotification(String toUser) {
        return notificationsRepo.countByToUserAndStatusId(toUser, Notifications.STATUS_CREATED);
    }

    @Override
    public void create(String fromUser, String toUser, String content, String url) {
        Notifications notification = new Notifications();

        notification.setFromUser(fromUser);
        notification.setToUser(toUser);
        notification.setContent(content);
        notification.setUrl(url);
        notification.setStatusId(Notifications.STATUS_CREATED);

        notification = notificationsRepo.save(notification);

        // Publish message to queue
        NotificationMessage message = new NotificationMessage();

        message.setUser(new User(toUser));
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
    public void onMessage(Message message) {
        NotificationMessage notificationMessage = (NotificationMessage) messageConverter.fromMessage(message);

        // Send new notification.
        List<SseEmitter> subscription = subscriptions.get(notificationMessage.getUser().getId());
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
            Date beforeOrAt
    ) {
        QNotifications qNotifications = QNotifications.notifications;
        BooleanExpression unRead = qNotifications.statusId.eq(Notifications.STATUS_CREATED);
        BooleanExpression toUser = qNotifications.toUser.eq(userId);
        BooleanExpression beforeOrAtTime = qNotifications.createdStamp.loe(beforeOrAt);

        Iterable<Notifications> notifications = notificationsRepo.findAll(toUser.and(unRead).and(beforeOrAtTime));

        // TODO: upgrade this method to check valid status according to notification status transition.
        for (Notifications notification : notifications) {
            notification.setStatusId(status);
        }

        notificationsRepo.saveAll(notifications);
    }
}
