package openerp.openerpresourceserver.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterClassOpenedDto {

    private String semester;

    private String groupName;

    private String classRoom;

    private String moduleCode;

    private String moduleName;

    private String studyClass;

    private String crew;

    private String openBatch;

}
