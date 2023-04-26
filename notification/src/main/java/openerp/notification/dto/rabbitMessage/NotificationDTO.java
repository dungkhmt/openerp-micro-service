package openerp.notification.dto.rabbitMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String id;

    private String content;

    private String url;

    private String avatar;

    private boolean read;

    private Date createdStamp;
}
