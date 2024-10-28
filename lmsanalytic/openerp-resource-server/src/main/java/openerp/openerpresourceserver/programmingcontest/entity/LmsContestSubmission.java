package openerp.openerpresourceserver.programmingcontest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lms_contest_submission")
public class LmsContestSubmission {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "contest_submission_id")
    private UUID contestSubmissionId;

    @Column(name="contest_id")
    private String contestId;

    @Column(name="contest_name")
    private String contestName;

    @Column(name="contest_type")
    private String contestType;

    @Column(name="contest_user_create_id")
    private String contestCreatedUserId;

    @Column(name="contest_created_stamp")
    private Date contestCreatedStamp;

    @Column(name = "problem_id")
    private String problemId;

    @Column(name="problem_name")
    private String problemName;

    @Column(name="problem_description")
    private String problemDescription;

    @Column(name="problem_created_by_user_login_id")
    private String problemCreatedByUserLoginId;

    @Column(name="problem_time_limit")
    private int problemTimeLimit;

    @Column(name="problem_memory_limit")
    private int problemMemoryLimit;

    @Column(name="problem_level_id")
    private String problemLevelId;

    @Column(name="problem_category_id")
    private String problemCategoryId;

    @Column(name="problem_created_stamp")
    private Date problemCreatedStamp;

    @Column(name="user_submission_id")
    private String userSubmissionId;

    @Column(name="user_submission_name")
    private String userSubmissionFullName;
    @Column(name="status")
    private String status;

    @Column(name="point")
    private int point;

    @Column(name="test_case_pass")
    private String testCasePass;

    @Column(name="source_code ")
    private String sourceCode;
    @Column(name="source_code_language")
    private String sourceCodeLanguage;

    @Column(name="runtime")
    private int runTime;

    @Column(name="memory_usage")
    private double memoryUsage;

    @Column(name="submission_last_updated_stamp")
    private Date submissionLastUpdatedStamp;

    @Column(name="submission_created_stamp")
    private Date submissionCreatedStamp;

    @Column(name="message")
    private String message;

    @Column(name="submitted_by_user_id")
    private String submittedByUserId;
    @Column(name="submitted_by_user_fullname")
    private String submittedByUserFullName;

    @Column(name="last_updated_by_user_id")
    private String lastUpdatedByUserId;

    @Column(name="management_status")
    private String managementStatus;

    @Column(name="violate_forbidden_instruction")
    private String violateForbiddenInstructions;

    @Column(name="violate_forbidden_instruction_message")
    private String violateForbiddenInstructionMessage;

    @Column(name="last_updated_stamp")
    private Date lastUpdatedStamp;

    @Column(name="created_stamp")
    private Date createdStamp;

}
