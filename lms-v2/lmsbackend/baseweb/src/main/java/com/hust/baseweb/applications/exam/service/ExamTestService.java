package com.hust.baseweb.applications.exam.service;

import com.hust.baseweb.applications.exam.entity.ExamTestEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.ExamTestDetailsReq;
import com.hust.baseweb.applications.exam.model.request.ExamTestFilterReq;
import com.hust.baseweb.applications.exam.model.request.ExamTestDeleteReq;
import com.hust.baseweb.applications.exam.model.request.ExamTestSaveReq;
import com.hust.baseweb.applications.exam.model.response.ExamTestDetailsRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExamTestService {

    Page<ExamTestEntity> filter(Pageable pageable, ExamTestFilterReq examTestFilterReq);

    ResponseData<ExamTestDetailsRes> details(ExamTestDetailsReq examTestDetailsReq);

    ResponseData<ExamTestEntity> create(ExamTestSaveReq examTestSaveReq);

    ResponseData<ExamTestEntity> update(ExamTestSaveReq examTestSaveReq);

    ResponseData<ExamTestEntity> delete(ExamTestDeleteReq examTestDeleteReq);
}
