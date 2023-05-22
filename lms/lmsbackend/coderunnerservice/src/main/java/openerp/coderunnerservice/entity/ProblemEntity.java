package openerp.coderunnerservice.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "contest_problem_new")
public class ProblemEntity implements Serializable {
    @Id
    @Column(name = "problem_id")
    private String problemId;

    @Column(name = "problem_name", unique = true)
    private String problemName;

    @Column(name = "problem_description")
    private String problemDescription;

    @Column(name = "created_by_user_login_id")
    private String userId;

    @Column(name = "time_limit")
    private int timeLimit;

    @Column(name = "memory_limit")
    private int memoryLimit;

    @Column(name = "level_id")
    private String levelId;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "correct_solution_source_code")
    private String correctSolutionSourceCode;

    @Column(name = "correct_solution_language")
    private String correctSolutionLanguage;

    @Column(name = "solution_checker_source_code")
    private String solutionCheckerSourceCode;

    @Column(name = "solution_checker_source_language")
    private String solutionCheckerSourceLanguage;

    @Column(name = "solution")
    private String solution;

    @Column(name = "level_order")
    private int levelOrder;

    @Column(name = "created_stamp")
    private Date createdAt;

    @Column(name = "is_public")
    private boolean isPublicProblem;

    @Column(name = "attachment")
    private String attachment;

    @Column(name = "score_evaluation_type")
    private String scoreEvaluationType;

//    @JoinTable(name = "problem_tag",
//               joinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "problem_id"),
//               inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id")
//    )
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<TagEntity> tags;
}
