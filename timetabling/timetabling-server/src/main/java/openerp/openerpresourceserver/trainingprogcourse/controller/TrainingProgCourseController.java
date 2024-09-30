package openerp.openerpresourceserver.trainingprogcourse.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.service.TrainingProgCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable String id, @RequestBody RequestTrainingProgCourse request) {
        try {
            trainingProgCourseService.update(id, request);
            return new ResponseEntity<>("Course updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update course: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<List<ResponseTrainingProgCourse>> getAllCourses() {
        List<ResponseTrainingProgCourse> courses = trainingProgCourseService.getAll();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ResponseTrainingProgCourse> getCourseDetail(@PathVariable String id) {
        try {
            ResponseTrainingProgCourse courseDetail = trainingProgCourseService.getDetail(id);
            return new ResponseEntity<>(courseDetail, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
//        try {
//            trainingProgCourseService.delete(id);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

}
