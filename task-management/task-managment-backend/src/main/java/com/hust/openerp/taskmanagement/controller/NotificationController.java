package com.hust.openerp.taskmanagement.controller;

import static com.hust.openerp.taskmanagement.service.NotificationService.SSE_EVENT_HEARTBEAT;
import static com.hust.openerp.taskmanagement.service.NotificationService.subscriptions;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hust.openerp.taskmanagement.dto.GetNotifications;
import com.hust.openerp.taskmanagement.entity.Notification;
import com.hust.openerp.taskmanagement.model.UpdateMultipleNotificationStatusBody;
import com.hust.openerp.taskmanagement.service.NotificationService;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Validated
@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    private NotificationService notificationsService;

    /**
     * Subscribes SseEmitter to receive pushed events from server
     *
     * @param toUser
     * @return
     */
    @GetMapping("/subscription")
    public ResponseEntity<SseEmitter> subscribes(
            @CurrentSecurityContext(expression = "authentication.name") String toUser) {
        SseEmitter subscription;
        subscription = new SseEmitter(Long.MAX_VALUE);
        Runnable callback = () -> subscriptions.remove(toUser);

        subscription.onTimeout(callback); // OK
        subscription.onCompletion(callback); // OK
        subscription.onError((ex) -> { // Must consider carefully, but currently OK
            log.error("onError fired with exception: " + ex);
        });

        // Add new subscription to user's connection list.
        if (subscriptions.containsKey(toUser)) {
            subscriptions.get(toUser).add(subscription);
            log.info(
                    "{} RE-SUBSCRIBES --> #CURRENT CONNECTION = {}",
                    toUser,
                    subscriptions.get(toUser).size());
        } else {
            subscriptions.put(toUser, new ArrayList<SseEmitter>() {
                {
                    add(subscription);
                }
            });
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
        // log.info("#CURRENT ACTIVE USER = {}, START SENDING HEARTBEAT EVENT",
        // subscriptions.size());
        long start = System.currentTimeMillis();

        subscriptions.forEach((toUser, subscription) -> {
            // Use iterator to avoid ConcurrentModificationException.
            ListIterator<SseEmitter> iterator = subscription.listIterator();
            int size = subscription.size();

            while (iterator.hasNext()) {
                try {
                    iterator.next().send(SseEmitter
                            .event()
                            .name(SSE_EVENT_HEARTBEAT)
                            .data("keep alive", MediaType.TEXT_EVENT_STREAM));
                    // .comment(":\n\nkeep alive"));
                } catch (Exception e) {
                    iterator.remove();
                    size--;
                    // log.error("FAILED WHEN SENDING HEARTBEAT SIGNAL TO {}, MAY BE USER CLOSED A
                    // CONNECTION", toUser);
                }
            }

            if (size == 0) {
                subscriptions.remove(toUser);
            }
        });

        // log.info(
        // "#CURRENT ACTIVE USER = {}, SENDING HEARTBEAT EVENT DONE IN: {} MS",
        // subscriptions.size(),
        // (System.currentTimeMillis() - start) * 1.0);
    }

    /**
     * Get list of notifications with pagination
     *
     * @param toUser
     * @param page   page number, start at 0
     * @param size   page size
     */
    @GetMapping(params = { "fromId", "page", "size" })
    public ResponseEntity<?> getNotifications(
            @CurrentSecurityContext(expression = "authentication.name") String toUser,
            @RequestParam(required = false) UUID fromId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
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
    public void updateStatus(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        Object value = body.get("status");
        String status = null == value ? null : value.toString();

        if (!Notification.STATUS_READ.equals(status)) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(400), "Invalid status");
        } else {
            notificationsService.updateStatus(id, status);
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
    public void updateMultipleNotificationsStatus(
            @CurrentSecurityContext(expression = "authentication.name") String userId,
            @RequestBody UpdateMultipleNotificationStatusBody body) {
        if (!Notification.STATUS_READ.equals(body.getStatus())) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(400), "Invalid status");
        } else {
            notificationsService.updateMultipleNotificationsStatus(
                    userId,
                    Notification.STATUS_READ,
                    body.getBeforeOrAt());
        }
    }
}
