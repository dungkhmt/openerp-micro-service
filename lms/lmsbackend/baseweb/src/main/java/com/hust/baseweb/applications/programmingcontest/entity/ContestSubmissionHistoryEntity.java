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
//@Table(name = "contest_problem")
@Table(name = "contest_submission_history")
public class ContestSubmissionHistoryEntity {
    @Id
    @Column(name = "contest_submission_history_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID contestSubmissionHistoryId;

    @Column(name="contest_submission_id")
    private UUID contestSubmissionId;

    @Column(name="modified_source_code_submitted")
    private String modifiedSourceCodeSubmitted;

    @Column(name="language")
    private String language;

    @Column(name="problem_id")
    private String problemId;

    @Column(name="contest_id")
    private String contestId;

    @Column(name="created_stamp")
    private Date createdStamp;

}
