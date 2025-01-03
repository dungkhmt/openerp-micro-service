package com.hust.baseweb.applications.exam.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.exam.entity.ExamEntity;
import com.hust.baseweb.applications.exam.entity.ExamStudentEntity;
import com.hust.baseweb.applications.exam.entity.ExamTestEntity;
import com.hust.baseweb.applications.exam.entity.ExamTestQuestionEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.*;
import com.hust.baseweb.applications.exam.model.response.ExamDetailsRes;
import com.hust.baseweb.applications.exam.model.response.ExamTestDetailsRes;
import com.hust.baseweb.applications.exam.model.response.ExamTestQuestionDetailsRes;
import com.hust.baseweb.applications.exam.repository.ExamRepository;
import com.hust.baseweb.applications.exam.repository.ExamStudentRepository;
import com.hust.baseweb.applications.exam.repository.ExamTestQuestionRepository;
import com.hust.baseweb.applications.exam.repository.ExamTestRepository;
import com.hust.baseweb.applications.exam.service.ExamService;
import com.hust.baseweb.applications.exam.service.ExamTestService;
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
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamTestRepository examTestRepository;
    private final ExamStudentRepository examStudentRepository;
    private final EntityManager entityManager;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final ExamTestService examTestService;


    @Override
    public Page<ExamEntity> filter(Pageable pageable, ExamFilterReq examFilterReq) {
        StringBuilder sql = new StringBuilder();
        sql.append("select\n" +
                   "    *\n" +
                   "from\n" +
                   "    exam e\n" +
                   "where\n" +
                   "    e.created_by = :userLogin \n");
        if(examFilterReq.getStatus() != null){
            sql.append("and\n" +
                       "    e.status = :status \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getKeyword())){
            sql.append("and\n" +
                       "    ((lower(e.code) like CONCAT('%', LOWER(:keyword),'%')) or \n" +
                       "    (lower(e.name) like CONCAT('%', LOWER(:keyword),'%'))) \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getStartTimeFrom()) &&
           DataUtils.stringIsNotNullOrEmpty(examFilterReq.getStartTimeTo())){
            sql.append("and\n" +
                       "    e.start_time between :startTimeFrom and :startTimeTo \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getStartTimeFrom()) &&
           !DataUtils.stringIsNotNullOrEmpty(examFilterReq.getStartTimeTo())){
            sql.append("and\n" +
                       "    e.start_time >= :startTimeFrom \n");
        }
        if(!DataUtils.stringIsNotNullOrEmpty(examFilterReq.getStartTimeFrom()) &&
           DataUtils.stringIsNotNullOrEmpty(examFilterReq.getStartTimeTo())){
            sql.append("and\n" +
                       "    e.start_time <= :startTimeTo \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getEndTimeFrom()) &&
           DataUtils.stringIsNotNullOrEmpty(examFilterReq.getEndTimeTo())){
            sql.append("and\n" +
                       "    e.end_time between :endTimeFrom and :endTimeTo \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getEndTimeFrom()) &&
           !DataUtils.stringIsNotNullOrEmpty(examFilterReq.getEndTimeTo())){
            sql.append("and\n" +
                       "    e.end_time >= :endTimeFrom \n");
        }
        if(!DataUtils.stringIsNotNullOrEmpty(examFilterReq.getEndTimeFrom()) &&
           DataUtils.stringIsNotNullOrEmpty(examFilterReq.getEndTimeTo())){
            sql.append("and\n" +
                       "    e.end_time <= :endTimeTo \n");
        }
        sql.append("order by start_time desc\n");

        Query query = entityManager.createNativeQuery(sql.toString(), ExamEntity.class);
        Query count = entityManager.createNativeQuery("select count(1) FROM (" + sql + ") as count");
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        query.setParameter("userLogin", SecurityUtils.getUserLogin());
        count.setParameter("userLogin", SecurityUtils.getUserLogin());
        if(examFilterReq.getStatus() != null){
            query.setParameter("status", examFilterReq.getStatus());
            count.setParameter("status", examFilterReq.getStatus());
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getKeyword())){
            query.setParameter("keyword", DataUtils.escapeSpecialCharacters(examFilterReq.getKeyword()));
            count.setParameter("keyword", DataUtils.escapeSpecialCharacters(examFilterReq.getKeyword()));
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getStartTimeFrom())){
            query.setParameter("startTimeFrom", DataUtils.formatStringValueSqlToLocalDateTime(examFilterReq.getStartTimeFrom(), true));
            count.setParameter("startTimeFrom", DataUtils.formatStringValueSqlToLocalDateTime(examFilterReq.getStartTimeFrom(), true));
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getStartTimeTo())){
            query.setParameter("startTimeTo", DataUtils.formatStringValueSqlToLocalDateTime(examFilterReq.getStartTimeTo(), false));
            count.setParameter("startTimeTo", DataUtils.formatStringValueSqlToLocalDateTime(examFilterReq.getStartTimeTo(), false));
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getEndTimeFrom())){
            query.setParameter("endTimeFrom", DataUtils.formatStringValueSqlToLocalDateTime(examFilterReq.getEndTimeFrom(), true));
            count.setParameter("endTimeFrom", DataUtils.formatStringValueSqlToLocalDateTime(examFilterReq.getEndTimeFrom(), true));
        }
        if(DataUtils.stringIsNotNullOrEmpty(examFilterReq.getEndTimeTo())){
            query.setParameter("endTimeTo", DataUtils.formatStringValueSqlToLocalDateTime(examFilterReq.getEndTimeTo(), false));
            count.setParameter("endTimeTo", DataUtils.formatStringValueSqlToLocalDateTime(examFilterReq.getEndTimeTo(), false));
        }

        long totalRecord = ((BigInteger) count.getSingleResult()).longValue();
        List<ExamEntity> list = query.getResultList();
        return new PageImpl<>(list, pageable, totalRecord);
    }

    @Override
    public ResponseData<ExamDetailsRes> details(ExamDetailsReq examDetailsReq) {
        ResponseData<ExamDetailsRes> responseData = new ResponseData<>();

        Optional<ExamEntity> examEntityExist = examRepository.findById(examDetailsReq.getId());
        if(!examEntityExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn kỳ thi");
            return responseData;
        }

        ExamDetailsRes examDetailsRes = modelMapper.map(examEntityExist.get(), ExamDetailsRes.class);

        List<ExamTestDetailsRes> examTests = new ArrayList<>();
        examTests.add(examTestService.details(new ExamTestDetailsReq(examDetailsRes.getExamTestId())).getData());
        examDetailsRes.setExamTests(examTests);

        examDetailsRes.setExamStudents(examStudentRepository.findALlByExamId(examDetailsRes.getId()));

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Success");
        responseData.setData(examDetailsRes);
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamEntity> create(ExamSaveReq examSaveReq) {
        ResponseData<ExamEntity> responseData = new ResponseData<>();

        Optional<ExamTestEntity> examTestEntity = examTestRepository.findById(examSaveReq.getExamTestId());
        if(!examTestEntity.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại đề thi");
            return responseData;
        }

        Optional<ExamEntity> examEntityExist = examRepository.findByCode(examSaveReq.getCode());
        if(examEntityExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Đã tồn tại kỳ thi");
            return responseData;
        }

        ExamEntity examEntity = modelMapper.map(examSaveReq, ExamEntity.class);
        examEntity.setStartTime(DataUtils.formatStringToLocalDateTime(examSaveReq.getStartTime()));
        examEntity.setEndTime(DataUtils.formatStringToLocalDateTime(examSaveReq.getEndTime()));
        examEntity = examRepository.save(examEntity);

        if(!examSaveReq.getExamStudents().isEmpty()){
            for(ExamStudentEntity examStudent: examSaveReq.getExamStudents()){
                examStudent.setExamId(examEntity.getId());
                examStudent.setExamTestId(examSaveReq.getExamTestId());
            }
            examStudentRepository.saveAll(examSaveReq.getExamStudents());
        }

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Thêm mới kỳ thi thành công");
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamEntity> update(ExamSaveReq examSaveReq) {
        ResponseData<ExamEntity> responseData = new ResponseData<>();

        Optional<ExamTestEntity> examTestEntity = examTestRepository.findById(examSaveReq.getExamTestId());
        if(!examTestEntity.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại đề thi");
            return responseData;
        }

        Optional<ExamEntity> examEntityExist = examRepository.findByCode(examSaveReq.getCode());
        if(!examEntityExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại kỳ thi");
            return responseData;
        }

        ExamEntity examEntity = modelMapper.map(examSaveReq, ExamEntity.class);
        examEntity.setId(examEntityExist.get().getId());
        examEntity.setCreatedBy(examEntityExist.get().getCreatedBy());
        examEntity.setCreatedAt(examEntityExist.get().getCreatedAt());
        examEntity.setStartTime(DataUtils.formatStringToLocalDateTime(examSaveReq.getStartTime()));
        examEntity.setEndTime(DataUtils.formatStringToLocalDateTime(examSaveReq.getEndTime()));
        examRepository.save(examEntity);

        if(!examSaveReq.getExamStudents().isEmpty()){
            for(ExamStudentEntity examStudent: examSaveReq.getExamStudents()){
                examStudent.setExamId(examEntity.getId());
                examStudent.setExamTestId(examSaveReq.getExamTestId());
            }
            examStudentRepository.saveAll(examSaveReq.getExamStudents());
        }

        if(!examSaveReq.getExamStudentDeletes().isEmpty()){
            examStudentRepository.deleteAll(examSaveReq.getExamStudentDeletes());
        }

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Cập nhật kỳ thi thành công");
        return responseData;
    }

    @Override
    public ResponseData<ExamEntity> delete(ExamDeleteReq examDeleteReq) {
        ResponseData<ExamEntity> responseData = new ResponseData<>();

        Optional<ExamEntity> examEntityExist = examRepository.findById(examDeleteReq.getId());
        if(!examEntityExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại kỳ thi");
            return responseData;
        }

        examRepository.delete(examEntityExist.get());

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Xoá kỳ thi thành công");
        return responseData;
    }
}
