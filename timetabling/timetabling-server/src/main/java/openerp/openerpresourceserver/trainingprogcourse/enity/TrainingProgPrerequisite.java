package openerp.openerpresourceserver.trainingprogcourse.enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "training_prog_prerequisite")
public class TrainingProgPrerequisite {
    @EmbeddedId
    private TrainingProgPrerequisiteId id;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private TrainingProgCourse course;

    @ManyToOne
    @MapsId("prerequisiteCourseId")
    @JoinColumn(name = "prerequisite_course_id")
    private TrainingProgCourse prerequisiteCourse;

    @Column(name = "create_stamp")
    private Date createStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;


}
