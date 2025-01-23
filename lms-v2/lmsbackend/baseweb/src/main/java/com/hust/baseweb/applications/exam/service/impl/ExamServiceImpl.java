package com.hust.baseweb.applications.exam.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.exam.entity.*;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.*;
import com.hust.baseweb.applications.exam.model.response.*;
import com.hust.baseweb.applications.exam.repository.*;
import com.hust.baseweb.applications.exam.service.ExamService;
import com.hust.baseweb.applications.exam.service.ExamTestService;
import com.hust.baseweb.applications.exam.service.MongoFileService;
import com.hust.baseweb.applications.exam.utils.Constants;
import com.hust.baseweb.applications.exam.utils.DataUtils;
import com.hust.baseweb.applications.exam.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamTestRepository examTestRepository;
    private final ExamStudentRepository examStudentRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamResultDetailsRepository examResultDetailsRepository;
    private final EntityManager entityManager;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final ExamTestService examTestService;
    private final MongoFileService mongoFileService;


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

        examDetailsRes.setExamStudents(examStudentRepository.findAllWithResult(examDetailsRes.getId()));

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

        List<ExamResultEntity> examResultEntities = examResultRepository.findAllByExamId(examDeleteReq.getId());
        if(!examResultEntities.isEmpty()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Đã có học sinh hoàn thành bài thi, không được xoá");
            return responseData;
        }

        examRepository.delete(examEntityExist.get());

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Xoá kỳ thi thành công");
        return responseData;
    }

    @Override
    public Page<MyExamFilterRes> filterMyExam(Pageable pageable, MyExamFilterReq myExamFilterReq) {
        StringBuilder sql = new StringBuilder();
        sql.append("select\n" +
                   "    es.id as examStudentId,\n" +
                   "    e.id as examId,\n" +
                   "    e.code as examCode,\n" +
                   "    e.name as examName,\n" +
                   "    e.description as examDescription,\n" +
                   "    e.start_time as startTime,\n" +
                   "    e.end_time as endTime,\n" +
                   "    et.id as examTestId,\n" +
                   "    et.code as examTestCode,\n" +
                   "    et.name as examTestName,\n" +
                   "    er.id as examResultId,\n" +
                   "    er.total_score as totalScore\n" +
                   "from\n" +
                   "    exam_student es\n" +
                   "left join exam e on\n" +
                   "    e.id = es.exam_id\n" +
                   "left join exam_test et on\n" +
                   "    et.id = es.exam_test_id\n" +
                   "left join exam_result er on\n" +
                   "    er.exam_student_id = es.id\n" +
                   "where\n" +
                   "    es.code = :userLogin\n" +
                   "    and e.status = 1 \n");
        if(myExamFilterReq.getStatus() != null){
            if(myExamFilterReq.getStatus().equals(Constants.MyExamStatus.NOT_DOING)){
                sql.append("and (er.id is null and er.total_score is null) \n");
            }else if(myExamFilterReq.getStatus().equals(Constants.MyExamStatus.NOT_SCORED)){
                sql.append("and (er.id is not null and er.total_score is null) \n");
            }else if(myExamFilterReq.getStatus().equals(Constants.MyExamStatus.SCORED)){
                sql.append("and (er.id is not null and er.total_score is not null) \n");
            }
        }
        if(DataUtils.stringIsNotNullOrEmpty(myExamFilterReq.getKeyword())){
            sql.append("and\n" +
                       "    ((lower(et.name) like CONCAT('%', LOWER(:keyword),'%')) or \n" +
                       "    (lower(e.name) like CONCAT('%', LOWER(:keyword),'%'))) \n");
        }
        sql.append("order by start_time desc\n");

        Query query = entityManager.createNativeQuery(sql.toString());
        Query count = entityManager.createNativeQuery("select count(1) FROM (" + sql + ") as count");
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        query.setParameter("userLogin", SecurityUtils.getUserLogin());
        count.setParameter("userLogin", SecurityUtils.getUserLogin());
        if(DataUtils.stringIsNotNullOrEmpty(myExamFilterReq.getKeyword())){
            query.setParameter("keyword", DataUtils.escapeSpecialCharacters(myExamFilterReq.getKeyword()));
            count.setParameter("keyword", DataUtils.escapeSpecialCharacters(myExamFilterReq.getKeyword()));
        }

        long totalRecord = ((BigInteger) count.getSingleResult()).longValue();
        List<Object[]> result = query.getResultList();
        List<MyExamFilterRes> list = new ArrayList<>();
        if (!Objects.isNull(result) && !result.isEmpty()) {
            for (Object[] obj : result) {
                list.add(MyExamFilterRes.builder()
                             .examStudentId(DataUtils.safeToString(obj[0]))
                             .examId(DataUtils.safeToString(obj[1]))
                             .examCode(DataUtils.safeToString(obj[2]))
                             .examName(DataUtils.safeToString(obj[3]))
                             .examDescription(DataUtils.safeToString(obj[4]))
                             .startTime(DataUtils.safeToString(obj[5]))
                             .endTime(DataUtils.safeToString(obj[6]))
                             .examTestId(DataUtils.safeToString(obj[7]))
                             .examTestCode(DataUtils.safeToString(obj[8]))
                             .examTestName(DataUtils.safeToString(obj[9]))
                             .examResultId(DataUtils.safeToString(obj[10]))
                             .totalScore(DataUtils.safeToDouble(obj[11])).build());
            }
        }
        return new PageImpl<>(list, pageable, totalRecord);
    }

    @Override
    public ResponseData<MyExamDetailsRes> detailsMyExam(MyExamDetailsReq myExamDetailsReq) {
        ResponseData<MyExamDetailsRes> responseData = new ResponseData<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select\n" +
                   "    es.id as examStudentId,\n" +
                   "    e.id as examId,\n" +
                   "    e.code as examCode,\n" +
                   "    e.name as examName,\n" +
                   "    e.description as examDescription,\n" +
                   "    e.start_time as startTime,\n" +
                   "    e.end_time as endTime,\n" +
                   "    et.id as examTestId,\n" +
                   "    et.code as examTestCode,\n" +
                   "    et.name as examTestName,\n" +
                   "    er.id as examResultId,\n" +
                   "    er.total_score as totalScore,\n" +
                   "    er.total_time as totalTime,\n" +
                   "    er.submited_at as submitedAt,\n" +
                   "    er.file_path as answerFiles,\n" +
                   "    er.comment as comment\n" +
                   "from\n" +
                   "    exam_student es\n" +
                   "left join exam e on\n" +
                   "    e.id = es.exam_id\n" +
                   "left join exam_test et on\n" +
                   "    et.id = es.exam_test_id\n" +
                   "left join exam_result er on\n" +
                   "    er.exam_student_id = es.id\n" +
                   "where\n" +
                   "    es.code = :userLogin\n" +
                   "    and e.id = :examId\n" +
                   "    and es.id = :examStudentId\n" +
                   "    and e.status = 1 \n");
        sql.append("order by start_time desc\n");

        Query query = entityManager.createNativeQuery(sql.toString());

        query.setParameter("userLogin", SecurityUtils.getUserLogin());
        query.setParameter("examId", myExamDetailsReq.getExamId());
        query.setParameter("examStudentId", myExamDetailsReq.getExamStudentId());
        List<Object[]> result = query.getResultList();
        List<MyExamDetailsRes> list = new ArrayList<>();
        if (!Objects.isNull(result) && !result.isEmpty()) {
            for (Object[] obj : result) {
                list.add(MyExamDetailsRes.builder()
                                        .examStudentId(DataUtils.safeToString(obj[0]))
                                        .examId(DataUtils.safeToString(obj[1]))
                                        .examCode(DataUtils.safeToString(obj[2]))
                                        .examName(DataUtils.safeToString(obj[3]))
                                        .examDescription(DataUtils.safeToString(obj[4]))
                                        .startTime(DataUtils.safeToString(obj[5]))
                                        .endTime(DataUtils.safeToString(obj[6]))
                                        .examTestId(DataUtils.safeToString(obj[7]))
                                        .examTestCode(DataUtils.safeToString(obj[8]))
                                        .examTestName(DataUtils.safeToString(obj[9]))
                                        .examResultId(DataUtils.safeToString(obj[10]))
                                        .totalScore(DataUtils.safeToInt(obj[11]))
                                        .totalTime(DataUtils.safeToInt(obj[12]))
                                        .submitedAt(DataUtils.safeToString(obj[13]))
                                        .answerFiles(DataUtils.safeToString(obj[14]))
                                        .comment(DataUtils.safeToString(obj[15]))
                                        .build());
            }
        }

        MyExamDetailsRes myExamDetailsRes = list.get(0);

        if(!DataUtils.stringIsNotNullOrEmpty(myExamDetailsRes.getExamResultId())){
            LocalDateTime now = DataUtils.getTimeNowWithZone();
            if(now.isBefore(DataUtils.formatStringToLocalDateTimeFull(myExamDetailsRes.getStartTime()))){
                responseData.setHttpStatus(HttpStatus.NOT_FOUND);
                responseData.setResultCode(HttpStatus.NOT_FOUND.value());
                responseData.setData(myExamDetailsRes);
                responseData.setResultMsg("Chưa đến thời gian thi");
                return responseData;
            }
            if(now.isAfter(DataUtils.formatStringToLocalDateTimeFull(myExamDetailsRes.getEndTime()))){
                responseData.setHttpStatus(HttpStatus.NOT_FOUND);
                responseData.setResultCode(HttpStatus.NOT_FOUND.value());
                responseData.setData(myExamDetailsRes);
                responseData.setResultMsg("Đã hết thời gian thi");
                return responseData;
            }
        }

        myExamDetailsRes.setQuestionList(examTestRepository.getMyExamQuestionDetails(myExamDetailsRes.getExamTestId(),
                                                                                     myExamDetailsRes.getExamStudentId()));

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setData(myExamDetailsRes);
        responseData.setResultMsg("Success");
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamResultEntity> doingMyExam(MyExamResultSaveReq myExamResultSaveReq, MultipartFile[] files) {
        ResponseData<ExamResultEntity> responseData = new ResponseData<>();

        ExamResultEntity examResultEntity = modelMapper.map(myExamResultSaveReq, ExamResultEntity.class);
        examResultEntity = examResultRepository.save(examResultEntity);

        for(MultipartFile file: files){
            String filename = file.getOriginalFilename();
            for(MyExamResultDetailsSaveReq examResultDetails: myExamResultSaveReq.getExamResultDetails()){
                if(Objects.equals(examResultDetails.getQuestionOrder(), getQuestionOrderFromFilename(filename))){
                    String filePath = mongoFileService.storeFile(file);
                    if(DataUtils.stringIsNotNullOrEmpty(examResultDetails.getFilePath())){
                        examResultDetails.setFilePath(examResultDetails.getFilePath() + ";" + filePath);
                    }else{
                        examResultDetails.setFilePath(filePath);
                    }
                }
            }

        }

        List<ExamResultDetailsEntity> examResultDetailsEntities = new ArrayList<>();
        for(MyExamResultDetailsSaveReq examResultDetails: myExamResultSaveReq.getExamResultDetails()){
            examResultDetails.setExamResultId(examResultEntity.getId());
            ExamResultDetailsEntity examResultDetailsEntity = modelMapper.map(examResultDetails, ExamResultDetailsEntity.class);
            examResultDetailsEntities.add(examResultDetailsEntity);
        }
        examResultDetailsRepository.saveAll(examResultDetailsEntities);

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Nộp bài thành công");
        return responseData;
    }
    private Integer getQuestionOrderFromFilename(String filename){
        if(DataUtils.stringIsNotNullOrEmpty(filename)){
            String[] fileParts = filename.split("\\.");
            String[] subFileParts = fileParts[fileParts.length - 2].split("_");
            return Integer.parseInt(subFileParts[subFileParts.length-1]);
        }
        return null;
    }

    @Override
    public ResponseData<ExamMarkingDetailsRes> detailsExamMarking(String examStudentId) {
        ResponseData<ExamMarkingDetailsRes> responseData = new ResponseData<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select\n" +
                   "    es.exam_id as examId,\n" +
                   "    es.exam_test_id as examTestId,\n" +
                   "    es.id as examStudentId,\n" +
                   "    es.code as examStudentCode,\n" +
                   "    es.name as examStudentName,\n" +
                   "    es.email as examStudentEmail,\n" +
                   "    es.phone as examStudentPhone,\n" +
                   "    er.id as examResultId,\n" +
                   "    er.total_score as totalScore,\n" +
                   "    er.total_time as totalTime,\n" +
                   "    er.submited_at as submitedAt,\n" +
                   "    er.file_path as answerFiles,\n" +
                   "    er.comment as comment\n" +
                   "from\n" +
                   "    exam_student es\n" +
                   "left join exam_result er on\n" +
                   "    er.exam_student_id = es.id\n" +
                   "where\n" +
                   "    es.id = :examStudentId \n");

        Query query = entityManager.createNativeQuery(sql.toString());

        query.setParameter("examStudentId", examStudentId);
        List<Object[]> result = query.getResultList();
        List<ExamMarkingDetailsRes> list = new ArrayList<>();
        if (!Objects.isNull(result) && !result.isEmpty()) {
            for (Object[] obj : result) {
                list.add(ExamMarkingDetailsRes.builder()
                                         .examId(DataUtils.safeToString(obj[0]))
                                         .examTestId(DataUtils.safeToString(obj[1]))
                                         .examStudentId(DataUtils.safeToString(obj[2]))
                                         .examStudentCode(DataUtils.safeToString(obj[3]))
                                         .examStudentName(DataUtils.safeToString(obj[4]))
                                         .examStudentEmail(DataUtils.safeToString(obj[5]))
                                         .examStudentPhone(DataUtils.safeToString(obj[6]))
                                         .examResultId(DataUtils.safeToString(obj[7]))
                                         .totalScore(DataUtils.safeToInt(obj[8]))
                                         .totalTime(DataUtils.safeToInt(obj[9]))
                                         .submitedAt(DataUtils.safeToString(obj[10]))
                                         .answerFiles(DataUtils.safeToString(obj[11]))
                                         .comment(DataUtils.safeToString(obj[12]))
                                         .build());
            }
        }

        ExamMarkingDetailsRes examMarkingDetailsRes = list.get(0);

        examMarkingDetailsRes.setQuestionList(examTestRepository.getExamMarkingDetails(examMarkingDetailsRes.getExamTestId(),
                                                                                          examMarkingDetailsRes.getExamStudentId()));

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setData(examMarkingDetailsRes);
        responseData.setResultMsg("Success");
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamResultEntity> markingExam(ExamMarkingSaveReq examMarkingSaveReq) {
        ResponseData<ExamResultEntity> responseData = new ResponseData<>();

        Optional<ExamResultEntity> examResultExist = examResultRepository.findById(examMarkingSaveReq.getExamResultId());
        if(!examResultExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại bài làm của học viên");
            return responseData;
        }
        ExamResultEntity examResult = examResultExist.get();
        examResult.setTotalScore(examMarkingSaveReq.getTotalScore());
        examResult.setComment(examMarkingSaveReq.getComment());
        examResultRepository.save(examResult);

        List<ExamResultDetailsEntity> examResultDetailsEntities = new ArrayList<>();
        for(ExamMarkingDetailsSaveReq detailsSaveReq: examMarkingSaveReq.getExamResultDetails()){
            ExamResultDetailsEntity examResultDetailsEntity = modelMapper.map(detailsSaveReq, ExamResultDetailsEntity.class);
            examResultDetailsEntities.add(examResultDetailsEntity);
        }
        examResultDetailsRepository.saveAll(examResultDetailsEntities);

        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Chấm bài thành công");
        return responseData;
    }
}
