package openerp.openerpresourceserver.service.mapper;

import org.springframework.stereotype.Component;

import openerp.openerpresourceserver.dto.CodeEditorRoomDTO;
import openerp.openerpresourceserver.entity.CodeEditorRoom;

@Component
public class CodeEditorRoomMapper {
    public CodeEditorRoomDTO toDTO(CodeEditorRoom codeEditorRoom) {
        CodeEditorRoomDTO codeEditorRoomDTO = new CodeEditorRoomDTO();
        codeEditorRoomDTO.setId(codeEditorRoom.getId());
        codeEditorRoomDTO.setRoomName(codeEditorRoom.getRoomName());
        codeEditorRoomDTO.setRoomMasterId(codeEditorRoom.getRoomMasterId());
        codeEditorRoomDTO.setAccessPermission(codeEditorRoom.getAccessPermission());
        codeEditorRoomDTO.setIsPublic(codeEditorRoom.getIsPublic());
        return codeEditorRoomDTO;
    }
}
