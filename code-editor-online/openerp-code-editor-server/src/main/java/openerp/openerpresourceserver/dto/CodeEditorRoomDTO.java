package openerp.openerpresourceserver.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeEditorRoomDTO {
    private UUID id;

    private String roomName;

    private String roomMasterId;
}
