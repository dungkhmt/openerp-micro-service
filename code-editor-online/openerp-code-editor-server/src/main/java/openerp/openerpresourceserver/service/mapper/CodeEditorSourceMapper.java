package openerp.openerpresourceserver.service.mapper;

import org.springframework.stereotype.Component;

import openerp.openerpresourceserver.dto.CodeEditorSourceDTO;
import openerp.openerpresourceserver.entity.CodeEditorSource;

@Component
public class CodeEditorSourceMapper {

    public CodeEditorSourceDTO toDTO(CodeEditorSource codeEditorSource) {
        CodeEditorSourceDTO codeEditorSourceDTO = new CodeEditorSourceDTO();
        codeEditorSourceDTO.setId(codeEditorSource.getId());
        codeEditorSourceDTO.setLanguage(codeEditorSource.getLanguage());
        codeEditorSourceDTO.setSource(codeEditorSource.getSource());
        codeEditorSourceDTO.setRoomId(codeEditorSource.getRoom().getId());
        return codeEditorSourceDTO;
    }

}
