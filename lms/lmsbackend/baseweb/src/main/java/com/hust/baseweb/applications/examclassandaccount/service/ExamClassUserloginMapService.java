package com.hust.baseweb.applications.examclassandaccount.service;

import com.hust.baseweb.applications.examclassandaccount.entity.ExamClassUserloginMap;
import com.hust.baseweb.applications.examclassandaccount.model.UserLoginModel;

import java.util.List;
import java.util.UUID;

public interface ExamClassUserloginMapService {
    List<ExamClassUserloginMap> getExamClassUserloginMap(UUID examClassId);

    List<ExamClassUserloginMap> createExamClassAccount(UUID examClassId, List<UserLoginModel> users);


}
