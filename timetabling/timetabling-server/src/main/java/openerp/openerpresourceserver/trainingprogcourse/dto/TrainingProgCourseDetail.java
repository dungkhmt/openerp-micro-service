package openerp.openerpresourceserver.trainingprogcourse.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisite;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class TrainingProgCourseDetail {

    private UUID idSchedule;

    private String Id;

    private String courseName;

    private Long credit;

    private String status;

    private String semester;

    private List<String> prerequisites;


}
