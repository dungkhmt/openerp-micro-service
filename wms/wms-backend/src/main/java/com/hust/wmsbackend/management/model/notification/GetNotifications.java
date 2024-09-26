package com.hust.wmsbackend.management.model.notification;

import com.hust.wmsbackend.management.model.NotificationProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class GetNotifications {
    private Page<NotificationProjection> notifications;
    private long numUnRead;
}
