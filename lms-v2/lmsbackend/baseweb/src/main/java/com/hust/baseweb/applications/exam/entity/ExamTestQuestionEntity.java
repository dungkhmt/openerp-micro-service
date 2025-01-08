package com.hust.baseweb.applications.exam.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "exam_test_question")
public class ExamTestQuestionEntity {

    @Id
    @Column(length = 60)
    protected String id;

    @Column(name = "exam_test_id")
    private String examTestId;

    @Column(name = "exam_question_id")
    private String examQuestionId;

    @Column(name = "\"order\"")
    private Integer order;
}
