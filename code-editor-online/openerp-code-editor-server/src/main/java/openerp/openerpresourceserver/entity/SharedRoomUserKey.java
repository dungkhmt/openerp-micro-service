package openerp.openerpresourceserver.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SharedRoomUserKey implements Serializable {
    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "user_id")
    private String userId;
}
