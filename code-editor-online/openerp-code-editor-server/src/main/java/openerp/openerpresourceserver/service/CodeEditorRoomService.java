package openerp.openerpresourceserver.service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.CodeEditorRoomDTO;
import openerp.openerpresourceserver.entity.CodeEditorRoom;
import openerp.openerpresourceserver.exception.BadRequestAlertException;
import openerp.openerpresourceserver.repo.CodeEditorRoomRepository;
import openerp.openerpresourceserver.repo.CodeEditorSourceRepository;
import openerp.openerpresourceserver.service.mapper.CodeEditorRoomMapper;

@Service
@AllArgsConstructor
public class CodeEditorRoomService {
    private final CodeEditorRoomRepository codeEditorRoomRepository;

    private final CodeEditorRoomMapper codeEditorRoomMapper;

    private final CodeEditorSourceRepository codeEditorSourceRepository;

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

        room = codeEditorRoomRepository.save(room);
        return codeEditorRoomMapper.toDTO(room);
    }

    public CodeEditorRoomDTO update(UUID roomId, CodeEditorRoomDTO codeEditorRoomDTO) {
        Optional<CodeEditorRoom> codeEditorRoom = codeEditorRoomRepository.findById(roomId);
        if (codeEditorRoom.isEmpty()) {
            throw new BadRequestAlertException("Không tìm thấy phòng với id: " + roomId);
        }

        codeEditorRoom.get().setRoomName(codeEditorRoomDTO.getRoomName());
        return codeEditorRoomMapper.toDTO(codeEditorRoomRepository.save(codeEditorRoom.get()));
    }

    public CodeEditorRoomDTO findById(UUID roomId) {
        Optional<CodeEditorRoom> codeEditorRoom = codeEditorRoomRepository.findById(roomId);
        if (codeEditorRoom.isEmpty()) {
            throw new BadRequestAlertException("Không tìm thấy phòng với id: " + roomId);
        }
        return codeEditorRoomMapper.toDTO(codeEditorRoom.get());
    }

    public void deleteById(UUID roomId) {
        codeEditorSourceRepository.deleteByRoomId(roomId);
        codeEditorRoomRepository.deleteById(roomId);
    }
}
