package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "test_case_new")
public class TestCaseEntity implements Serializable {
    private static final long serialVersionUID = 3487495895819801L;

    public static final String STATUS_DISABLED = "DISABLED";
    public static final String STATUS_ENABLED = "ENABLED";

    @Id
    @Column(name = "test_case_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID testCaseId;

    @Column(name = "test_case_point")
    private int testCasePoint;

    @Column(name = "test_case")
    private String testCase;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Column(name = "contest_problem_id")
    private String problemId;

    @Column(name = "is_public")
    private String isPublic;

    @Column(name = "description")
    private String description;

    @Column(name = "status_id")
    private String statusId;

}
