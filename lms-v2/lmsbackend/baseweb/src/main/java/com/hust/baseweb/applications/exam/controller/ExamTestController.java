package com.hust.baseweb.applications.exam.controller;

import com.hust.baseweb.applications.exam.entity.ExamQuestionEntity;
import com.hust.baseweb.applications.exam.entity.ExamTestEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.*;
import com.hust.baseweb.applications.exam.model.response.ExamTestDetailsRes;
import com.hust.baseweb.applications.exam.service.ExamTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/exam-test")
@RequiredArgsConstructor
public class ExamTestController {

    private final ExamTestService examTestService;

    @Secured("ROLE_TEACHER")
    @PostMapping("/filter")
    public ResponseEntity<Page<ExamTestEntity>> filter(Pageable pageable, @RequestBody ExamTestFilterReq examTestFilterReq) {
        return ResponseEntity.ok(examTestService.filter(pageable, examTestFilterReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/details")
    public ResponseEntity<ResponseData<ExamTestDetailsRes>> details(@RequestBody ExamTestDetailsReq examTestDetailsReq) {
        return ResponseEntity.ok(examTestService.details(examTestDetailsReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/create")
    public ResponseEntity<ResponseData<ExamTestEntity>> create(@RequestBody @Valid ExamTestSaveReq examTestSaveReq) {
        return ResponseEntity.ok(examTestService.create(examTestSaveReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/update")
    public ResponseEntity<ResponseData<ExamTestEntity>> update(@RequestBody @Valid ExamTestSaveReq examTestSaveReq) {
        return ResponseEntity.ok(examTestService.update(examTestSaveReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/delete")
    public ResponseEntity<ResponseData<ExamTestEntity>> delete(@RequestBody @Valid ExamTestDeleteReq examTestDeleteReq) {
        return ResponseEntity.ok(examTestService.delete(examTestDeleteReq));
    }
}
