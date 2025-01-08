package openerp.openerpresourceserver.labtimetabling.controller;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.algorithm.LabTimetablingSolver;
import openerp.openerpresourceserver.labtimetabling.entity.autoscheduling.*;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.entity.Room;
import openerp.openerpresourceserver.labtimetabling.repo.AutoSchedulingResultRepo;
import openerp.openerpresourceserver.labtimetabling.repo.AutoSchedulingSubmissionRepo;
import openerp.openerpresourceserver.labtimetabling.service.AutoSchedulingSubmissionService;
import openerp.openerpresourceserver.labtimetabling.service.ClassService;
import openerp.openerpresourceserver.labtimetabling.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/lab-timetabling/auto-assign")
public class SolvingController {
    private ClassService classService;
    private RoomService roomService;
    private AutoSchedulingSubmissionService autoSchedulingSubmissionService;
    private AutoSchedulingResultRepo autoSchedulingResultRepo;

    private AutoSchedulingSubmissionRepo autoSchedulingSubmissionRepo;
    @PostMapping("/{semester_id}")
    public ResponseEntity<?> solve(@PathVariable Long semester_id, @RequestBody AutoSchedulingRequest request){
        List<Class> classList = classService.getClassesBySemester(semester_id);
        List<Room> roomList = roomService.getAllRooms();

        List<Long> oddWeekClassesId = new ArrayList<>();
        List<Long> evenWeekClassesId = new ArrayList<>();
        List<Long> allClassesId = classList.stream().map(Class::getId).toList();

        for(Class c: classList){
            if (c.getWeek_schedule_constraint() == 1) oddWeekClassesId.add(c.getId());
            else if (c.getWeek_schedule_constraint() == 2) evenWeekClassesId.add(c.getId());
        }

        List<Long> classesId = new ArrayList<>();
        List<List<Integer>> avoidWeeks = new ArrayList<>();
        for(Class c: classList){
            classesId.add(c.getId());
            avoidWeeks.add(new ArrayList<>(Arrays.asList(c.getAvoid_week_schedule_constraint())).stream().map(x->x-1).toList());
        }


        UUID id = UUID.randomUUID();
        AutoSchedulingSubmission submission = new AutoSchedulingSubmission.Builder().setId(id).setStatus(SubmissionStatus.WAITING).setSemesterId(semester_id).build();
        AutoSchedulingSubmission newSubmission = autoSchedulingSubmissionService.create(submission);
        Thread solver = new Thread(new Runnable() {
            @Override
            public void run() {
                LabTimetablingSolver problem = new LabTimetablingSolver(classList, roomList, request.getSolvingTimeLimit());
                List<LabTimetablingSolver.OptionalConstraint> optionalConstraints = new ArrayList<>();
                optionalConstraints.add(new LabTimetablingSolver.ConsistentWeeklyScheduleConstraint(allClassesId));
                optionalConstraints.add(new LabTimetablingSolver.OddWeekScheduleConstraint(oddWeekClassesId));
                optionalConstraints.add(new LabTimetablingSolver.EvenWeekScheduleConstraint(evenWeekClassesId));
                optionalConstraints.add(new LabTimetablingSolver.AvoidWeekScheduleConstraint(classesId, avoidWeeks));

                List<List<AutoSchedulingVar>> vars = problem.solve(optionalConstraints);
                if (vars == null) {
                    return ;
                }
                newSubmission.setStatus(SubmissionStatus.FINISHED);
                autoSchedulingSubmissionRepo.save(newSubmission);
                for(List<AutoSchedulingVar> autoSchedulingVarList: vars){
                    for(AutoSchedulingVar var: autoSchedulingVarList){
                        AutoSchedulingResult result = new AutoSchedulingResult(var);
                        result.setSubmission_id(id);
                        result.setId(UUID.randomUUID());
                        autoSchedulingResultRepo.save(result);
                    }
                }
            }
        });
        solver.start();
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }
    @GetMapping("/{semester_id}")
    public ResponseEntity<?> getResultsBySemester(@PathVariable Long semester_id){
        List<AutoSchedulingSubmission> results = autoSchedulingSubmissionService.getAllBySemesterId(semester_id);
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        List<AutoSchedulingSubmission> results = autoSchedulingSubmissionService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }
    @GetMapping("/result/{submission_id}")
    public ResponseEntity<?> getResultsBySubmissionId(@PathVariable UUID submission_id) {
        List<AutoSchedulingResult> resultList = autoSchedulingResultRepo.findAllBySubmission_id(submission_id);
        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }
}
