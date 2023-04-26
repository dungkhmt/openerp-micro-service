package openerp.openerpresourceserver.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.entity.enumeration.ProgrammingLanguage;

@Getter
@Setter
public class CodeEditorSourceDTO {
    private  UUID id;

    private ProgrammingLanguage language;

    private String source;

    private UUID roomId;
}
