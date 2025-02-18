package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.entity.UserRegistrationContestEntity;
import com.hust.baseweb.applications.programmingcontest.repo.UserRegistrationContestRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserRegistrationContestServiceImpl implements  UserRegistrationContestService{

    private UserRegistrationContestRepo userRegistrationContestRepo;
    @Override
    public String findUserFullnameOfContest(String contestId, String userId) {
        List<UserRegistrationContestEntity> lst = userRegistrationContestRepo.findUserRegistrationContestEntityByContestIdAndUserId(contestId,userId);
        if(lst != null && lst.size() > 0){
            UserRegistrationContestEntity u = lst.get(0);
            return u.getFullname();
        }
        return null;
    }
}
