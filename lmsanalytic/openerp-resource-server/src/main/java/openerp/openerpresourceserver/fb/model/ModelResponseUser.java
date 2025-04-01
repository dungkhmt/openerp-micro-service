package openerp.openerpresourceserver.fb.model;

import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ModelResponseUser {
    private String id;

    private String groupId;

    private String Name;

    private String link;

    private Date createStamp;

}
