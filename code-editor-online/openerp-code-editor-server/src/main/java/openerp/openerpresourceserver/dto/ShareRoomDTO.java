package openerp.openerpresourceserver.dto;

import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.entity.SharedRoomUserKey;
import openerp.openerpresourceserver.entity.enumeration.AccessPermission;

@Getter
@Setter
public class ShareRoomDTO {
    private SharedRoomUserKey id;

    private AccessPermission accessPermission;

    private CodeEditorRoomDTO room;

    private UserDTO user;

}
