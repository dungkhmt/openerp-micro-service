package openerp.openerpresourceserver.trainingprogcourse.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestChangeCourseDTO {
    private String courseId;
    private long targetSemester;
    private String programId;
}
