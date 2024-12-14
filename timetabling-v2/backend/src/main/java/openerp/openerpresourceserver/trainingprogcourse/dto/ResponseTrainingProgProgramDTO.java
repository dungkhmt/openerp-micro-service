package openerp.openerpresourceserver.trainingprogcourse.dto;



import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ResponseTrainingProgProgramDTO {

    private String id;

    private String name;

    private Date createStamp;

    private Date lastUpdated;

    private List<TrainingProgCourseDetail> schedules;
}
