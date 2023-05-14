package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.controller.vm.ShareRoomVM;
import openerp.openerpresourceserver.dto.CodeEditorRoomDTO;
import openerp.openerpresourceserver.dto.SharedRoomUserDTO;
import openerp.openerpresourceserver.entity.CodeEditorRoom;
import openerp.openerpresourceserver.entity.SharedRoomUser;
import openerp.openerpresourceserver.entity.SharedRoomUserKey;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.entity.enumeration.AccessPermission;
import openerp.openerpresourceserver.exception.BadRequestAlertException;
import openerp.openerpresourceserver.repo.CodeEditorRoomRepository;
import openerp.openerpresourceserver.repo.CodeEditorSourceRepository;
import openerp.openerpresourceserver.repo.SharedRoomUserRepository;
import openerp.openerpresourceserver.repo.UserRepository;
import openerp.openerpresourceserver.service.mapper.CodeEditorRoomMapper;
import openerp.openerpresourceserver.service.mapper.SharedRoomUserMapper;
import openerp.openerpresourceserver.service.mapper.UserMapper;

@Service
@AllArgsConstructor
@Transactional
public class CodeEditorRoomService {
    private final CodeEditorRoomRepository codeEditorRoomRepository;

    private final CodeEditorRoomMapper codeEditorRoomMapper;

    private final CodeEditorSourceRepository codeEditorSourceRepository;

    private final SharedRoomUserRepository sharedRoomUserRepository;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final SharedRoomUserMapper sharedRoomUserMapper;

