package com.hust.wmsbackend.management.model.notification;

import com.hust.wmsbackend.management.model.NotificationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InAppChannel {
    private List<NotificationDTO> notifications;
    private OperationType type;
}
