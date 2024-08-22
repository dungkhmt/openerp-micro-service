package openerp.openerpresourceserver.firstyeartimetabling.entity.firstyear;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

@Table(name = "first_year_timetabling_plan_first_year_classes")
public class PlanFirstYearClass {
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
    private Integer numberOfClasses;
    private Integer lectureMaxQuantity;
    private Integer exerciseMaxQuantity;
    private Integer lectureExerciseMaxQuantity;
    private String learningWeeks;
    private String crew;
}
