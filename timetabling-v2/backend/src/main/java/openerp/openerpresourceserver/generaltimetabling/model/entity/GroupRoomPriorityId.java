package openerp.openerpresourceserver.generaltimetabling.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GroupRoomPriorityId implements Serializable {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupRoomPriorityId that = (GroupRoomPriorityId) o;
        return groupId.equals(that.groupId) && roomId.equals(that.roomId);
    }

    @Override
    public int hashCode() {
        return groupId.hashCode() + roomId.hashCode();
    }
}
