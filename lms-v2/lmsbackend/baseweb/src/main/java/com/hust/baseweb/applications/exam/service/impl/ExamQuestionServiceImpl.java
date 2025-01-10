package com.hust.baseweb.applications.exam.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import com.hust.baseweb.applications.exam.entity.ExamEntity;
import com.hust.baseweb.applications.exam.entity.ExamQuestionEntity;
import com.hust.baseweb.applications.exam.entity.ExamTestQuestionEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionDeleteReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionDetailsReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionFilterReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionSaveReq;
import com.hust.baseweb.applications.exam.repository.ExamQuestionRepository;
import com.hust.baseweb.applications.exam.repository.ExamTestQuestionRepository;
import com.hust.baseweb.applications.exam.service.ExamQuestionService;
import com.hust.baseweb.applications.exam.service.MongoFileService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final ExamTestQuestionRepository examTestQuestionRepository;
    private final MongoFileService mongoFileService;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    @Override
    public Page<ExamQuestionEntity> filter(Pageable pageable, ExamQuestionFilterReq examQuestionFilterReq) {
        StringBuilder sql = new StringBuilder();
        sql.append("select\n" +
                   "    *\n" +
                   "from\n" +
                   "    exam_question eq\n" +
                   "where\n" +
                   "    eq.created_by = :userLogin \n");
        if(examQuestionFilterReq.getType() != null){
            sql.append("and\n" +
                       "    eq.type = :type \n");
        }
        if(DataUtils.stringIsNotNullOrEmpty(examQuestionFilterReq.getKeyword())){
            sql.append("and\n" +
                       "    ((lower(eq.code) like CONCAT('%', LOWER(:keyword),'%')) or \n" +
                       "    (lower(eq.content) like CONCAT('%', LOWER(:keyword),'%'))) \n");
        }
        sql.append("order by created_at desc\n");

        Query query = entityManager.createNativeQuery(sql.toString(), ExamQuestionEntity.class);
        Query count = entityManager.createNativeQuery("select count(1) FROM (" + sql + ") as count");
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        query.setParameter("userLogin", SecurityUtils.getUserLogin());
        count.setParameter("userLogin", SecurityUtils.getUserLogin());
        if(examQuestionFilterReq.getType() != null){
            query.setParameter("type", examQuestionFilterReq.getType());
            count.setParameter("type", examQuestionFilterReq.getType());
        }
        if(DataUtils.stringIsNotNullOrEmpty(examQuestionFilterReq.getKeyword())){
            query.setParameter("keyword", DataUtils.escapeSpecialCharacters(examQuestionFilterReq.getKeyword()));
            count.setParameter("keyword", DataUtils.escapeSpecialCharacters(examQuestionFilterReq.getKeyword()));
        }

        long totalRecord = ((BigInteger) count.getSingleResult()).longValue();
        List<ExamQuestionEntity> list = query.getResultList();
        return new PageImpl<>(list, pageable, totalRecord);
    }

    @Override
    public ResponseData<ExamQuestionEntity> details(ExamQuestionDetailsReq examQuestionDetailsReq) {
        ResponseData<ExamQuestionEntity> responseData = new ResponseData<>();
        if(DataUtils.stringIsNotNullOrEmpty(examQuestionDetailsReq.getId())){
            Optional<ExamQuestionEntity> examQuestionEntity = examQuestionRepository.findById(examQuestionDetailsReq.getId());
            if(examQuestionEntity.isPresent()){
                responseData.setHttpStatus(HttpStatus.OK);
                responseData.setResultCode(HttpStatus.OK.value());
                responseData.setResultMsg("Success");
                responseData.setData(examQuestionEntity.get());
                return responseData;
            }
        }

        if(DataUtils.stringIsNotNullOrEmpty(examQuestionDetailsReq.getCode())){
            Optional<ExamQuestionEntity> examQuestionEntity = examQuestionRepository.findByCode(examQuestionDetailsReq.getCode());
            if(examQuestionEntity.isPresent()){
                responseData.setHttpStatus(HttpStatus.OK);
                responseData.setResultCode(HttpStatus.OK.value());
                responseData.setResultMsg("Success");
                responseData.setData(examQuestionEntity.get());
                return responseData;
            }
        }

        responseData.setHttpStatus(HttpStatus.NOT_FOUND);
        responseData.setResultCode(HttpStatus.NOT_FOUND.value());
        responseData.setResultMsg("Chưa tồn tại câu hỏi");
        return responseData;
    }

    @Override
    public ResponseData<ExamQuestionEntity> create(ExamQuestionSaveReq examQuestionSaveReq, MultipartFile[] files) {
        ResponseData<ExamQuestionEntity> responseData = new ResponseData<>();
        Optional<ExamQuestionEntity> examQuestionExist = examQuestionRepository.findByCode(examQuestionSaveReq.getCode());
        if(examQuestionExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.ALREADY_REPORTED);
            responseData.setResultCode(HttpStatus.ALREADY_REPORTED.value());
            responseData.setResultMsg("Đã tồn tại mã câu hỏi");
            return responseData;
        }

        List<String> filePaths = mongoFileService.storeFiles(files);

        ExamQuestionEntity examQuestionEntity = modelMapper.map(examQuestionSaveReq, ExamQuestionEntity.class);
        examQuestionEntity.setFilePath(String.join(";", filePaths));
        examQuestionRepository.save(examQuestionEntity);
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Thêm mới câu hỏi thành công");
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<ExamQuestionEntity> update(ExamQuestionSaveReq examQuestionSaveReq, MultipartFile[] files) {
        ResponseData<ExamQuestionEntity> responseData = new ResponseData<>();
        Optional<ExamQuestionEntity> examQuestionExist = examQuestionRepository.findByCode(examQuestionSaveReq.getCode());
        if(!examQuestionExist.isPresent()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Chưa tồn tại câu hỏi");
            return responseData;
        }

        List<String> filePaths = new ArrayList<>();
        if(files.length > 0){
            filePaths = mongoFileService.storeFiles(files);
        }

        ExamQuestionEntity examQuestionEntity = modelMapper.map(examQuestionSaveReq, ExamQuestionEntity.class);
        examQuestionEntity.setId(examQuestionExist.get().getId());
        examQuestionEntity.setCreatedBy(examQuestionExist.get().getCreatedBy());
        examQuestionEntity.setCreatedAt(examQuestionExist.get().getCreatedAt());
        examQuestionEntity.setFilePath(
            DataUtils.stringIsNotNullOrEmpty(examQuestionEntity.getFilePath()) ?
                (!filePaths.isEmpty() ?
                    examQuestionEntity.getFilePath() +";"+ String.join(";", filePaths) :
                    examQuestionEntity.getFilePath()) :
                String.join(";", filePaths));
        examQuestionRepository.save(examQuestionEntity);

        for(String filePath: examQuestionSaveReq.getDeletePaths()){
            mongoFileService.deleteByPath(filePath);
        }

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

        List<ExamTestQuestionEntity> examTestQuestionEntityList = examTestQuestionRepository.findAllByExamQuestionId(examQuestionDeleteReq.getId());
        if(!examTestQuestionEntityList.isEmpty()){
            responseData.setHttpStatus(HttpStatus.NOT_FOUND);
            responseData.setResultCode(HttpStatus.NOT_FOUND.value());
            responseData.setResultMsg("Câu hỏi đã được gán cho đề thi, không được xoá");
            return responseData;
        }

        String[] filePaths = examQuestionExist.get().getFilePath().split(";");
        for(String filePath: filePaths){
            if(DataUtils.stringIsNotNullOrEmpty(filePath)){
                mongoFileService.deleteByPath(filePath);
            }
        }

        examQuestionRepository.delete(examQuestionExist.get());
        responseData.setHttpStatus(HttpStatus.OK);
        responseData.setResultCode(HttpStatus.OK.value());
        responseData.setResultMsg("Xoá câu hỏi thành công");
        return responseData;
    }
}
