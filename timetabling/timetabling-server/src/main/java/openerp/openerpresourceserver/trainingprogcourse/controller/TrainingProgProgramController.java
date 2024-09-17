package openerp.openerpresourceserver.trainingprogcourse.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.trainingprogcourse.config.CustomException;
import openerp.openerpresourceserver.trainingprogcourse.dto.PaginationDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgProgramDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.TrainingProgProgramInfo;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgProgramDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.TrainingProgScheduleUpdateRequest;
import openerp.openerpresourceserver.trainingprogcourse.service.TrainingProgProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/training_prog_program")
@Configuration
@EnableAsync
public class TrainingProgProgramController {

    @Autowired
    private TrainingProgProgramService trainingProgProgramService;

    @PostMapping("/create")
    public ResponseEntity<String> createProgram(@RequestBody RequestTrainingProgProgramDTO requestDTO) {
        trainingProgProgramService.create(requestDTO);
        return ResponseEntity.ok("Program created successfully!");
    }

    @GetMapping("/getall")
    public ResponseEntity<PaginationDTO<TrainingProgProgramInfo>> getAllPrograms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String keyword) {

        try {
            PaginationDTO<TrainingProgProgramInfo> paginationDTO = trainingProgProgramService.getAllTrainingProgPrograms(page, limit, keyword);
            return ResponseEntity.ok(paginationDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ResponseTrainingProgProgramDTO> getProgramById(@PathVariable String id) {
        ResponseTrainingProgProgramDTO programDTO = trainingProgProgramService.getTrainingProgProgramById(id);
        return ResponseEntity.ok(programDTO);
    }

    @GetMapping("/get-available-course/{id}")
    public ResponseEntity<List<ResponseTrainingProgCourse>> getAvailableCourse(@PathVariable String id) {
        List<ResponseTrainingProgCourse> courses = trainingProgProgramService.getAvailableCourse(id);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @PostMapping("/add-courses/{programId}")
    public ResponseEntity<String> addCoursesToProgram(
            @PathVariable String programId,
            @RequestBody List<String> courseIds) {
        trainingProgProgramService.addCoursesToProgram(programId, courseIds);
        return ResponseEntity.ok("Courses added to program successfully!");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateSemesters(
            @RequestBody @Valid List<TrainingProgScheduleUpdateRequest> scheduleUpdates) {

        try {
            trainingProgProgramService.update(scheduleUpdates);
            return ResponseEntity.ok("Cập nhật học kỳ thành công");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi");
        }
    }

}