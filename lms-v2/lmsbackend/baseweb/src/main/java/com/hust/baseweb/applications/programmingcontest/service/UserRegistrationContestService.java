package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.entity.UserRegistrationContestEntity;

public interface UserRegistrationContestService {
    public String findUserFullnameOfContest(String contestId, String userId);
}
