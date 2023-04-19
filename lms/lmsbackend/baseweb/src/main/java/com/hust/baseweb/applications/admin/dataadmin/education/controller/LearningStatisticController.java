package com.hust.baseweb.applications.admin.dataadmin.education.controller;

import com.hust.baseweb.applications.admin.dataadmin.education.service.LearningStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/statistic/learning")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LearningStatisticController {

    private final LearningStatisticService learningStatisticService;

    @PostMapping("/basic")
    public ResponseEntity<?> executeLearningStatistic() throws ParseException {
        learningStatisticService.statisticLearningGeneral();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/basic")
    public ResponseEntity<?> getLearningStatisticResults(
        @RequestParam(value = "loginId", defaultValue = "") String partOfLoginId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
            learningStatisticService.findLearningStatisticResults(partOfLoginId, pageable)
        );
    }

}
