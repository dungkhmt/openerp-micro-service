package openerp.notification.dto.rabbitMessage;

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

