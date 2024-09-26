package com.hust.wmsbackend.management.controller;

import com.hust.wmsbackend.management.entity.Notifications;
import com.hust.wmsbackend.management.model.notification.GetNotifications;
import com.hust.wmsbackend.management.model.notification.UpdateMultipleNotificationRequest;
import com.hust.wmsbackend.management.service.NotificationsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/notification")
@AllArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class NotificationsController {

    private NotificationsService notificationsService;

    /**
     * Subscribes SseEmitter to receive pushed events from server
     *
     * @param toUser
     * @return
     */
    @GetMapping("/subscription")
    public ResponseEntity<SseEmitter> subscribes(
            @CurrentSecurityContext(expression = "authentication.name") String toUser
    ) {
        SseEmitter subscription;
        subscription = new SseEmitter(Long.MAX_VALUE);
        Runnable callback = () -> notificationsService.subscriptions.remove(toUser);

        subscription.onTimeout(callback); // OK
        subscription.onCompletion(callback); // OK
        subscription.onError((ex) -> { // Must consider carefully, but currently OK
            log.error("onError fired with exception: " + ex);
        });

        // Add new subscription to user's connection list.
        if (notificationsService.subscriptions.containsKey(toUser)) {
            notificationsService.subscriptions.get(toUser).add(subscription);
            log.info(
                    "{} RE-SUBSCRIBES --> #CURRENT CONNECTION = {}",
                    toUser,
                    notificationsService.subscriptions.get(toUser).size());
        } else {
            notificationsService.subscriptions.put(toUser, new ArrayList<SseEmitter>() {{
                add(subscription);
            }});
            log.info("{} SUBSCRIBES", toUser);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Accel-Buffering", "no");
        responseHeaders.set("Cache-Control", "no-cache"); // may be not necessary because Nginx server set it already

        return ResponseEntity.ok().headers(responseHeaders).body(subscription);
    }

    /**
     * To keep connection alive
     */
    @Async
    @Scheduled(fixedRate = 40000)
    public void sendHeartbeatSignal() {
        long start = System.currentTimeMillis();

        notificationsService.subscriptions.forEach((toUser, subscription) -> {
            // Use iterator to avoid ConcurrentModificationException.
            ListIterator<SseEmitter> iterator = subscription.listIterator();
            int size = subscription.size();

            while (iterator.hasNext()) {
                try {
                    iterator.next().send(SseEmitter
                            .event()
                            .name(notificationsService.SSE_EVENT_HEARTBEAT)
                            .data("keep alive", MediaType.TEXT_EVENT_STREAM));
                } catch (Exception e) {
                    iterator.remove();
                    size--;
                }
            }

            if (size == 0) {
                notificationsService.subscriptions.remove(toUser);
            }
        });

    }

    /**
     * Get list of notifications with pagination
     *
     * @param toUser
     * @param page   page number, start at 0
     * @param size   page size
     */
    @GetMapping(params = {"fromId", "page", "size"})
    public ResponseEntity<?> getNotifications(
            @CurrentSecurityContext(expression = "authentication.name") String toUser,
            @RequestParam(required = false) UUID fromId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        GetNotifications om = new GetNotifications(
                notificationsService.getNotifications(toUser, fromId, page, size),
                notificationsService.countNumUnreadNotification(toUser));

        return ResponseEntity.ok().body(om);
    }

    /**
     * Update status of notifications whose id equal {@code id}.
     *
     * @param id   notification id
     * @param body request body
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        Object value = body.get("status");
        String status = null == value ? null : value.toString();

        if (!Notifications.STATUS_READ.equals(status)) {
            return ResponseEntity.badRequest().body("Invalid status");
        } else {
            notificationsService.updateStatus(id, status);
            return ResponseEntity.ok().body(null);
        }
    }

    /**
     * Update multiple notifications' status of current log in user.
     * Currently, used for marking all notifications as read.
     *
     * @param userId
     * @param body   request body
     */
    @PatchMapping("/status")
    public ResponseEntity<?> updateMultipleNotificationsStatus(
            @CurrentSecurityContext(expression = "authentication.name") String userId,
            @RequestBody UpdateMultipleNotificationRequest body
    ) {
        if (!Notifications.STATUS_READ.equals(body.getStatus())) {
            return ResponseEntity.badRequest().body("Invalid status");
        } else {
            notificationsService.updateMultipleNotificationsStatus(
                    userId,
                    Notifications.STATUS_READ,
                    body.getBeforeOrAt());
            return ResponseEntity.ok().body(null);
        }
    }

    @GetMapping("/test")
    public void test() {
        notificationsService.create("anonymous", "dungpq", "test", "/");
    }

}
