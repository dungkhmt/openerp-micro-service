package openerp.openerpresourceserver.labtimetabling.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.labtimetabling.entity.Assign;
import openerp.openerpresourceserver.labtimetabling.entity.ScheduleConflict;
import openerp.openerpresourceserver.labtimetabling.entity.constant.ConflictType;
import openerp.openerpresourceserver.labtimetabling.repo.AssignRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class AssignServiceImpl implements AssignService {
    private AssignRepo assignRepo;
    @Override
    public List<ScheduleConflict> findConflict() {
        List<Assign> assignList = assignRepo.findAll();

        List<Assign> cap_constraint = assignList.stream().map(assign -> {
            if(assign.getLesson().getQuantity()>assign.getRoom().getCapacity()) return assign;
            return null;
        }).toList();

        List<ScheduleConflict> cap_conflicts = new ArrayList<>();
        assignList.forEach(assign -> {
            if(assign.getLesson().getQuantity()>assign.getRoom().getCapacity()){
                cap_conflicts.add(new ScheduleConflict(assign, ConflictType.CAP_EXCEPT_CONFLICT));
            }
        });

        List<Assign> time_constraint = new ArrayList<>();
        for(int i=1;i<assignList.size();i++){
            for(int j=0;j<i;j++){
                if(Objects.equals(assignList.get(i).getRoom().getId(), assignList.get(j).getRoom().getId())
                && Objects.equals(assignList.get(i).getWeek(), assignList.get(j).getWeek())){
                    Assign assign1 = assignList.get(i);
                    Assign assign2 = assignList.get(j);
                    long first_class_start_slot = (assign1.getDay_of_week()-2)*12+(assign1.getPeriod()-1)*6+assign1.getStart_slot();
                    long second_class_start_slot = (assign2.getDay_of_week()-2)*12 +( assign2.getPeriod()-1)*6+assign2.getStart_slot();
                    if(first_class_start_slot <= second_class_start_slot
                    && second_class_start_slot <= first_class_start_slot+assign1.getLesson().getPeriod()-1
                    || second_class_start_slot <= first_class_start_slot
                    && first_class_start_slot <= second_class_start_slot+assign2.getLesson().getPeriod()-1){
                        time_constraint.add(assign1);
                        time_constraint.add(assign2);
                    }
                }
            }
        }
        List<ScheduleConflict> time_conflicts = new ArrayList<>(time_constraint.stream().map(assign -> new ScheduleConflict(assign, ConflictType.TIME_ASSIGN_CONFLICT)).toList());
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        cap_conflicts.addAll(time_conflicts);
        return cap_conflicts;
    }

    @Override
    public List<Assign> getAssignsBySemester(Long semId) {

        return assignRepo.findAssignsBySemester_id(semId);
    }

    @Override
    public List<String> getAllSemesters() {
        return assignRepo.findDistinctSemesters();
    }
}
