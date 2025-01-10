package com.hust.baseweb.applications.exam.service;

import com.hust.baseweb.applications.exam.entity.ExamQuestionEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionDeleteReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionDetailsReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionFilterReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionSaveReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ExamQuestionService {

    Page<ExamQuestionEntity> filter(Pageable pageable, ExamQuestionFilterReq examQuestionFilterReq);
    ResponseData<ExamQuestionEntity> details(ExamQuestionDetailsReq examQuestionDetailsReq);
    ResponseData<ExamQuestionEntity> create(ExamQuestionSaveReq examQuestionSaveReq, MultipartFile[] files);
    ResponseData<ExamQuestionEntity> update(ExamQuestionSaveReq examQuestionSaveReq, MultipartFile[] files);
    ResponseData<ExamQuestionEntity> delete(ExamQuestionDeleteReq examQuestionDeleteReq);
}
