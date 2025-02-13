package openerp.openerpresourceserver.generaltimetabling.model.entity.composite;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CompositeGroupRoomId implements Serializable {
    private String groupId;
    private String roomId;
}
