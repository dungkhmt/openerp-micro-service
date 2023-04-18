package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
//@Table(name = "contest_problem")
@Table(name = "contest_submission_new")
public class ContestSubmissionEntity {
    public static final String SUBMISSION_STATUS_ACCEPTED ="Accept";
    public static final String SUBMISSION_STATUS_PARTIAL ="Partial";
    public static final String SUBMISSION_STATUS_FAILED ="Failed";
    public static final String SUBMISSION_STATUS_WRONG ="Wrong Answer";
    public static final String SUBMISSION_STATUS_TIME_LIMIT_EXCEEDED = "Time Limit Exceeded";
    public static final String SUBMISSION_STATUS_OUTPUT_LIMIT_EXCEEDED = "Output Size Limit Exceeded";
    public static final String SUBMISSION_STATUS_MEMORY_ALLOCATION_ERROR = "Memory Allocation Error";
    public static final String SUBMISSION_STATUS_COMPILE_ERROR = "Compile Error";
    public static final String SUBMISSION_STATUS_NOT_AVAILABLE = "NA";
    public static final String SUBMISSION_STATUS_EVALUATION_IN_PROGRESS = "In Progress";
    public static final String SUBMISSION_STATUS_WAIT_FOR_CUSTOM_EVALUATION = "Pending Evaluation";
    public static final String SUBMISSION_STATUS_CUSTOM_EVALUATED = "Evaluated";

    public static final String LANGUAGE_CPP = "CPP";
    public static final String LANGUAGE_JAVA = "JAVA";
    public static final String LANGUAGE_PYTHON = "PYTHON3";

    public static final String MANAGEMENT_STATUS_ENABLED = "ENABLED";
    public static final String MANAGEMENT_STATUS_DISABLED = "DISABLED";

    @Id
    @Column(name = "contest_submission_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID contestSubmissionId;

//    @JoinColumn(name = "problem_id", referencedColumnName = "problem_id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ProblemEntity problem;

    @Column(name = "problem_id")
    private String problemId;

//    @JoinColumn(name = "contest_id", referencedColumnName = "contest_id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ContestEntity contest;

    @Column(name = "contest_id")
    private String contestId;

//    @JoinColumn(name = "user_submission_id", referencedColumnName = "user_login_id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private UserLogin userLogin;

    @Column(name = "user_submission_id")
    private String userId;

//    @JoinColumn(name = "problem_submission_id", referencedColumnName = "problem_submission_id")
//    @OneToOne
//    private ProblemSubmissionEntity problemSubmission;

    @Column(name = "test_case_pass")
    private String testCasePass;

    @Column(name = "source_code")
    private String sourceCode;

    @Column(name = "source_code_language")
    private String sourceCodeLanguage;

    @Column(name = "runtime")
    private Long runtime;

    @Column(name = "memory_usage")
    private float memoryUsage;

    @Column(name = "point")
    private Integer point;

    @Column(name = "status")
    private String status;

    @Column(name = "management_status")
    private String managementStatus;

    @Column(name="submitted_by_user_id")
    private String submittedByUserId;

    @Column(name = "created_stamp")
    private Date createdAt;

    @Column(name = "last_updated_stamp")
    private Date updateAt;

    @Column(name = "last_updated_by_user_id")
    private Date lastUpdatedByUserId;

    @Column(name="message")
    private String message;
}
