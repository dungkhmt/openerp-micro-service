package com.hust.openerp.taskmanagement.consumer;

import static com.hust.openerp.taskmanagement.service.NotificationService.SSE_EVENT_NEW_NOTIFICATION;
import static com.hust.openerp.taskmanagement.service.NotificationService.subscriptions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hust.openerp.taskmanagement.dto.NotificationMessage;
import com.nimbusds.jose.shaded.gson.Gson;

public class InAppNotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(InAppNotificationConsumer.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private MessageConverter messageConverter;

    public InAppNotificationConsumer(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
        init();
    }

    private void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }));
    }

    @RabbitListener(queues = "#{queueInApp.name}")
    public void onMessage(Message message) {
        NotificationMessage notificationMessage = (NotificationMessage) messageConverter.fromMessage(message);

        // Send new notification.
        List<SseEmitter> subscription = subscriptions.get(notificationMessage.getUserId());
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
                // try {
                // subscription.completeWithError(ignore);
                // log.info("Marked SseEmitter as complete with an error");
                // } catch (Exception completionException) {
                // log.info("Failed to mark SseEmitter as complete on error");
                // }
            }
        }));
    }
}
