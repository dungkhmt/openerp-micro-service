package openerp.openerpresourceserver.trainingprogcourse.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.service.TrainingProgCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/training_prog_course")
@Configuration
@EnableAsync
public class TrainingProgCourseController {

    private final TrainingProgCourseService trainingProgCourseService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createCourse(@Valid @RequestBody RequestTrainingProgCourse request) {

        try {
            trainingProgCourseService.create(request);
            return ResponseEntity.ok().body("ok");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
