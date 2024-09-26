package com.hust.baseweb.applications.programmingcontest.model;

import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelContestSubmissionMessage {

    private ModelContestSubmission modelContestSubmission;
    private ContestSubmissionEntity submission;
}
