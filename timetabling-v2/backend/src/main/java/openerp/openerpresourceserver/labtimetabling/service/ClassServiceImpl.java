package openerp.openerpresourceserver.labtimetabling.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.labtimetabling.entity.Class;
import openerp.openerpresourceserver.labtimetabling.repo.ClassRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ClassServiceImpl implements ClassService {
    private ClassRepo classRepo;
    @Override
    public List<Class> getAllClasses() {
        return classRepo.findAll();
    }

    @Override
    public List<Class> getClassesBySemester(Long semId) {
        return classRepo.findAllBySemester_id(semId);
    }


    @Override
    public Class getClassById(Long id) {
        Optional<Class> _class = classRepo.findById(id);
        return _class.orElse(null);
    }

    @Override
    public Class createClass(Class _class) {
        Date currentDate = new Date();
//        _class.setCreatedDate(currentDate);
//        _class.setLastModifiedDate(currentDate);
        return classRepo.save(_class);
    }

    @Override
    public Optional<Class> patchClass(Long id, Class _class) {
        Optional<Class> optionalClass = classRepo.findById(id);
        Date currentDate = new Date();
        optionalClass.ifPresent(c -> {
            if (_class.getPeriod() != null) {
                c.setPeriod(_class.getPeriod());
            }
            if (_class.getQuantity() != null) {
                c.setQuantity(_class.getQuantity());
            }
            if (_class.getLessons_per_semester() != null){
                c.setLessons_per_semester(_class.getLessons_per_semester());
            }
            if (_class.getWeek_schedule_constraint()!=null){
                c.setWeek_schedule_constraint(_class.getWeek_schedule_constraint());
            }
            if(_class.getAvoid_week_schedule_constraint() != null){
                c.setAvoid_week_schedule_constraint(_class.getAvoid_week_schedule_constraint());
            }
            c.setNote(_class.getNote());
            c.setDepartment_id(_class.getDepartment_id());
//            c.setLastModifiedDate(currentDate);
            classRepo.save(c);
        });
        return optionalClass;
    }

    @Override
    public boolean deleteClass(Long id) {
        Optional<Class> optionalClass = classRepo.findById(id);
        if(optionalClass.isPresent()){
            classRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getAllSemesters() {
        return classRepo.findDistinctSemesters();
    }
    @Override
    public int batchInsert(List<Class> classes) {
        classes.forEach(c->{
            Date curr = new Date();
//            c.setCreatedDate(curr);
//            c.setLastModifiedDate(curr);
        });
        long prev = classRepo.count();
        classRepo.saveAll(classes);
        long curr = classRepo.count();
        return (int) (curr-prev);
    }
}
