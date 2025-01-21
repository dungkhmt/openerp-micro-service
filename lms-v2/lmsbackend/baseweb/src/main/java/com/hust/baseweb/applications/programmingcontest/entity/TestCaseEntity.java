package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "test_case_new")
@EntityListeners(AuditingEntityListener.class)
public class TestCaseEntity implements Serializable {
    private static final long serialVersionUID = 3487495895819801L;

    public static final String STATUS_DISABLED = "DISABLED";
    public static final String STATUS_ENABLED = "ENABLED";

    @Id
    @Column(name = "test_case_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID testCaseId;

    @Column(name = "test_case_point")
    private int testCasePoint;

    @Column(name = "test_case")
    private String testCase;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Column(name = "contest_problem_id")
    private String problemId;

    @Column(name = "is_public")
    private String isPublic;

    @Column(name = "description")
    private String description;

    @Column(name = "status_id")
    private String statusId;

    @LastModifiedDate
    private Date lastUpdatedStamp;

    @CreatedDate
    private Date createdStamp;

//    @JoinColumn(name = "contest_problem_id", referencedColumnName = "problem_id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ProblemEntity problem;

//    @JoinTable(name = "contest_problem_test_case",
//            joinColumns = @JoinColumn(name = "test_case_id", referencedColumnName = "test_case_id"),
//            inverseJoinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "problem_id")
//    )
//    @OneToOne(fetch = FetchType.LAZY)
//    private ContestProblem contestProblem;
}
