package openerp.openerpresourceserver.generaltimetabling.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MakeGeneralClassRequest {
    private Long id;
    private Integer quantityMax;
    private String classType;
    private String mass;
    private String programName;
    private String moduleCode;
    private String moduleName;
    private String semester;
    private String learningWeeks;
    private String crew;
    private String weekType;
}
