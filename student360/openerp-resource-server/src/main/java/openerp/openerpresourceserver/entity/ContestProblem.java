package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.*;
import openerp.openerpresourceserver.composite.CompositeContestProblemId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "contest_contest_problem_new")
@IdClass(CompositeContestProblemId.class)
public class ContestProblem {

    public static final String SUBMISSION_MODE_SOURCE_CODE = "SUBMISSION_MODE_SOURCE_CODE";
    public static final String SUBMISSION_MODE_SOLUTION_OUTPUT = "SUBMISSION_MODE_SOLUTION_OUTPUT";
    public static final String SUBMISSION_MODE_NOT_ALLOWED = "SUBMISSION_MODE_NOT_ALLOWED";
    public static final String SUBMISSION_MODE_HIDDEN = "SUBMISSION_MODE_HIDDEN";
    public static List<String> getSubmissionModes() {
        List<String> L = new ArrayList();
        L.add(SUBMISSION_MODE_SOURCE_CODE);
        L.add(SUBMISSION_MODE_SOLUTION_OUTPUT);
        L.add(SUBMISSION_MODE_NOT_ALLOWED);
        return L;
    }

    @Id
    @Column(name = "contest_id")
    private String contestId;

    @Id
    @Column(name = "problem_id")
    private String problemId;

    @Column(name = "problem_rename")
    private String problemRename;

    @Column(name = "problem_recode")
    private String problemRecode;

    @Column(name = "submission_mode")
    private String submissionMode;

    @Column(name="forbidden_instructions")
    private String forbiddenInstructions;
}
