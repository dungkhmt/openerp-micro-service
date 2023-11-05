package com.hust.openerp.taskmanagement.service.implement;

import static com.hust.openerp.taskmanagement.entity.Notification.STATUS_CREATED;
import static com.hust.openerp.taskmanagement.entity.Notification.STATUS_READ;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hust.openerp.taskmanagement.entity.Notification;
import com.hust.openerp.taskmanagement.model.NotificationDTO;
import com.hust.openerp.taskmanagement.repository.NotificationRepository;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.nimbusds.jose.shaded.gson.Gson;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class NotificationServiceImplement implements NotificationService {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  private final NotificationRepository notificationsRepo;

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
    Pageable sortedByCreatedStampDsc = PageRequest.of(page, size, Sort.by("created_stamp").descending());
    Page<NotificationDTO> notifications = fromId == null
        ? notificationsRepo.findAllNotifications(
            toUser,
            sortedByCreatedStampDsc)
        : notificationsRepo.findNotificationsFromId(toUser, fromId, sortedByCreatedStampDsc);

    return notifications;
  }

  @Override
  public long countNumUnreadNotification(String toUser) {
    return notificationsRepo.countByToUserAndStatusId(toUser, STATUS_CREATED);
  }

  @Override
  public void create(String fromUser, String toUser, String content, String url) {
    Notification notification = new Notification();
    Gson gson = new Gson();

    notification.setFromUser(fromUser);
    notification.setToUser(toUser);
    notification.setContent(content);
    notification.setUrl(url);
    notification.setStatusId(STATUS_CREATED);
    // set now as created_stamp
    notification.setCreatedStamp(new Date());

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
                  gson.toJson(notificationsRepo.findNotificationById(notification.getId())),
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
        // try {
        // subscription.completeWithError(ignore);
        // log.info("Marked SseEmitter as complete with an error");
        // } catch (Exception completionException) {
        // log.info("Failed to mark SseEmitter as complete on error");
        // }
      }
    }));
  }

  @Override
  public void updateStatus(UUID notificationId, String status) {
    Notification notification = notificationsRepo.findById(notificationId).orElse(null);

    if (null != notification) {
      notification.setStatusId(STATUS_READ);
      notificationsRepo.save(notification);
    }
  }

  @Override
  public void updateMultipleNotificationsStatus(
      String userId,
      String status,
      Date beforeOrAt) {
    List<Notification> notifications = notificationsRepo.getNotificationsByUserIdAndStatusIdAndDateBeforeOrAt(
        userId, Notification.STATUS_CREATED, beforeOrAt);
    for (Notification notification : notifications) {
      notification.setStatusId(status);
    }
    notificationsRepo.saveAll(notifications);
  }
}
