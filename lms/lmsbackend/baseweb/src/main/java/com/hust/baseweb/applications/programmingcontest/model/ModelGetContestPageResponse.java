package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModelGetContestPageResponse {
    List<ModelGetContestResponse> contests;
    long count;
}
