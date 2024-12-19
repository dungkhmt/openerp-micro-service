package com.hust.baseweb.applications.exam.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.exam.entity.ExamTestEntity;
import com.hust.baseweb.applications.exam.entity.ExamTestQuestionEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.*;
import com.hust.baseweb.applications.exam.repository.ExamTestQuestionRepository;
import com.hust.baseweb.applications.exam.repository.ExamTestRepository;
import com.hust.baseweb.applications.exam.service.ExamTestService;
import com.hust.baseweb.applications.exam.utils.DataUtils;
import com.hust.baseweb.applications.exam.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamTestServiceImpl implements ExamTestService {

    private final ExamTestRepository examTestRepository;
    private final ExamTestQuestionRepository examTestQuestionRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<ExamTestEntity> filter(Pageable pageable, ExamTestFilterReq examTestFilterReq) {
        return examTestRepository.filter(pageable, SecurityUtils.getUserLogin(),
                                         DataUtils.formatStringValueSql(examTestFilterReq.getKeyword()),
                                         DataUtils.formatStringValueSqlToLocalDateTime(examTestFilterReq.getCreatedFrom(), true),
                                         DataUtils.formatStringValueSqlToLocalDateTime(examTestFilterReq.getCreatedTo(), false));
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
    public ResponseData<ExamTestEntity> update(ExamTestSaveReq examTestSaveReq) {
        ResponseData<ExamTestEntity> responseData = new ResponseData<>();

        Optional<ExamTestEntity> examTestExist = examTestRepository.findByCode(examTestSaveReq.getCode());
        if(!examTestExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            responseData.setResultCode(HttpStatus.ALREADY_REPORTED.value());
            responseData.setResultMsg("Chưa tồn tại đề thi");
            return responseData;
        }

        ExamTestEntity examTestEntity = modelMapper.map(examTestSaveReq, ExamTestEntity.class);
        examTestEntity.setId(examTestExist.get().getId());
        examTestEntity.setCreatedBy(examTestExist.get().getCreatedBy());
        examTestEntity.setCreatedAt(examTestExist.get().getCreatedAt());
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
        responseData.setResultMsg("Cập nhật đề thi thành công");
        return responseData;
    }

    @Override
    public ResponseData<ExamTestEntity> delete(ExamTestDeleteReq examTestDeleteReq) {
        ResponseData<ExamTestEntity> responseData = new ResponseData<>();
        Optional<ExamTestEntity> examTestExist = examTestRepository.findById(examTestDeleteReq.getId());
        if(!examTestExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            responseData.setResultCode(HttpStatus.ALREADY_REPORTED.value());
            responseData.setResultMsg("Chưa tồn tại đề thi");
            return responseData;
        }
        examTestRepository.delete(examTestExist.get());
        List<ExamTestQuestionEntity> testQuestionEntityList = examTestQuestionRepository.findAllByExamTestId(examTestDeleteReq.getId());
        examTestQuestionRepository.deleteAll(testQuestionEntityList);
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Xoá đề thi thành công");
        return responseData;
    }
}
