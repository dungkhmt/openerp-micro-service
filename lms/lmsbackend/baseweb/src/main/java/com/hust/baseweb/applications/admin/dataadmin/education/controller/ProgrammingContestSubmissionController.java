package com.hust.baseweb.applications.admin.dataadmin.education.controller;

import com.hust.baseweb.applications.admin.dataadmin.education.service.ProgrammingContestSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/admin/data/programming-contests/submissions")
public class ProgrammingContestSubmissionController {

    private final ProgrammingContestSubmissionService contestSubmissionService;

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getContestSubmissionsOfStudent(
        @PathVariable("studentId") String studentLoginId,
        @RequestParam String search,
        @RequestParam int page,
        @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
            contestSubmissionService.findContestSubmissionsOfStudent(studentLoginId, search, pageable)
        );
    }
}
