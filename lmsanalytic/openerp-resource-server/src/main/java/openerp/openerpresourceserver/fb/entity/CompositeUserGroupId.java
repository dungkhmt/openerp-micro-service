package openerp.openerpresourceserver.fb.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CompositeUserGroupId {
    private String userId;
    private String groupId;
}
