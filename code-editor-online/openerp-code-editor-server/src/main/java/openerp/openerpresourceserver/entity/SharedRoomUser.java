package openerp.openerpresourceserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.entity.enumeration.AccessPermission;

@Table(name = "code_editor_shared_room_user")
@Entity
@Getter
@Setter
public class SharedRoomUser {
    @EmbeddedId
    private SharedRoomUserKey id;

    @Column(name = "access_permission")
    @Enumerated(EnumType.STRING)
    private AccessPermission accessPermission;

    @ManyToOne
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private CodeEditorRoom room;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
