package com.hust.baseweb.applications.programmingcontest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
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
    public static final String CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_INPUT_PARTICIPANT_OUTPUT = "ONLY_INPUT_PARTICIPANT_OUTPUT";




    public static final String CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_VISIBLE = "VISIBLE";
    public static final String CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_HIDDEN = "HIDDEN";

    public static final String EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_YES = "Y";
    public static final String EVALUATE_USE_BOTH_PUBLIC_PRIVATE_TESTCASE_NO = "N";

    public static final String SYNCHRONOUS_JUDGE_MODE = "SYNCHRONOUS_JUDGE_MODE";
    public static final String ASYNCHRONOUS_JUDGE_MODE_QUEUE = "ASYNCHRONOUS_JUDGE_MODE_QUEUE";

    public static final String SEND_CONFIRM_EMAIL_UPON_SUBMISSION_YES = "Y";
    public static final String SEND_CONFIRM_EMAIL_UPON_SUBMISSION_NO = "N";

    public static final String PARTICIPANT_VIEW_SUBMISSION_MODE_ENABLED = "ALLOW_VIEW";
    public static final String PARTICIPANT_VIEW_SUBMISSION_MODE_DISABLED = "FORBIDDEN";

    public static final String PROG_LANGUAGES_C = "C";
    public static final String PROG_LANGUAGES_CPP11 = "CPP11";
    public static final String PROG_LANGUAGES_CPP14 = "CPP14";
    public static final String PROG_LANGUAGES_CPP17 = "CPP17";
    public static final String PROG_LANGUAGES_JAVA = "JAVA";
    public static final String PROG_LANGUAGES_PYTHON3 = "PYTHON3";

    public static final String CONTEST_TYPE_REAL_TEST_WITH_EVALUATION = "REAL_TEST_WITH_EVALUATION";
    public static final String CONTEST_TYPE_TRAINING_NO_EVALUATION = "TRAINING_NO_EVALUATION";

    public static String CONTEST_SHOW_TAG_PROBLEMS_YES = "Y";
    public static String CONTEST_SHOW_TAG_PROBLEMS_NO = "N";

    public static String CONTEST_SHOW_COMMENT_YES = "Y";
    public static String CONTEST_SHOW_COMMENT_NO = "N";

    public static Boolean CONTEST_PUBLIC_YES = true;
    public static Boolean CONTEST_PUBLIC_NO = false;

    public static List<String> getListContestTypes(){
        List<String> L = new ArrayList();
        L.add(CONTEST_TYPE_REAL_TEST_WITH_EVALUATION);
        L.add(CONTEST_TYPE_TRAINING_NO_EVALUATION);
        return L;
    }
    public static List<Boolean> getListContestPublic(){
        List<Boolean> L = new ArrayList();
        L.add(CONTEST_PUBLIC_YES);
        L.add(CONTEST_PUBLIC_NO);
        return L;
    }

    public static List<String> getListContestShowTag(){
        List<String> L = new ArrayList();
        L.add(CONTEST_SHOW_TAG_PROBLEMS_YES);
        L.add(CONTEST_SHOW_TAG_PROBLEMS_NO);
        return L;
    }
    public static List<String> getListContestShowComment(){
        List<String> L = new ArrayList();
        L.add(CONTEST_SHOW_COMMENT_YES);
        L.add(CONTEST_SHOW_TAG_PROBLEMS_NO);
        return L;
    }
    public static List<String> getListParticipantViewSubmissionModes() {
        List<String> L = new ArrayList();
        L.add(PARTICIPANT_VIEW_SUBMISSION_MODE_ENABLED);
        L.add(PARTICIPANT_VIEW_SUBMISSION_MODE_DISABLED);
        return L;
    }

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
        L.add(CONTEST_PARTICIPANT_VIEW_TESTCASE_DETAIL_INPUT_PARTICIPANT_OUTPUT);
        return L;
    }

    public List<String> getListLanguagesAllowed() {
        List<String> L = new ArrayList<>();
//        if (languagesAllowed != null && !languagesAllowed.equals("")) {
//            String[] s = languagesAllowed.split(",");
//            if (s != null && s.length > 0) {
//                for (String l : s) {
//                    if (l != null && !l.equals("")) {
//                        L.add(l.trim());
//                    }
//                }
//            }
//        } else {// no limitation, take all languages
        L.add(ContestEntity.PROG_LANGUAGES_C);
        L.add(ContestEntity.PROG_LANGUAGES_CPP11);
        L.add(ContestEntity.PROG_LANGUAGES_CPP14);
        L.add(ContestEntity.PROG_LANGUAGES_CPP17);
        L.add(ContestEntity.PROG_LANGUAGES_JAVA);
        L.add(ContestEntity.PROG_LANGUAGES_PYTHON3);
//        }
        return L;
    }
    public List<String> getListLanguagesAllowedInContest() {
        List<String> L = new ArrayList<>();
        if (languagesAllowed != null && !languagesAllowed.equals("")) {
            String[] s = languagesAllowed.split(",");
            if (s != null && s.length > 0) {
                for (String l : s) {
                    if (l != null && !l.equals("")) {
                        L.add(l.trim());
                    }
                }
            }
        } else {// no limitation, take all languages
            L.add(ContestEntity.PROG_LANGUAGES_C);
            L.add(ContestEntity.PROG_LANGUAGES_CPP11);
            L.add(ContestEntity.PROG_LANGUAGES_CPP14);
            L.add(ContestEntity.PROG_LANGUAGES_CPP17);
            L.add(ContestEntity.PROG_LANGUAGES_JAVA);
            L.add(ContestEntity.PROG_LANGUAGES_PYTHON3);
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

    @Column(name = "created_stamp")
    private Date createdAt;

    @Column(name = "public")
    private Boolean contestPublic;

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

    @Column(name = "max_source_code_length")
    private int maxSourceCodeLength;

    @Column(name = "evaluate_both_public_private_testcase")
    private String evaluateBothPublicPrivateTestcase;

    @Column(name = "min_time_between_two_submissions")
    private long minTimeBetweenTwoSubmissions;

    @Column(name = "judge_mode")
    private String judgeMode; // synchronous or asynchronous using queue

    @Column(name = "send_confirm_email_upon_submission")
    private String sendConfirmEmailUponSubmission;

    @Column(name = "participant_view_submission_mode")
    private String participantViewSubmissionMode;

    @Column(name = "languages_allowed")
    private String languagesAllowed;

    @Column(name = "contest_type")
    private String contestType;

    @Column(name = "contest_show_tag")
    private String contestShowTag;

    @Column(name = "contest_show_comment")
    private String contestShowComment;

}
