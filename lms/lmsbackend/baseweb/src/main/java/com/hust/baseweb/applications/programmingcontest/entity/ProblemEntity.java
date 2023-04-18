package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
//@Table(name = "contest_problem")
@Table(name = "contest_problem_new")
public class ProblemEntity implements Serializable {
    @Id
    @Column(name = "problem_id")
    private String problemId;

//    @JoinTable(name = "contest_problem_problem_source_code",
//        joinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "problem_id"),
//        inverseJoinColumns = @JoinColumn(name = "problem_source_code_id", referencedColumnName = "problem_source_code_id")
//    )
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<ProblemSourceCode> problemSourceCode;

//    @OneToMany(mappedBy = "problem")
//    private Set<ProblemSourceCodeEntity> problemSourceCodes;

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

    @Column(name="solution_checker_source_language")
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

//    @OneToMany(mappedBy = "contestProblem")
//    private Set<TestCase> testCases;
//    @JoinTable(name = "contest_problem_test_case",
//            joinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "problem_id"),
//            inverseJoinColumns = @JoinColumn(name = "test_case_id", referencedColumnName = "test_case_id")
//    )
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<TestCase> testCases;

    @JoinTable(name = "problem_tag",
               joinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "problem_id"),
               inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id")
    )
    @OneToMany(fetch = FetchType.LAZY)
    private List<TagEntity> tags;
}
