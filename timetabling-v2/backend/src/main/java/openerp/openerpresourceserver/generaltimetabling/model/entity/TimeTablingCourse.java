package openerp.openerpresourceserver.generaltimetabling.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "timetabling_course")
public class TimeTablingCourse {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="name")
    private String name;

    @Column(name="slots_priority")
    private String slotPriority;// 3,2,1

    @Column(name = "max_teacher_in_charge")
    private Integer maxTeacherInCharge;
}
