package com.hust.baseweb.applications.education.quiztest.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "participant_doing_quiz_add_explanation")
@NoArgsConstructor
public class QuizQuestionDoingExplanation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID questionId;

    private String participantUserId;

    private String testId;

    private String solutionExplanation;

    private String attachment;

    @Setter(AccessLevel.PACKAGE)
    private Date createdStamp;

    @Setter(AccessLevel.PACKAGE)
    private Date lastUpdatedStamp;

    @PrePersist
    void initCreatedStamp() {
        createdStamp = new Date();
    }

    @PreUpdate
    void updateLastUpdatedStamp() {
        lastUpdatedStamp = new Date();
    }

    public void setExplanation(String solutionExplanation, ObjectId attachmentStorageId) {
        this.solutionExplanation = solutionExplanation;
        this.attachment = attachmentStorageId.toString();
    }

}
