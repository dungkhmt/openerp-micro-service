package openerp.openerpresourceserver.model.notification;

import openerp.openerpresourceserver.entity.User;
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
