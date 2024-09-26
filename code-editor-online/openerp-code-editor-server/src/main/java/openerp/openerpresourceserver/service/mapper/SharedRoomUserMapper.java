package openerp.openerpresourceserver.service.mapper;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.SharedRoomUserDTO;
import openerp.openerpresourceserver.entity.SharedRoomUser;

@Component
@AllArgsConstructor
public class SharedRoomUserMapper {

    private final CodeEditorRoomMapper codeEditorRoomMapper;

    private final UserMapper userMapper;

    public SharedRoomUserDTO toDTO(SharedRoomUser sharedRoomUser) {
        SharedRoomUserDTO sharedRoomUserDTO = new SharedRoomUserDTO();
        sharedRoomUserDTO.setId(sharedRoomUser.getId());
        sharedRoomUserDTO.setAccessPermission(sharedRoomUser.getAccessPermission());
        sharedRoomUserDTO.setRoom(codeEditorRoomMapper.toDTO(sharedRoomUser.getRoom()));
        sharedRoomUserDTO.setUser(userMapper.toDTO(sharedRoomUser.getUser()));
        return sharedRoomUserDTO;
    }

}
