package com.hust.baseweb.applications.programmingcontest.model;

import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelGetContestInfosOfSubmissionOutput {
    private UUID submissionId;
    private String contestId;
    private List<String> problemIds;
    private List<ProblemEntity> problems;
}
