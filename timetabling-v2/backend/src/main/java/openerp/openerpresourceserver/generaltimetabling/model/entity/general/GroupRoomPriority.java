package openerp.openerpresourceserver.generaltimetabling.model.entity.general;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.composite.CompositeGroupRoomId;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_group_room_priority")
@IdClass(CompositeGroupRoomId.class)
public class GroupRoomPriority {
    @Id
    @Column(name = "group_id")
    private String groupId;

    @Id
    @Column(name="room_id")
    private String roomId;

    @Column(name="priority")
    private int priority;

}
