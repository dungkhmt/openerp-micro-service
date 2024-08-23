package openerp.openerpresourceserver.trainingprogcourse.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
public class RequestTrainingProgCourse {
    private String id;

    private String courseName;

    private Long credit;

    List<String> prerequisites ;

    private String status;
}
