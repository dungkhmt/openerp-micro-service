package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ContestSubmissionComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID submissionId;
    private String userId;
    private String comment;
    private Date createdStamp;
    private Date lastUpdatedStamp;
}
