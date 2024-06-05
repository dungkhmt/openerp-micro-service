package openerp.openerpresourceserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContestSubmissionByStudent {
    private String contestId;
    private int totalProblems;
    private int totalProblemsSubmitted;
}
