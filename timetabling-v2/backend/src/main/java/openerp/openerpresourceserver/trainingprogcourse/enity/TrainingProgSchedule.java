package openerp.openerpresourceserver.trainingprogcourse.enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.generaltimetabling.model.entity.User;

import java.util.UUID;
@Setter
@Getter
@Entity
@Table(name = "training_prog_schedule")
public class TrainingProgSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "semester", referencedColumnName = "id")
    private TrainingProgSemester semester;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private TrainingProgCourse course;

    @ManyToOne
    @JoinColumn(name = "program_id", referencedColumnName = "id")
    private TrainingProgProgram program;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "user_login_id")
    private User createdByUserId;;




}
