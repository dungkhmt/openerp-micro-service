package com.hust.baseweb.applications.exam.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.exam.entity.ExamQuestionEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionDeleteReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionFilterReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionSaveReq;
import com.hust.baseweb.applications.exam.repository.ExamQuestionRepository;
import com.hust.baseweb.applications.exam.service.ExamQuestionService;
import com.hust.baseweb.applications.exam.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<ExamQuestionEntity> filter(Pageable pageable, ExamQuestionFilterReq examQuestionFilterReq) {
        return examQuestionRepository.filter(pageable, DataUtils.formatStringValueSql(examQuestionFilterReq.getCode()),
                                             DataUtils.formatStringValueSql(examQuestionFilterReq.getContent()),
                                             examQuestionFilterReq.getTypes());
    }

    @Override
    public ResponseData<ExamQuestionEntity> create(ExamQuestionSaveReq examQuestionSaveReq) {
        ResponseData<ExamQuestionEntity> responseData = new ResponseData<>();
        Optional<ExamQuestionEntity> examQuestionExist = examQuestionRepository.findByCode(examQuestionSaveReq.getCode());
        if(examQuestionExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            responseData.setResultCode(HttpStatus.ALREADY_REPORTED.value());
            responseData.setResultMsg("Đã tồn tại mã câu hỏi");
            return responseData;
        }

        ExamQuestionEntity examQuestionEntity = modelMapper.map(examQuestionSaveReq, ExamQuestionEntity.class);
        examQuestionRepository.save(examQuestionEntity);
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Thêm mới câu hỏi thành công");
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamQuestionEntity> update(ExamQuestionSaveReq examQuestionSaveReq) {
        ResponseData<ExamQuestionEntity> responseData = new ResponseData<>();
        Optional<ExamQuestionEntity> examQuestionExist = examQuestionRepository.findByCode(examQuestionSaveReq.getCode());
        if(!examQuestionExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại câu hỏi");
            return responseData;
        }

        ExamQuestionEntity examQuestionEntity = modelMapper.map(examQuestionSaveReq, ExamQuestionEntity.class);
        examQuestionEntity.setId(examQuestionExist.get().getId());
        examQuestionEntity.setCreatedBy(examQuestionExist.get().getCreatedBy());
        examQuestionEntity.setCreatedAt(examQuestionExist.get().getCreatedAt());
        examQuestionRepository.save(examQuestionEntity);
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Cập nhật câu hỏi thành công");
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamQuestionEntity> delete(ExamQuestionDeleteReq examQuestionDeleteReq) {
        ResponseData<ExamQuestionEntity> responseData = new ResponseData<>();
        Optional<ExamQuestionEntity> examQuestionExist = examQuestionRepository.findById(examQuestionDeleteReq.getId());
        if(!examQuestionExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại câu hỏi");
            return responseData;
        }
        examQuestionRepository.delete(examQuestionExist.get());
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Xoá câu hỏi thành công");
        return responseData;
    }
}
