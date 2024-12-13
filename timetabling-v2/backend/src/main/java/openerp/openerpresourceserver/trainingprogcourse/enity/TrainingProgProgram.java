package openerp.openerpresourceserver.trainingprogcourse.enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "training_prog_program")
public class TrainingProgProgram {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "create_stamp")
    private Date createStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingProgSchedule> schedules;

}
