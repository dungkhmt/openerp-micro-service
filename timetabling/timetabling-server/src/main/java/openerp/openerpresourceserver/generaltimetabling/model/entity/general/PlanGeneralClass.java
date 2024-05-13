package openerp.openerpresourceserver.generaltimetabling.model.entity.general;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "timetabling_plan_general_classes")
public class PlanGeneralClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer quantityMax;
    private String classType;
    private String mass;
    private String programName;
    private String moduleCode;
    private String moduleName;
    private String semester;

}
