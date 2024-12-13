package openerp.openerpresourceserver.trainingprogcourse.enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "training_prog_semester")
public class TrainingProgSemester {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "semester")
    private List<TrainingProgSchedule> schedules;

}
