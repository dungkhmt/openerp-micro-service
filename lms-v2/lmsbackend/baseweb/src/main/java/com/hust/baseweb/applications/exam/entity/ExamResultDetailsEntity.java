package com.hust.baseweb.applications.exam.entity;

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
@Table(name = "exam_result_details")
public class ExamResultDetailsEntity {

    @Id
    @Column(length = 60)
    private String id;

    @Column(name = "exam_result_id")
    private String examResultId;

    @Column(name = "exam_question_id")
    private String examQuestionId;

    @Column(name = "answer")
    private String answer;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "pass")
    private boolean pass;

    @Column(name = "score")
    private Integer score;

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
    }
}
