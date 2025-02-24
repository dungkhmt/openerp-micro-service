package openerp.openerpresourceserver.examtimetabling.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exam_timetabling_class")
public class ExamClass {
    @Id
    @Column(name="id")
    private UUID id;

    @Column(name="exam_class_id")
    private String examClassId;

    @Column(name="class_id")
    private String classId;

    @Column(name="course_id")
    private String courseId;

    @Column(name="group_id")
    private String groupId;

    @Column(name="course_name")
    private String courseName;

    @Column(name="description")
    private String description;

    @Column(name="number_students")
    private Integer numberOfStudents;

    @Column(name="period")
    private String period;

    @Column(name="management_code")
    private String managementCode;

    @Column(name="school")
    private String school;

    @Column(name="exam_plan_id", nullable = true)
    private UUID examPlanId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_plan_id", insertable = false, updatable = false)
    private ExamPlan examPlan;
}
