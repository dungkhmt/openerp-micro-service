package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "contest_submission_testcase_new")

public class ContestSubmissionTestCaseEntity {
    public static final String USED_TO_GRADE_YES = "Y";
    public static final String USED_TO_GRADE_NO = "N";
    @Id
    @Column(name = "contest_submission_testcase_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID contestSubmissionTestcaseId;

    @Column(name = "contest_submission_id")
    private UUID contestSubmissionId;

    @Column(name = "contest_id")
    private String contestId;

    @Column(name = "problem_id")
    private String problemId;

    @Column(name = "submitted_by_user_login_id")
    private String submittedByUserLoginId;

    @Column(name = "test_case_id")
    private UUID testCaseId;

    @Column(name = "point")
    private int point;

    @Column(name = "used_to_grade")
    private String usedToGrade; // Y/N: means that the point of this is (not) accounted to grade of submission

    @Column(name = "status")
    private String status;

    @Column(name = "participant_solution_output")
    private String participantSolutionOutput;

    @Column(name = "stderr")
    private String stderr;

    @Column(name = "runtime")
    private Long runtime;

    @Column(name = "memory_usage")
    private Float memoryUsage;

    @Column(name = "judge0_submission_token")
    private UUID judge0SubmissionToken;

    @UpdateTimestamp
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

}
