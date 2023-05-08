package com.hust.baseweb.applications.education.quiztest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "edu_quiz_test")
public class EduQuizTest {
    public static final String QUIZ_TEST_STATUS_CREATED = "CREATED";
    public static final String QUIZ_TEST_STATUS_DISABLED = "DISABLED";
    public static final String QUIZ_TEST_STATUS_OPEN = "OPEN";
    public static final String QUIZ_TEST_STATUS_RUNNING = "RUNNING";
    public static final String QUIZ_TEST_STATUS_HIDDEN = "HIDDEN";


    public static final String QUIZ_TEST_VIEW_TYPE_LIST = "VIEW_LIST";
    public static final String QUIZ_TEST_VIEW_TYPE_STEP = "VIEW_STEP";

    public static final String QUESTION_STATEMENT_VIEW_TYPE_VISIBLE = "VISIBLE";
    public static final String QUESTION_STATEMENT_VIEW_TYPE_HIDDEN = "HIDDEN";

    public static final String PARTICIPANT_QUIZ_GROUP_ASSIGNMENT_MODE_ASSIGN_GROUP_BEFORE_HANDOUT = "ASSIGN_GROUP_BEFORE_HANDOUT"; // quiz group is assigned to participant by teacher on the system before hand-out
    public static final String PARTICIPANT_QUIZ_GROUP_ASSIGNMENT_MODE_HANDOUT_THEN_UPDATE_GROUP = "HANDOUT_THEN_UPDATE_GROUP";// quiz group questions is distributed (hard copy) randomly among participants and participants check and confirm update quiz group

    public static final String JUDGE_MODE_SYNCHRONOUS = "SYNCHRONOUS";
    public static final String JUDGE_MODE_ASYNCHRONOUS_QUEUE = "ASYNCHRONOUS_WITH_QUEUE";
    public static final String JUDGE_MODE_BATCH_LAZY_EVALUATION = "BATCH_LAZY_EVALUATION";
    public static final String JUDGE_MODE_OFFLINE_VIA_EXCEL_UPLOAD = "OFFLINE_VIA_EXCEL_UPLOAD";

    public static List<String> getListJudgeModes(){
        List<String> L = new ArrayList();
        L.add(JUDGE_MODE_SYNCHRONOUS);
        L.add(JUDGE_MODE_ASYNCHRONOUS_QUEUE);
        L.add(JUDGE_MODE_BATCH_LAZY_EVALUATION);
        L.add(JUDGE_MODE_OFFLINE_VIA_EXCEL_UPLOAD);
        return L;
    }
    public static List<String> getListStatusIds(){
        List<String> L = new ArrayList();
        L.add(QUIZ_TEST_STATUS_CREATED);
        L.add(QUIZ_TEST_STATUS_DISABLED);
        L.add(QUIZ_TEST_STATUS_OPEN);
        L.add(QUIZ_TEST_STATUS_HIDDEN);
        L.add(QUIZ_TEST_STATUS_RUNNING);
        return L;
    }
    public static List<String> getListParticipantQuizGroupAssignmentModes(){
        List<String> L = new ArrayList();
        L.add(PARTICIPANT_QUIZ_GROUP_ASSIGNMENT_MODE_ASSIGN_GROUP_BEFORE_HANDOUT);
        L.add(PARTICIPANT_QUIZ_GROUP_ASSIGNMENT_MODE_HANDOUT_THEN_UPDATE_GROUP);
        return L;
    }
    public static List<String> getListQuestionStatementViewType(){
        List<String> L = new ArrayList();
        L.add(EduQuizTest.QUESTION_STATEMENT_VIEW_TYPE_VISIBLE);
        L.add(EduQuizTest.QUESTION_STATEMENT_VIEW_TYPE_HIDDEN);
        return L;
    };
    public static List<String> getListQuizTestViewTypes(){
        List<String> L = new ArrayList();
        L.add(QUIZ_TEST_VIEW_TYPE_LIST);
        L.add(QUIZ_TEST_VIEW_TYPE_STEP);
        return L;
    }
    @Id
    @Column(name = "test_id")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String testId;

    @Column(name = "test_name")
    private String testName;

    @Column(name = "schedule_datetime")
    private Date scheduleDatetime;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "created_by_user_login_id")
    private String createdByUserLoginId;

    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "class_id")
    private UUID classId;

    @Column(name="session_id")
    private UUID sessionId;

    @Column(name="participant_quiz_group_assignment_mode")
    private String participantQuizGroupAssignmentMode;

    @Column(name="view_type_id")
    private String viewTypeId;

    @Column(name="question_statement_view_type_id")
    private String questionStatementViewTypeId;

    @Column(name="judge_mode")
    private String judgeMode;
}
