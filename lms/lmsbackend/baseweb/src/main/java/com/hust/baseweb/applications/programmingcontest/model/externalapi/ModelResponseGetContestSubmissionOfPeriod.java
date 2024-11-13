package com.hust.baseweb.applications.programmingcontest.model.externalapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseGetContestSubmissionOfPeriod {
    private Date fromDateTime;
    private Date toDateTime;
}
