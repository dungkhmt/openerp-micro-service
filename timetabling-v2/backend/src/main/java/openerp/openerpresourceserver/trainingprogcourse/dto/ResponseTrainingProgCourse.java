package openerp.openerpresourceserver.trainingprogcourse.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ResponseTrainingProgCourse {
    private String id;

    private String courseName;

    private Long credit;

    private String status;

    private Date createStamp;

    private Date lastUpdated;

    List<String> prerequisites ;
}
