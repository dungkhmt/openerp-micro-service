package com.hust.wmsbackend.management.service.impl;

import com.google.gson.Gson;
import com.hust.wmsbackend.management.config.RabbitConfig;
import com.hust.wmsbackend.management.entity.Notifications;
import com.hust.wmsbackend.management.entity.User;
import com.hust.wmsbackend.management.model.NotificationProjection;
import com.hust.wmsbackend.management.model.notification.Channel;
import com.hust.wmsbackend.management.model.notification.InAppChannel;
import com.hust.wmsbackend.management.model.notification.NotificationMessage;
import com.hust.wmsbackend.management.model.notification.OperationType;
import com.hust.wmsbackend.management.repository.NotificationsRepository;
import com.hust.wmsbackend.management.service.NotificationsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class NotificationsServiceImpl implements NotificationsService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final NotificationsRepository notificationsRepo;

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

        message.setUser(User.builder().id(toUser).build());
        InAppChannel inApp = new InAppChannel(
                Collections.singletonList(notificationsRepo.findNotificationById(notification.getId()).toDTO()),
                OperationType.CREATE);
        message.setChannels(Channel.builder().inApp(inApp).build());

        publishMessage(message);
    }

    private void publishMessage(NotificationMessage message) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("channels.inApp", message.getChannels().getInApp() != null);
        headers.put("channels.email", message.getChannels().getEmail() != null);

        template.convertAndSend(RabbitConfig.NOTIFICATION_HEADERS_EXCHANGE, "", message, messagePostProcessor -> {
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
    @Transactional
    public void updateMultipleNotificationsStatus(String userId, String status, Date beforeOrAt) {
        List<Notifications> notifications = notificationsRepo.getNotificationsByUserIdAndStatusIdAndDateBeforeOrAt(
                userId, Notifications.STATUS_CREATED, beforeOrAt);
        for (Notifications notification : notifications) {
            notification.setStatusId(status);
        }
        notificationsRepo.saveAll(notifications);
    }
}
