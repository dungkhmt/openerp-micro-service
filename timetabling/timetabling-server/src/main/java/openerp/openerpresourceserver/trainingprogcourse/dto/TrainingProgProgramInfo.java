package openerp.openerpresourceserver.trainingprogcourse.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingProgProgramInfo {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;
}
