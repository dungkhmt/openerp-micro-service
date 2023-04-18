package com.hust.baseweb.applications.education.thesisdefensejury.entity;


import com.hust.baseweb.applications.education.entity.mongodb.Teacher;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import com.hust.baseweb.applications.education.thesisdefensejury.composite.DefenseJuryTeacherID;
import com.hust.baseweb.applications.education.thesisdefensejury.composite.DefensePlanTeacherID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "teacher_thesis_defense_plan") // Entity map voi bang teacher_thesis_defense_plan
@NoArgsConstructor
@IdClass(DefensePlanTeacherID.class)
public class TeacherThesisDefensePlan {
    @Id
    @Column(name = "teacher_id")
    private String teacherId;

    @Id
    @Column(name = "thesis_defense_plan_id")
    private String thesisDefensePlanId;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updatedDateTime;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private LocalDateTime createdTime;


}
