package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "contest_submission_testcase_new")

public class ContestSubmissionTestCaseEntity {
    @Id
    @Column(name = "contest_submission_testcase_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID contestSubmissionTestcaseId;

    @Column(name="contest_submission_id")
    private UUID contestSubmissionId;

    @Column(name="contest_id")
    private String contestId;

    @Column(name="problem_id")
    private String problemId;

    @Column(name="submitted_by_user_login_id")
    private String submittedByUserLoginId;

    @Column(name="test_case_id")
    private UUID testCaseId;

    @Column(name="point")
    private int point;

    @Column(name="status")
    private String status;

    @Column(name ="participant_solution_output")
    private String participantSolutionOtput;

    @Column(name="runtime")
    private Long runtime;

    @Column(name="memory_usage")
    private int memoryUsage;

    @Column(name="created_stamp")
    private Date createdStamp;

}
