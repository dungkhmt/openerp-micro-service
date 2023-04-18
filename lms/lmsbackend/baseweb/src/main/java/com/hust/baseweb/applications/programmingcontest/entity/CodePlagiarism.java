package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;
import java.util.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "code_plagiarism")

public class CodePlagiarism {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="plagiarism_id")
    private UUID plagiarismId;

    @Column(name="contest_id")
    private String contestId;

    @Column(name="problem_id")
    private String problemId;

    @Column(name="user_id_1")
    private String userId1;

    @Column(name="user_id_2")
    private String userId2;

    @Column(name="user_fullname1")
    private String userFullname1;

    @Column(name="user_fullname2")
    private String userFullname2;

    @Column(name="source_code_1")
    private String sourceCode1;

    @Column(name="source_code_2")
    private String sourceCode2;

    @Column(name="submit_date1")
    private Date submitDate1;

    @Column(name="submit_date2")
    private Date submitDate2;


    @Column(name="score")
    private double score;

    @Column(name="created_stamp")
    private Date createdStamp;
}
