package com.hust.baseweb.applications.education.thesisdefensejury.entity;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import com.hust.baseweb.applications.education.thesisdefensejury.composite.DefenseJuryTeacherID;
import com.hust.baseweb.applications.programmingcontest.composite.UserSubmissionContestResultID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "defense_jury_teacher") // Entity map voi bang defense_jury_teacher
@NoArgsConstructor
@IdClass(DefenseJuryTeacherID.class)
public class DefenseJuryTeacher {
    @Id
    @Column(name = "teacher_id")
    private String teacherId;

    @Id
    @Column(name = "jury_id")
    private UUID juryId;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updatedDateTime;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private LocalDateTime createdTime;

}
