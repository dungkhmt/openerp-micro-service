package openerp.openerpresourceserver.trainingprogcourse.dto.request;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TrainingProgScheduleUpdateRequest {

    private String programId;

    private String courseId;

    private String semesterId;
}
