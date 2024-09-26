package com.hust.baseweb.applications.examclassandaccount.service;

import com.hust.baseweb.applications.examclassandaccount.entity.ExamClass;
import com.hust.baseweb.applications.examclassandaccount.model.ModelCreateExamClass;
import com.hust.baseweb.applications.examclassandaccount.model.ModelRepsonseExamClassDetail;

import java.util.List;
import java.util.UUID;

public interface ExamClassService {
    public List<ExamClass> getAllExamClass();

    public ExamClass createExamClass(String userLoginId, ModelCreateExamClass m);

    public boolean updateStatusExamClass(UUID examClassId, String status);

    public boolean clearAccountExamClass(UUID examClassId);

    public ModelRepsonseExamClassDetail getExamClassDetail(UUID examClassId);
}
