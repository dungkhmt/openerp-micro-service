package openerp.openerpresourceserver.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.entity.enumeration.AccessPermission;

@Getter
@Setter
public class CodeEditorRoomDTO {
    private UUID id;

    private String roomName;

    private String roomMasterId;  
    
    private UserDTO roomMaster;

    private Boolean isPublic;

    private AccessPermission accessPermission;
}
