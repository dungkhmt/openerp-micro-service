package openerp.openerpresourceserver.programmingcontest.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CompositeProgrammingContestProblemRankingId {
    private String userId;
    private String contestId;
    private String problemId;
}
