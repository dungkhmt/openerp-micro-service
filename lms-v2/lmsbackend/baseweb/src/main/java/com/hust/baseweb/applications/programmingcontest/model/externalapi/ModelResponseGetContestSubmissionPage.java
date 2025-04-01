package com.hust.baseweb.applications.programmingcontest.model.externalapi;

import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseGetContestSubmissionPage {
    private List<ContestSubmissionEntity> submissions;
}
