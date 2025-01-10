package com.hust.baseweb.applications.exam.entity;

import com.hust.baseweb.applications.exam.utils.SecurityUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "exam_result")
public class ExamResultEntity{

    @Id
    @Column(length = 60)
    protected String id;

    @Column(name = "exam_id")
    private String examId;

    @Column(name = "exam_student_id")
    private String examStudentId;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "comment")
    private String comment;

    @Column(name = "submited_at")
    private LocalDateTime submitedAt;

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
        submitedAt = LocalDateTime.now();
    }
}
