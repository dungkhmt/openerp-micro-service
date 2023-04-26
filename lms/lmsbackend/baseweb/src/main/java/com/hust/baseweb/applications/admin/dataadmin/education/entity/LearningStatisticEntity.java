package com.hust.baseweb.applications.admin.dataadmin.education.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "student_learning_statistic")
public class LearningStatisticEntity {

    @Id
    private String loginId;

    private long totalQuizDoingTimes = 0;

    private long totalCodeSubmissions = 0;

    private LocalDateTime latestTimeDoingQuiz;

    private LocalDateTime latestTimeSubmittingCode;

    private long submissionsAcceptedOnTheFirstTime;

    private long totalQuizDoingPeriods;

    private long totalErrorSubmissions;

    private Date createdAt;

    private Date lastModifiedAt;

    public LearningStatisticEntity(String loginId) {
        this.loginId = loginId;
    }

    @PrePersist
    void prePersist() {
        createdAt = new Date();
        lastModifiedAt = new Date();
    }

    @PreUpdate
    void preUpdate() {
        lastModifiedAt = new Date();
    }
}
