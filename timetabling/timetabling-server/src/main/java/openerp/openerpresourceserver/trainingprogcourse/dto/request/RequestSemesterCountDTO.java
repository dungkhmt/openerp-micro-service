package openerp.openerpresourceserver.trainingprogcourse.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSemesterCountDTO {

    private String programId;
    private long semesterCount;
}
