package openerp.openerpresourceserver.labtimetabling.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.algorithm.LabTimetablingSolver;
import openerp.openerpresourceserver.labtimetabling.entity.AutoSchedulingResult;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.entity.Room;
import openerp.openerpresourceserver.labtimetabling.repo.AutoSchedulingResultRepo;
import openerp.openerpresourceserver.labtimetabling.service.ClassService;
import openerp.openerpresourceserver.labtimetabling.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/lab-timetabling/auto-assign")
public class SolvingController {
    private ClassService classService;
    private RoomService roomService;

    private AutoSchedulingResultRepo autoSchedulingResultRepo;
    @PostMapping("/{semester_id}")
    public ResponseEntity<?> autoAssign(@PathVariable Long semester_id){
        UUID reqId = UUID.randomUUID();
        AutoSchedulingResult result = new AutoSchedulingResult();
        result.setSemester_id(semester_id);
        result.setId(reqId);
        result.setCreated_time(new Date());

        autoSchedulingResultRepo.save(result);

        List<Class> classList = classService.getClassesBySemester(semester_id);
        List<Room> roomList = roomService.getAllRooms();

        Thread solver = new Thread(new Runnable() {
            @Override
            public void run() {
                LabTimetablingSolver problem = new LabTimetablingSolver(classList, roomList);
                String res = problem.solve();
                result.setResult(res);
                autoSchedulingResultRepo.save(result);
            }
        });
        solver.start();
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }
    @GetMapping("/{semester_id}")
    public ResponseEntity<?> getResultsBySemester(@PathVariable Long semester_id){
        List<AutoSchedulingResult> results = autoSchedulingResultRepo.findAllBySemester_id(semester_id);
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        List<AutoSchedulingResult> results = autoSchedulingResultRepo.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }
}
