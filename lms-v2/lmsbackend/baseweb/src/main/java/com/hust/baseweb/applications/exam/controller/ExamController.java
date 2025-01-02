package com.hust.baseweb.applications.exam.controller;

import com.hust.baseweb.applications.exam.entity.ExamEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.*;
import com.hust.baseweb.applications.exam.model.response.ExamDetailsRes;
import com.hust.baseweb.applications.exam.service.ExamService;
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
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @Secured("ROLE_TEACHER")
    @PostMapping("/filter")
    public ResponseEntity<Page<ExamEntity>> filter(
        Pageable pageable, @RequestBody ExamFilterReq examFilterReq) {
        return ResponseEntity.ok(examService.filter(pageable, examFilterReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/details")
    public ResponseEntity<ResponseData<ExamDetailsRes>> details(@RequestBody ExamDetailsReq examDetailsReq) {
        return ResponseEntity.ok(examService.details(examDetailsReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/create")
    public ResponseEntity<ResponseData<ExamEntity>> create(@RequestBody @Valid ExamSaveReq examSaveReq) {
        return ResponseEntity.ok(examService.create(examSaveReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/update")
    public ResponseEntity<ResponseData<ExamEntity>> update(@RequestBody @Valid ExamSaveReq examSaveReq) {
        return ResponseEntity.ok(examService.update(examSaveReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/delete")
    public ResponseEntity<ResponseData<ExamEntity>> delete(@RequestBody @Valid ExamDeleteReq examDeleteReq) {
        return ResponseEntity.ok(examService.delete(examDeleteReq));
    }
}
