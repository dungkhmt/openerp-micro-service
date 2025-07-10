package openerp.openerpresourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String id;
    private String content;
    private String url;
    private String avatar;
    private boolean read;
    private Date createdStamp;
}

