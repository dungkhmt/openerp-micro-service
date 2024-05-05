package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contest_contest_problem_new")
public class ContestProblem {
    @Id
    @Column(name = "contest_contest_problem_id",updatable = false, nullable = false)
    private String id;

    @Column(name = "contest_id")
    private String contestId;

    @Column(name = "problem_id")
    private String problemId;

    private String problemRecode;

    private String problemRename;
}
