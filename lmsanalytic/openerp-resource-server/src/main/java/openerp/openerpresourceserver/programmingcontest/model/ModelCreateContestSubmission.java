package openerp.openerpresourceserver.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelCreateContestSubmission {
    private UUID contestSubmissionId;

    private String contestId;
    private String contestName;
    private String contestType;
    private String contestCreatedUserId;
    private Date contestCreatedStamp;

    private String problemId;
    private String problemName;
    private String problemDescription;
    private String problemCreatedUserId;
    private int problemTimeLimit;
    private int problemMemoryLimit;
    private String problemLevelId;
    private String problemCategory;
    private Date problemCreatedStamp;

    private String participantUserId;
    private String participantUserFullName;
    private String submittedByUserId;
    private String submittedByUserFullname;
    private String submissionStatus;
    private int point;
    private String testCasePasses;
    private String sourceCode;
    private String sourceCodeLanguage;
    private int runtime;
    private int memoryUsage;
    private Date submissionCreatedStamp;
    private Date submissionLastUpdatedStamp;
    private String message;
    private String managementStatus;
    private String violationForbiddenInstructionMessage;
    private String violationForbiddenInstructionStatus;

}
