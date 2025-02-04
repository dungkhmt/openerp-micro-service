package com.hust.baseweb.applications.exam.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.exam.entity.*;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.*;
import com.hust.baseweb.applications.exam.repository.ExamQuestionRepository;
import com.hust.baseweb.applications.exam.repository.ExamSubjectRepository;
import com.hust.baseweb.applications.exam.service.ExamSubjectService;
import com.hust.baseweb.applications.exam.utils.DataUtils;
import com.hust.baseweb.applications.exam.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamSubjectServiceImpl implements ExamSubjectService {

    private final ExamSubjectRepository examSubjectRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final EntityManager entityManager;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<ExamSubjectEntity> filter(Pageable pageable, ExamSubjectFilterReq examSubjectFilterReq) {
        StringBuilder sql = new StringBuilder();
        sql.append("select\n" +
                   "    *\n" +
                   "from\n" +
                   "    exam_subject es\n" +
                   "where\n" +
                   "    es.created_by = :userLogin \n");
        if(examSubjectFilterReq.getStatus() != null){
            sql.append("and\n" +
                       "    es.status = :status \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examSubjectFilterReq.getKeyword())){
            sql.append("and\n" +
                       "    ((lower(es.code) like CONCAT('%', LOWER(:keyword),'%')) or \n" +
                       "    (lower(es.name) like CONCAT('%', LOWER(:keyword),'%'))) \n");
        }
        sql.append("order by es.name asc\n");

        Query query = entityManager.createNativeQuery(sql.toString(), ExamSubjectEntity.class);
        Query count = entityManager.createNativeQuery("select count(1) FROM (" + sql + ") as count");
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        query.setParameter("userLogin", SecurityUtils.getUserLogin());
        count.setParameter("userLogin", SecurityUtils.getUserLogin());
        if(examSubjectFilterReq.getStatus() != null){
            query.setParameter("status", examSubjectFilterReq.getStatus());
            count.setParameter("status", examSubjectFilterReq.getStatus());
        }
        if(DataUtils.stringIsNotNullOrEmpty(examSubjectFilterReq.getKeyword())){
            query.setParameter("keyword", DataUtils.escapeSpecialCharacters(examSubjectFilterReq.getKeyword()));
            count.setParameter("keyword", DataUtils.escapeSpecialCharacters(examSubjectFilterReq.getKeyword()));
        }

        long totalRecord = ((BigInteger) count.getSingleResult()).longValue();
        List<ExamSubjectEntity> list = query.getResultList();
        return new PageImpl<>(list, pageable, totalRecord);
    }

    @Override
    public ResponseData<ExamSubjectEntity> create(ExamSubjectSaveReq examSubjectSaveReq) {
        ResponseData<ExamSubjectEntity> responseData = new ResponseData<>();
        Optional<ExamSubjectEntity> examSubjectExist = examSubjectRepository.findByCode(examSubjectSaveReq.getCode());
        if(examSubjectExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            responseData.setResultCode(HttpStatus.ALREADY_REPORTED.value());
            responseData.setResultMsg("Đã tồn tại mã môn học");
            return responseData;
        }

        ExamSubjectEntity examSubjectEntity = modelMapper.map(examSubjectSaveReq, ExamSubjectEntity.class);
        examSubjectRepository.save(examSubjectEntity);
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Thêm mới môn hc thành công");
        return responseData;
    }

    @Override
    public ResponseData<ExamSubjectEntity> update(ExamSubjectSaveReq examSubjectSaveReq) {
        ResponseData<ExamSubjectEntity> responseData = new ResponseData<>();
        Optional<ExamSubjectEntity> examSubjectExist = examSubjectRepository.findByCode(examSubjectSaveReq.getCode());
        if(!examSubjectExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại môn học");
            return responseData;
        }

        ExamSubjectEntity examSubjectEntity = modelMapper.map(examSubjectSaveReq, ExamSubjectEntity.class);
        examSubjectEntity.setId(examSubjectExist.get().getId());
        examSubjectEntity.setCreatedBy(examSubjectExist.get().getCreatedBy());
        examSubjectEntity.setCreatedAt(examSubjectExist.get().getCreatedAt());
        examSubjectRepository.save(examSubjectEntity);

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Cập nhật môn học thành công");
        return responseData;
    }

    @Override
    public ResponseData<ExamSubjectEntity> delete(ExamSubjectDeleteReq examSubjectDeleteReq) {
        ResponseData<ExamSubjectEntity> responseData = new ResponseData<>();
        Optional<ExamSubjectEntity> examSubjectExist = examSubjectRepository.findById(examSubjectDeleteReq.getId());
        if(!examSubjectExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại môn học");
            return responseData;
        }

        List<ExamQuestionEntity> examQuestionEntityList = examQuestionRepository.findAllByExamSubjectId(examSubjectDeleteReq.getId());
        if(!examQuestionEntityList.isEmpty()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Đã có câu hỏi thuộc môn học, không được xoá");
            return responseData;
        }

        examSubjectRepository.delete(examSubjectExist.get());
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Xoá môn học thành công");
        return responseData;
    }
}