    private final static Pattern UUID_REGEX_PATTERN = Pattern
            .compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(str).matches();
    }

    public Page<CodeEditorRoomDTO> search(String userLoginId, String keyword, Pageable pageable) {
        Specification<CodeEditorRoom> searchByRoomId = (root, query, criteriaBuilder) -> {
            try {
                if (isValidUUID(keyword)) {
                    UUID roomId = UUID.fromString(keyword);
                    if (roomId != null) {
                        return criteriaBuilder.equal(root.get("id").as(UUID.class),
                                roomId);
                    }
                }

            } catch (IllegalArgumentException e) {
                return null;
            }
            return null;

        };
        Specification<CodeEditorRoom> searchByRoomName = (root, query, criteriaBuilder) -> {
            if (keyword != null) {
                return criteriaBuilder.like(root.get("roomName"), "%" + keyword + "%");
            }
            return null;
        };

        Specification<CodeEditorRoom> searchByRoomMasterId = (root, query, criteriaBuilder) -> {
            if (userLoginId != null) {
                return criteriaBuilder.equal(root.get("roomMasterId"), userLoginId);
            }
            return null;
        };

        Specification<CodeEditorRoom> search = searchByRoomId.or(searchByRoomName).and(searchByRoomMasterId);

        Page<CodeEditorRoomDTO> result = codeEditorRoomRepository.findAll(search, pageable)
                .map(codeEditorRoomMapper::toDTO);

        return result;
    }

    public CodeEditorRoomDTO create(String userLoginId, CodeEditorRoomDTO codeEditorRoomDTO) {
        codeEditorRoomDTO.setId(null);
        if (codeEditorRoomDTO.getRoomName() == null) {
            throw new BadRequestAlertException("Tên phòng không được để trống");
        }

        CodeEditorRoom room = new CodeEditorRoom();
        room.setRoomName(codeEditorRoomDTO.getRoomName());
        room.setRoomMasterId(userLoginId);
        room.setIsPublic(false);
        room.setAccessPermission(AccessPermission.VIEWER);

        room = codeEditorRoomRepository.save(room);
        return codeEditorRoomMapper.toDTO(room);
    }

    public CodeEditorRoomDTO update(UUID roomId, CodeEditorRoomDTO codeEditorRoomDTO) {
        Optional<CodeEditorRoom> codeEditorRoom = codeEditorRoomRepository.findById(roomId);
        if (codeEditorRoom.isEmpty()) {
            throw new BadRequestAlertException("Không tìm thấy phòng với id: " + roomId);
        }
        if (codeEditorRoomDTO.getRoomName() != null) {
            codeEditorRoom.get().setRoomName(codeEditorRoomDTO.getRoomName());
        }
        if (codeEditorRoomDTO.getIsPublic() != null) {
            codeEditorRoom.get().setIsPublic(codeEditorRoomDTO.getIsPublic());
        }
        if (codeEditorRoomDTO.getAccessPermission() != null) {
            codeEditorRoom.get().setAccessPermission(codeEditorRoomDTO.getAccessPermission());
        }
        return codeEditorRoomMapper.toDTO(codeEditorRoomRepository.save(codeEditorRoom.get()));
    }

    public CodeEditorRoomDTO findById(UUID roomId) {
        Optional<CodeEditorRoom> codeEditorRoom = codeEditorRoomRepository.findById(roomId);
        if (codeEditorRoom.isEmpty()) {
            throw new BadRequestAlertException("Không tìm thấy phòng với id: " + roomId);
        }

        Optional<User> user = userRepository.findById(codeEditorRoom.get().getRoomMasterId());
        if (user.isEmpty()) {
            throw new BadRequestAlertException("Không tìm thấy user với id: " + codeEditorRoom.get().getRoomMasterId());
        }
        CodeEditorRoomDTO codeEditorRoomDTO = codeEditorRoomMapper.toDTO(codeEditorRoom.get());
        codeEditorRoomDTO.setRoomMaster(userMapper.toDTO(user.get()));
        return codeEditorRoomDTO;
    }

    public void deleteById(UUID roomId) {
        sharedRoomUserRepository.deleteByIdRoomId(roomId);
        codeEditorSourceRepository.deleteByRoomId(roomId);
        codeEditorRoomRepository.deleteById(roomId);
    }

    public void shareRoom(ShareRoomVM shareRoomVM) {
        Optional<CodeEditorRoom> codeEditorRoom = codeEditorRoomRepository.findById(shareRoomVM.getRoomId());
        if (codeEditorRoom.isEmpty()) {
            throw new BadRequestAlertException("Không tìm thấy phòng với id: " + shareRoomVM.getRoomId());
        }
        for (String userId : shareRoomVM.getUserIds()) {
            Optional<SharedRoomUser> sharedRoomUserOpt = sharedRoomUserRepository
                    .findByIdRoomIdAndIdUserId(shareRoomVM.getRoomId(), userId);
            Optional<User> userOpt = userRepository.findById(userId);
            if (sharedRoomUserOpt.isEmpty()) {
                SharedRoomUser sharedRoomUser = new SharedRoomUser();
                SharedRoomUserKey sharedRoomUserKey = new SharedRoomUserKey();
                sharedRoomUserKey.setRoomId(shareRoomVM.getRoomId());
                sharedRoomUserKey.setUserId(userId);

                sharedRoomUser.setId(sharedRoomUserKey);
                sharedRoomUser.setAccessPermission(shareRoomVM.getAccessPermission());
                sharedRoomUser.setRoom(codeEditorRoom.get());
                if (userOpt.isPresent()) {
                    sharedRoomUser.setUser(userOpt.get());
                    sharedRoomUserRepository.save(sharedRoomUser);
                } else {
                    throw new BadRequestAlertException("Không tìm thấy user với id: " + userId);
                }

            } else {
                sharedRoomUserOpt.get().setAccessPermission(shareRoomVM.getAccessPermission());
                sharedRoomUserRepository.save(sharedRoomUserOpt.get());
            }
        }
    }

    public List<SharedRoomUserDTO> getSharedUsersOfRoom(UUID roomId) {
        return sharedRoomUserRepository.findByIdRoomId(roomId).stream().map(sharedRoomUserMapper::toDTO).toList();
    }

    public void deleteAccessPermission(UUID roomId, String userId) {
        sharedRoomUserRepository.deleteByIdRoomIdAndIdUserId(roomId, userId);
    }

}
