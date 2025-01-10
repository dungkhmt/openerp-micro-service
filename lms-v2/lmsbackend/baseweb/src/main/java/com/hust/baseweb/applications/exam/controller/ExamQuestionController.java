package com.hust.baseweb.applications.exam.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.exam.entity.ExamQuestionEntity;
import com.hust.baseweb.applications.exam.model.ResponseData;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionDeleteReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionDetailsReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionFilterReq;
import com.hust.baseweb.applications.exam.model.request.ExamQuestionSaveReq;
import com.hust.baseweb.applications.exam.service.ExamQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/exam-question")
@RequiredArgsConstructor
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;

    @Secured("ROLE_TEACHER")
    @PostMapping("/filter")
    public ResponseEntity<Page<ExamQuestionEntity>> filter(Pageable pageable, @RequestBody ExamQuestionFilterReq examQuestionFilterReq) {
        return ResponseEntity.ok(examQuestionService.filter(pageable, examQuestionFilterReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/details")
    public ResponseEntity<ResponseData<ExamQuestionEntity>> details(@RequestBody ExamQuestionDetailsReq examQuestionDetailsReq) {
        return ResponseEntity.ok(examQuestionService.details(examQuestionDetailsReq));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/create")
    public ResponseEntity<ResponseData<ExamQuestionEntity>> create(@RequestParam("body") String questions,
                                                                   @RequestParam("files") MultipartFile[] files) {
        Gson gson = new Gson();
        return ResponseEntity.ok(examQuestionService.create(gson.fromJson(questions, ExamQuestionSaveReq.class), files));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/update")
    public ResponseEntity<ResponseData<ExamQuestionEntity>> update(@RequestParam("body") String questions,
                                                                   @RequestParam("files") MultipartFile[] files) {
        Gson gson = new Gson();
        return ResponseEntity.ok(examQuestionService.update(gson.fromJson(questions, ExamQuestionSaveReq.class), files));
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/delete")
    public ResponseEntity<ResponseData<ExamQuestionEntity>> delete(@RequestBody @Valid ExamQuestionDeleteReq examQuestionDeleteReq) {
        return ResponseEntity.ok(examQuestionService.delete(examQuestionDeleteReq));
    }
}
