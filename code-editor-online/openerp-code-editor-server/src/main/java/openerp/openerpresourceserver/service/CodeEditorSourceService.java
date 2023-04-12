package openerp.openerpresourceserver.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.CodeEditorSourceDTO;
import openerp.openerpresourceserver.entity.CodeEditorRoom;
import openerp.openerpresourceserver.entity.CodeEditorSource;
import openerp.openerpresourceserver.entity.enumeration.ProgrammingLanguage;
import openerp.openerpresourceserver.exception.BadRequestAlertException;
import openerp.openerpresourceserver.repo.CodeEditorRoomRepository;
import openerp.openerpresourceserver.repo.CodeEditorSourceRepository;
import openerp.openerpresourceserver.service.mapper.CodeEditorSourceMapper;

@Service
@AllArgsConstructor
public class CodeEditorSourceService {

    private final CodeEditorSourceRepository codeEditorSourceRepository;

    private final CodeEditorRoomRepository codeEditorRoomRepository;

    private final CodeEditorSourceMapper codeEditorSourceMapper;

    public CodeEditorSourceDTO loadSourceByRoomIdAndLanguage(String userLoginId, UUID roomId, ProgrammingLanguage language) {
        Optional<CodeEditorSource> codeEditorSource = codeEditorSourceRepository.findByRoomIdAndLanguage(roomId,
                language);
        if (codeEditorSource.isPresent()) {
            return codeEditorSourceMapper.toDTO(codeEditorSource.get());
        } else {
            CodeEditorSource newSource = new CodeEditorSource();
            newSource.setEditBy(userLoginId);
            newSource.setSource("");
            newSource.setLanguage(language);
            Optional<CodeEditorRoom> codeEditorRoom = codeEditorRoomRepository
                    .findById(roomId);
            if (codeEditorRoom.isEmpty()) {
                throw new BadRequestAlertException("Không tìm thấy phòng với id: " + roomId);
            }
            newSource.setRoom(codeEditorRoom.get());
            return codeEditorSourceMapper.toDTO(codeEditorSourceRepository.save(newSource));

        }
    }

    public CodeEditorSourceDTO save(String userLoginId, CodeEditorSourceDTO codeEditorSourceDTO) {
        Optional<CodeEditorSource> codeEditorSource = codeEditorSourceRepository
                .findByRoomIdAndLanguage(codeEditorSourceDTO.getRoomId(), codeEditorSourceDTO.getLanguage());
        if (codeEditorSource.isPresent()) {
            codeEditorSource.get().setSource(codeEditorSourceDTO.getSource());
            codeEditorSource.get().setEditBy(userLoginId);
            return codeEditorSourceMapper.toDTO(codeEditorSourceRepository.save(codeEditorSource.get()));
        } else {
            CodeEditorSource newSource = new CodeEditorSource();
            newSource.setLanguage(codeEditorSourceDTO.getLanguage());

            Optional<CodeEditorRoom> codeEditorRoom = codeEditorRoomRepository
                    .findById(codeEditorSourceDTO.getRoomId());
            if (codeEditorRoom.isEmpty()) {
                throw new BadRequestAlertException("Không tìm thấy phòng với id: " + codeEditorSourceDTO.getRoomId());
            }
            newSource.setRoom(codeEditorRoom.get());
            newSource.setSource("");
            newSource.setEditBy(userLoginId);
            newSource = codeEditorSourceRepository.save(newSource);
            return codeEditorSourceMapper.toDTO(newSource);

        }

    }

}
