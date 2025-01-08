package openerp.openerpresourceserver.tarecruitment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.tarecruitment.entity.dto.ChartDTO;
import openerp.openerpresourceserver.tarecruitment.entity.dto.PaginationDTO;
import openerp.openerpresourceserver.tarecruitment.entity.ClassCall;
import openerp.openerpresourceserver.tarecruitment.service.ClassCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/class-call")
public class ClassCallController {
    private ClassCallService classCallService;

    @PostMapping("/create-class")
    public ResponseEntity<?> createClass(@RequestBody ClassCall classCall) {
        try {
            ClassCall newClassCall = classCallService.createNewClass(classCall);
            return ResponseEntity.ok().body(newClassCall);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get-all-class")
    public ResponseEntity<?> getAllClass(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int limit) {
        PaginationDTO<ClassCall> paginationDTO = classCallService.getAllClass(page, limit);
        return ResponseEntity.ok().body(paginationDTO);
    }

    @GetMapping("/get-class/{id}")
    public ResponseEntity<?> getClassById(@PathVariable int id) {
        try {
            Optional<ClassCall> classCall = classCallService.getClassById(id);
            return ResponseEntity.ok().body(classCall);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update-class/{id}")
    public ResponseEntity<?> updateClass(@PathVariable int id, @RequestBody ClassCall classCall) {
        try {
            ClassCall updatedClassCall = classCallService.updateClass(id, classCall);
            return ResponseEntity.ok().body(updatedClassCall);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-class/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable int id) {
        try {
            classCallService.deleteClass(id);
            return ResponseEntity.ok().body("Delete successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-multiple-class")
    public ResponseEntity<?> deleteMultipleClass(@RequestBody List<Integer> idList) {
        try {
            classCallService.deleteMultipleClass(idList);
            return ResponseEntity.ok().body("Delete successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get-class-by-semester/{semester}")
    public ResponseEntity<?> getClassBySemester(
            @PathVariable String semester,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "") String search) {
        try {
            PaginationDTO<ClassCall> paginationDTO = classCallService.getClassBySemester(semester, search, page, limit);
            return ResponseEntity.ok().body(paginationDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get-my-registered-class/{semester}")
    public ResponseEntity<?> getMyRegisteredClass(Principal principal, @PathVariable String semester) {
        String userId = principal.getName();
        List<ClassCall> myClassCall = classCallService.getAllMyRegisteredClass(userId, semester);
        return ResponseEntity.ok().body(myClassCall);
    }

    @PostMapping("/import-class/{semester}")
    public ResponseEntity<?> importClassCall(@RequestParam("excelFile") MultipartFile file, @PathVariable String semester) {
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body("Need to upload a file");
        }
        try {
            int numberOfData = classCallService.importClass(file, semester);
            return ResponseEntity.ok().body("Nhập thành công " + numberOfData + " lớp học");


        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("get-class-numb-data")
    public ResponseEntity<?> getClassNumbData() {
        List<ChartDTO> chart = classCallService.getNumberOfClassEachSemester();
        return ResponseEntity.ok().body(chart);
    }
}
