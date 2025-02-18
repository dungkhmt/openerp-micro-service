package openerp.openerpresourceserver.fb.model;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ModelResponseFbGroup {

    private String id;


    private String groupName;

    private String groupType;


    private int numberMembers;


    private String link;


    private Date createStamp;


}
