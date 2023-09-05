package com.hust.baseweb.applications.programmingcontest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
//@Table(name = "contest")
@Table(name = "contest_new")
public class ContestEntity implements Serializable {
    private static final long serialVersionUID = 3487495895819802L;

    public static final String CONTEST_STATUS_CREATED = "CREATED";
    public static final String CONTEST_STATUS_OPEN = "OPEN";
    public static final String CONTEST_STATUS_RUNNING = "RUNNING";
    public static final String CONTEST_STATUS_COMPLETED = "COMPLETED";
    public static final String CONTEST_STATUS_CLOSED = "CLOSED";
    public static final String CONTEST_STATUS_DISABLED = "DISABLED";

    public static final String CONTEST_SUBMISSION_ACTION_TYPE_STORE_ONLY = "STORE_ONLY";
    public static final String CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE = "STORE_AND_EXECUTE";

//    public static final String CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER = "SEE_CORRECT_ANSWER";
//    public static final String CONTEST_PARTICIPANT_VIEW_MODE_NOT_SEE_CORRECT_ANSWER = "NOT_SEE_CORRECT_ANSWER";
//    public static final String CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER_AND_PRIVATE_TESTCASE = "SEE_CORRECT_ANSWER_AND_PRIVATE_TEST_CASE";
//    public static final String CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER_AND_PRIVATE_TESTCASE_SHORT = "SEE_CORRECT_ANSWER_AND_PRIVATE_TEST_CASE_SHORT";

    public static final String CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_ENABLED = "ENABLE";
    public static final String CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_DISABLED = "DISABLE";


    public static final String CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_VISIBLE = "VISIBLE";
    public static final String CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_HIDDEN = "HIDDEN";

    public static final String EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES = "Y";
    public static final String EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_NO = "N";

    public static final String SYNCHRONOUS_JUDGE_MODE = "SYNCHRONOUS_JUDGE_MODE";
    public static final String ASYNCHRONOUS_JUDGE_MODE_QUEUE = "ASYNCHRONOUS_JUDGE_MODE_QUEUE";

    public static List<String> getJudgeModes() {
        List<String> L = new ArrayList();
        L.add(SYNCHRONOUS_JUDGE_MODE);
        L.add(ASYNCHRONOUS_JUDGE_MODE_QUEUE);
        return L;
    }

    public static List<String> getListEvaluateBothPublicPrivateTestcases() {
        List<String> L = new ArrayList();
        L.add(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES);
        L.add(ContestEntity.EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_NO);
        return L;
    }

    public static List<String> getProblemDescriptionViewTypes() {
        List<String> L = new ArrayList();
        L.add(CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_VISIBLE);
        L.add(CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_HIDDEN);
        return L;
    }

    public static List<String> getStatusIds() {
        List<String> L = new ArrayList();
        L.add(ContestEntity.CONTEST_STATUS_CREATED);
        L.add(ContestEntity.CONTEST_STATUS_OPEN);
        L.add(ContestEntity.CONTEST_STATUS_CLOSED);
        L.add(ContestEntity.CONTEST_STATUS_DISABLED);
        L.add(ContestEntity.CONTEST_STATUS_RUNNING);
        L.add(ContestEntity.CONTEST_STATUS_COMPLETED);
        return L;
    }

    public static List<String> getSubmissionActionTypes() {
        List<String> L = new ArrayList();
        L.add(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE);
        L.add(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_ONLY);
        return L;
    }

    public static List<String> getParticipantViewResultModes() {
        List<String> L = new ArrayList();
//        L.add(CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER);
//        L.add(CONTEST_PARTICIPANT_VIEW_MODE_NOT_SEE_CORRECT_ANSWER);
//        L.add(CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER_AND_PRIVATE_TESTCASE);
//        L.add(CONTEST_PARTICIPANT_VIEW_MODE_SEE_CORRECT_ANSWER_AND_PRIVATE_TESTCASE_SHORT);
        L.add(CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_ENABLED);
        L.add(CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_DISABLED);
        return L;
    }

    public static List<Integer> getListMaxNumberSubmissions() {
        List<Integer> L = new ArrayList();
        for (int i = 0; i <= 200; i++) {
            L.add(i);
        }
        return L;
    }

    @Id
    @Column(name = "contest_id")
    private String contestId;

    @Column(name = "contest_name")
    private String contestName;

//    @OneToOne
//    @JoinColumn(name = "user_create_id", referencedColumnName = "user_login_id")
//    private UserLogin userCreatedContest;

    @Column(name = "user_create_id")
    private String userId;

    @Column(name = "contest_solving_time")
    private long contestSolvingTime;

    @JoinTable(
        name = "contest_contest_problem_new",
        joinColumns = @JoinColumn(name = "contest_id", referencedColumnName = "contest_id"),
        inverseJoinColumns = @JoinColumn(name = "problem_id", referencedColumnName = "problem_id")
    )
    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProblemEntity> problems;

    @Column(name = "try_again")
    private boolean tryAgain;

    @Column(name = "created_stamp")
    private Date createdAt;

    @Column(name = "started_at")
    private Date startedAt;

    @Column(name = "count_down")
    private long countDown;

    @Column(name = "started_count_down_time")
    private Date startedCountDownTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "submission_action_type")
    private String submissionActionType;

    @Column(name = "max_number_submission")
    private int maxNumberSubmissions;

    @Column(name = "participant_view_result_mode")
    private String participantViewResultMode;

    @Column(name = "problem_description_view_type")
    private String problemDescriptionViewType;

    @Column(name = "use_cache_contest_problem")
    private String useCacheContestProblem;

    @Column(name = "max_source_code_length")
    private int maxSourceCodeLength;

    @Column(name = "evaluate_both_public_private_testcase")
    private String evaluateBothPublicPrivateTestcase;

    @Column(name = "min_time_between_two_submissions")
    private long minTimeBetweenTwoSubmissions;

    @Column(name = "judge_mode")
    private String judgeMode; // synchronous or asynchronous using queue


}
