package openerp.openerpresourceserver.generaltimetabling.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MakeGeneralClassRequest {
    private Long id;
    private Integer quantityMax;
    private Integer exerciseMaxQuantity;
    private Integer lectureExerciseMaxQuantity;
    private Integer lectureMaxQuantity;
    private String classType;
    private int nbClasses;
    private String mass;
    private String programName;
    private String moduleCode;
    private String moduleName;
    private String semester;
    private String learningWeeks;
    private String crew;
    private String weekType;
    private int duration;
}
