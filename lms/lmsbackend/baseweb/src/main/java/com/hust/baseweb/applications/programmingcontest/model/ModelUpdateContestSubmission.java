package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelUpdateContestSubmission {
    private UUID contestSubmissionId;
    private String modifiedSourceCodeSubmitted;
    private String language;
    private String problemId;
    private String contestId;
}
