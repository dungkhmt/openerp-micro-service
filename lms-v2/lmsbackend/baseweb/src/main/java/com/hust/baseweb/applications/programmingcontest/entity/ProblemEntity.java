package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "contest_problem_new")
@EntityListeners(AuditingEntityListener.class)
public class ProblemEntity implements Serializable {
    private static final long serialVersionUID = 3487495895819800L;
    public static final String PROBLEM_STATUS_OPEN = "OPEN";
    public static final String PROBLEM_STATUS_HIDDEN = "HIDDEN";

    @Id
    @Column(name = "problem_id")
    private String problemId;

    @Column(name = "problem_name", unique = true)
    private String problemName;

    @Column(name = "problem_description")
    private String problemDescription;

//    @Column(name = "time_limit")
//    private float timeLimit;

    @Column(name = "time_limit_cpp")
    private float timeLimitCPP;

    @Column(name = "time_limit_java")
    private float timeLimitJAVA;

    @Column(name = "time_limit_python")
    private float timeLimitPYTHON;

    @Column(name = "memory_limit")
    private float memoryLimit;

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

    @Column(name = "is_public")
    private boolean isPublicProblem;

    @Column(name = "attachment")
    private String attachment;

    @Column(name = "score_evaluation_type")
    private String scoreEvaluationType;

    @Column(name = "appearances")
    private int appearances;

    @Column(name = "is_preload_code")
    private Boolean isPreloadCode;

    @Column(name = "preload_code")
    private String preloadCode;
    
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

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "sample_testcase")
    private String sampleTestcase;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdAt;

    @LastModifiedDate
    private Date lastUpdatedStamp;

    @CreatedBy
    @Column(name = "created_by_user_login_id")
    private String createdBy;
}
