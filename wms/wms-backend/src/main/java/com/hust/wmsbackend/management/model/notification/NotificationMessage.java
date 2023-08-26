package com.hust.wmsbackend.management.model.notification;

import com.hust.wmsbackend.management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private User user;
    private Channel channels;
}
