package com.hust.baseweb.applications.exam.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.exam.entity.ExamEntity;
import com.hust.baseweb.applications.exam.entity.ExamTestEntity;
import com.hust.baseweb.applications.exam.entity.ExamTestQuestionEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.*;
import com.hust.baseweb.applications.exam.model.response.ExamTestDetailsRes;
import com.hust.baseweb.applications.exam.model.response.ExamTestQuestionDetailsRes;
import com.hust.baseweb.applications.exam.repository.ExamRepository;
import com.hust.baseweb.applications.exam.repository.ExamTestQuestionRepository;
import com.hust.baseweb.applications.exam.repository.ExamTestRepository;
import com.hust.baseweb.applications.exam.service.ExamTestService;
import com.hust.baseweb.applications.exam.utils.DataUtils;
import com.hust.baseweb.applications.exam.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamTestServiceImpl implements ExamTestService {

    private final ExamRepository examRepository;
    private final ExamTestRepository examTestRepository;
    private final ExamTestQuestionRepository examTestQuestionRepository;
    private final EntityManager entityManager;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<ExamTestEntity> filter(Pageable pageable, ExamTestFilterReq examTestFilterReq) {
        StringBuilder sql = new StringBuilder();
        sql.append("select\n" +
                   "    *\n" +
                   "from\n" +
                   "    exam_test et\n" +
                   "where\n" +
                   "    et.created_by = :userLogin \n");

        if(DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getKeyword())){
            sql.append("and\n" +
                       "    ((lower(et.code) like CONCAT('%', LOWER(:keyword),'%')) or \n" +
                       "    (lower(et.name) like CONCAT('%', LOWER(:keyword),'%'))) \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getCreatedFrom()) &&
           DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getCreatedTo())){
            sql.append("and\n" +
                       "    et.created_at between :createdFrom and :createdTo \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getCreatedFrom()) &&
           !DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getCreatedTo())){
            sql.append("and\n" +
                       "    et.created_at >= :createdFrom \n");
        }
        if(!DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getCreatedFrom()) &&
           DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getCreatedTo())){
            sql.append("and\n" +
                       "    et.created_at <= :createdTo \n");
        }
        sql.append("order by created_at desc\n");

        Query query = entityManager.createNativeQuery(sql.toString(), ExamTestEntity.class);
        Query count = entityManager.createNativeQuery("select count(1) FROM (" + sql + ") as count");
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        query.setParameter("userLogin", SecurityUtils.getUserLogin());
        count.setParameter("userLogin", SecurityUtils.getUserLogin());
        if(DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getKeyword())){
            query.setParameter("keyword", DataUtils.escapeSpecialCharacters(examTestFilterReq.getKeyword()));
            count.setParameter("keyword", DataUtils.escapeSpecialCharacters(examTestFilterReq.getKeyword()));
        }
        if(DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getCreatedFrom())){
            query.setParameter("createdFrom", DataUtils.formatStringValueSqlToLocalDateTime(examTestFilterReq.getCreatedFrom(), true));
            count.setParameter("createdFrom", DataUtils.formatStringValueSqlToLocalDateTime(examTestFilterReq.getCreatedFrom(), true));
        }
        if(DataUtils.stringIsNotNullOrEmpty(examTestFilterReq.getCreatedTo())){
            query.setParameter("createdTo", DataUtils.formatStringValueSqlToLocalDateTime(examTestFilterReq.getCreatedTo(), false));
            count.setParameter("createdTo", DataUtils.formatStringValueSqlToLocalDateTime(examTestFilterReq.getCreatedTo(), false));
        }

        long totalRecord = ((BigInteger) count.getSingleResult()).longValue();
        List<ExamTestEntity> list = query.getResultList();
        return new PageImpl<>(list, pageable, totalRecord);
    }

    @Override
    public ResponseData<ExamTestDetailsRes> details(ExamTestDetailsReq examTestDetailsReq) {
        ResponseData<ExamTestDetailsRes> responseData = new ResponseData<>();

        Optional<ExamTestEntity> examTestEntity = examTestRepository.findById(examTestDetailsReq.getId());
        if(!examTestEntity.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại đề thi");
            return responseData;
        }

        List<ExamTestQuestionDetailsRes> list = examTestRepository.details(SecurityUtils.getUserLogin(),
                                                                           examTestDetailsReq.getId());

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Success");
        responseData.setData(ExamTestDetailsRes.builder()
                                 .id(examTestEntity.get().getId())
                                 .code(examTestEntity.get().getCode())
                                 .name(examTestEntity.get().getName())
                                 .description(examTestEntity.get().getDescription())
                                 .examTestQuestionDetails(list).build());
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamTestEntity> create(ExamTestSaveReq examTestSaveReq) {
        ResponseData<ExamTestEntity> responseData = new ResponseData<>();

        Optional<ExamTestEntity> examTestExist = examTestRepository.findByCode(examTestSaveReq.getCode());
        if(examTestExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            responseData.setResultCode(HttpStatus.ALREADY_REPORTED.value());
            responseData.setResultMsg("Đã tồn tại mã đề thi");
            return responseData;
        }

        ExamTestEntity examTestEntity = modelMapper.map(examTestSaveReq, ExamTestEntity.class);
        examTestEntity = examTestRepository.save(examTestEntity);

        List<ExamTestQuestionEntity> examTestQuestionEntityList = new ArrayList<>();
        for(ExamTestQuestionSaveReq examTestQuestionSaveReq: examTestSaveReq.getExamTestQuestionSaveReqList()){
            ExamTestQuestionEntity examTestQuestionEntity = modelMapper.map(examTestQuestionSaveReq, ExamTestQuestionEntity.class);
            examTestQuestionEntity.setId(UUID.randomUUID().toString());
            examTestQuestionEntity.setExamTestId(examTestEntity.getId());
            examTestQuestionEntityList.add(examTestQuestionEntity);
        }
        examTestQuestionRepository.saveAll(examTestQuestionEntityList);

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Thêm mới đề thi thành công");
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamTestEntity> update(ExamTestSaveReq examTestSaveReq) {
        ResponseData<ExamTestEntity> responseData = new ResponseData<>();

        Optional<ExamTestEntity> examTestExist = examTestRepository.findByCode(examTestSaveReq.getCode());
        if(!examTestExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại đề thi");
            return responseData;
        }

        ExamTestEntity examTestEntity = modelMapper.map(examTestSaveReq, ExamTestEntity.class);
        examTestEntity.setId(examTestExist.get().getId());
        examTestEntity.setCreatedBy(examTestExist.get().getCreatedBy());
        examTestEntity.setCreatedAt(examTestExist.get().getCreatedAt());
        examTestRepository.save(examTestEntity);

        List<ExamTestQuestionEntity> examTestQuestionEntityList = new ArrayList<>();
        for(ExamTestQuestionSaveReq examTestQuestionSaveReq: examTestSaveReq.getExamTestQuestionSaveReqList()){
            ExamTestQuestionEntity examTestQuestionEntity = modelMapper.map(examTestQuestionSaveReq, ExamTestQuestionEntity.class);
            if(!DataUtils.stringIsNotNullOrEmpty(examTestQuestionEntity.getId())){
                examTestQuestionEntity.setId(UUID.randomUUID().toString());
            }
            examTestQuestionEntity.setExamTestId(examTestEntity.getId());
            examTestQuestionEntityList.add(examTestQuestionEntity);
        }
        examTestQuestionRepository.saveAll(examTestQuestionEntityList);

        List<ExamTestQuestionEntity> examTestQuestionDeleteList = new ArrayList<>();
        for(ExamTestQuestionSaveReq examTestQuestion: examTestSaveReq.getExamTestQuestionDeleteReqList()){
            Optional<ExamTestQuestionEntity> examTestQuestionEntity = examTestQuestionRepository.findById(examTestQuestion.getId());
            examTestQuestionEntity.ifPresent(examTestQuestionDeleteList::add);
        }
        examTestQuestionRepository.deleteAll(examTestQuestionDeleteList);

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Cập nhật đề thi thành công");
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamTestEntity> delete(ExamTestDeleteReq examTestDeleteReq) {
        ResponseData<ExamTestEntity> responseData = new ResponseData<>();
        Optional<ExamTestEntity> examTestExist = examTestRepository.findById(examTestDeleteReq.getId());
        if(!examTestExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại đề thi");
            return responseData;
        }

        List<ExamEntity> examEntityList = examRepository.findALlByExamTestId(examTestDeleteReq.getId());
        if(!examEntityList.isEmpty()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Đề thi đã được gán cho bài thi, không được xoá");
            return responseData;
        }

        List<ExamTestQuestionEntity> testQuestionEntityList = examTestQuestionRepository.findAllByExamTestId(examTestDeleteReq.getId());
        examTestQuestionRepository.deleteAll(testQuestionEntityList);
        examTestRepository.delete(examTestExist.get());
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Xoá đề thi thành công");
        return responseData;
    }
}
