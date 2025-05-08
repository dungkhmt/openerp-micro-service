package openerp.openerpresourceserver.model.notification;

import openerp.openerpresourceserver.model.NotificationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InAppChannel {
    private List<NotificationDto> notifications;
    private OperationType type;
}
