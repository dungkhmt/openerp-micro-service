package openerp.openerpresourceserver.generaltimetabling.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "timetabling_group_room_priority")
@IdClass(GroupRoomPriorityId.class)
public class GroupRoomPriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Id
    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "priority", nullable = false)
    private Integer priority;
}
