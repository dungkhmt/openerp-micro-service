package openerp.openerpresourceserver.trainingprogcourse.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Getter
@Setter
public class RequestDeleteTrainingProgProgramDTO {

    String programId;
    List<String> courseIds;
}
