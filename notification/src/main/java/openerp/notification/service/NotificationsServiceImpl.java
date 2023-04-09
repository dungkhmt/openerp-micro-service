package openerp.notification.service;

import openerp.notification.dto.NotificationDTO;
import openerp.notification.entity.Notifications;
import openerp.notification.entity.QNotifications;
import openerp.notification.repo.NotificationsRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class NotificationsServiceImpl implements NotificationsService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final NotificationsRepo notificationsRepo;

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
    public Page<NotificationDTO> getNotifications(String toUser, UUID fromId, int page, int size) {
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

        // Send new notification.
        List<SseEmitter> subscription = subscriptions.get(toUser);
        if (null != subscription) {
            send(
                    subscription,
                    SseEmitter.event()
                            .id(notification.getId().toString())
                            .name(SSE_EVENT_NEW_NOTIFICATION)
                            .data(
                                    notificationsRepo.findNotificationById(notification.getId()).toJson(),
                                    MediaType.TEXT_EVENT_STREAM)
                    // TODO: discover reconnectTime() method
            );
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
