package openerp.openerpresourceserver.trainingprogcourse.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgSchedule;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RequestTrainingProgProgramDTO {

    private String id;

    private String name;

    private Date createStamp;

    private Date lastUpdated;

    private List<String> courses;
}
