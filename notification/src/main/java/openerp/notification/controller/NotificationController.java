package openerp.notification.controller;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.notification.dto.GetNotifications;
import openerp.notification.dto.NewNotificationRequest;
import openerp.notification.dto.UpdateMultipleNotificationStatus;
import openerp.notification.entity.Notifications;
import openerp.notification.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
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

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import static openerp.notification.service.NotificationsService.SSE_EVENT_HEARTBEAT;
import static openerp.notification.service.NotificationsService.subscriptions;


@Log4j2
@Validated
@RestController
@RequestMapping("/notification")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class NotificationController {

    @Autowired
    private NotificationsService notificationsService;

    public NotificationController(MeterRegistry registry) {
        Gauge.builder("active_user", subscriptions::size).register(registry);
    }

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
            subscriptions.put(toUser, new ArrayList<SseEmitter>() {{
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
//        log.info("#CURRENT ACTIVE USER = {}, START SENDING HEARTBEAT EVENT", subscriptions.size());
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
//                                      .comment(":\n\nkeep alive"));
                } catch (Exception e) {
                    iterator.remove();
                    size--;
//                    log.error("FAILED WHEN SENDING HEARTBEAT SIGNAL TO {}, MAY BE USER CLOSED A CONNECTION", toUser);
                }
            }

            if (size == 0) {
                subscriptions.remove(toUser);
            }
        });

//        log.info(
//            "#CURRENT ACTIVE USER = {}, SENDING HEARTBEAT EVENT DONE IN: {} MS",
//            subscriptions.size(),
//            (System.currentTimeMillis() - start) * 1.0);
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
            @RequestBody UpdateMultipleNotificationStatus body
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

    @PostMapping
    public ResponseEntity<?> createNotification(
            @CurrentSecurityContext(expression = "authentication.name") String fromUser,
            @RequestBody NewNotificationRequest request) {
        notificationsService.create(fromUser, request.getToUser(), request.getContent(), request.getUrl());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
