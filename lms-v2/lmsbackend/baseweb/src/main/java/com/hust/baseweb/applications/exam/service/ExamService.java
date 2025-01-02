package com.hust.baseweb.applications.exam.service;

import com.hust.baseweb.applications.exam.entity.ExamEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.ExamDeleteReq;
import com.hust.baseweb.applications.exam.model.request.ExamDetailsReq;
import com.hust.baseweb.applications.exam.model.request.ExamFilterReq;
import com.hust.baseweb.applications.exam.model.request.ExamSaveReq;
import com.hust.baseweb.applications.exam.model.response.ExamDetailsRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExamService {

    Page<ExamEntity> filter(Pageable pageable, ExamFilterReq examFilterReq);

    ResponseData<ExamDetailsRes> details(ExamDetailsReq examDetailsReq);

    ResponseData<ExamEntity> create(ExamSaveReq examSaveReq);

    ResponseData<ExamEntity> update(ExamSaveReq examSaveReq);

    ResponseData<ExamEntity> delete(ExamDeleteReq examDeleteReq);
}
