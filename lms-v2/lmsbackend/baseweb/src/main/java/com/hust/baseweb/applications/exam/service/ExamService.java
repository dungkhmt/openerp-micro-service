package com.hust.baseweb.applications.exam.service;

import com.hust.baseweb.applications.exam.entity.ExamEntity;
import com.hust.baseweb.applications.exam.entity.ExamResultEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.*;
import com.hust.baseweb.applications.exam.model.response.ExamDetailsRes;
import com.hust.baseweb.applications.exam.model.response.ExamMarkingDetailsRes;
import com.hust.baseweb.applications.exam.model.response.MyExamDetailsRes;
import com.hust.baseweb.applications.exam.model.response.MyExamFilterRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ExamService {

    Page<ExamEntity> filter(Pageable pageable, ExamFilterReq examFilterReq);

    ResponseData<ExamDetailsRes> details(ExamDetailsReq examDetailsReq);

    ResponseData<ExamEntity> create(ExamSaveReq examSaveReq);

    ResponseData<ExamEntity> update(ExamSaveReq examSaveReq);

    ResponseData<ExamEntity> delete(ExamDeleteReq examDeleteReq);

    Page<MyExamFilterRes> filterMyExam(Pageable pageable, MyExamFilterReq myExamFilterReq);

    ResponseData<MyExamDetailsRes> detailsMyExam(MyExamDetailsReq myExamDetailsReq);

    ResponseData<ExamResultEntity> doingMyExam(MyExamResultSaveReq myExamResultSaveReq, MultipartFile[] files);

    ResponseData<ExamMarkingDetailsRes> detailsExamMarking(String examStudentId);

    ResponseData<ExamResultEntity> markingExam(ExamMarkingSaveReq examMarkingSaveReq);
}
