package openerp.openerpresourceserver.trainingprogcourse.enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "training_prog_course")
public class TrainingProgCourse {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "credit")
    private Long credit;

    @Column(name = "status")
    private String status;

    @Column(name = "create_stamp")
    private Date createStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrainingProgPrerequisite> prerequisites;

}
