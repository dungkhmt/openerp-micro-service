package com.hust.baseweb.applications.education.entity;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.hust.baseweb.applications.education.entity.compositeid.CompositeQuizQuestionTagId;

@Getter
@Setter
@Entity
@Table(name = "quiz_question_tag")
@IdClass(CompositeQuizQuestionTagId.class)
public class QuizQuestionTag {

    @Id
    @Column(name = "question_id")
    private UUID questionId;

    @Id
    @Column(name = "tag_id")
    private UUID tagId;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;
}
