package com.hust.baseweb.applications.exam.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "exam_question")
public class ExamQuestionEntity extends BaseEntity {

    @Column(name = "exam_subject_id")
    private String examSubjectId;

    @Column(name = "code")
    private String code;

    @Column(name = "type")
    private Integer type;

    @Column(name = "content")
    private String content;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "number_answer")
    private Integer numberAnswer;

    @Column(name = "content_answer1")
    private String contentAnswer1;

    @Column(name = "content_answer2")
    private String contentAnswer2;

    @Column(name = "content_answer3")
    private String contentAnswer3;

    @Column(name = "content_answer4")
    private String contentAnswer4;

    @Column(name = "content_answer5")
    private String contentAnswer5;

    @Column(name = "multichoice")
    private boolean multichoice;

    @Column(name = "answer")
    private String answer;

    @Column(name = "explain")
    private String explain;
}
