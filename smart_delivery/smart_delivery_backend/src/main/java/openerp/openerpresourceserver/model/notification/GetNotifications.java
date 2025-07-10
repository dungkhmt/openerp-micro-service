package openerp.openerpresourceserver.model.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.model.NotificationProjection;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class GetNotifications {
    private Page<NotificationProjection> notifications;
    private long numUnRead;
}
