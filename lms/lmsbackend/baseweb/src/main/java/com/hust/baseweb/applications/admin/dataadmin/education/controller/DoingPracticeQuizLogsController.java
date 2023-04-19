package com.hust.baseweb.applications.admin.dataadmin.education.controller;

import com.hust.baseweb.applications.admin.dataadmin.education.service.DoingPracticeQuizLogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/admin/data/education/doing-practice-quiz-logs")
public class DoingPracticeQuizLogsController {

    private final DoingPracticeQuizLogsService doingPracticeQuizLogsService;

    @GetMapping("/{studentLoginId}")
    public ResponseEntity<?> getDoingPracticeQuizLogsOfStudent(@PathVariable String studentLoginId,
                                                               @RequestParam String search,
                                                               @RequestParam int page,
                                                               @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
            doingPracticeQuizLogsService.findDoingPracticeQuizLogsOfStudent(
                studentLoginId, search, pageable
            )
        );
    }
}
