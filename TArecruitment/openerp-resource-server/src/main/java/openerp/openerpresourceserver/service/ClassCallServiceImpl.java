package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.ClassCall;
import openerp.openerpresourceserver.repo.ClassCallRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ClassCallServiceImpl implements ClassCallService {

    private ClassCallRepo classCallRepo;
    @Override
    public ClassCall createNewClass(ClassCall classCall) {
        classCallRepo.save(classCall);
        return classCall;
    }

    @Override
    public List<ClassCall> getAllClass() {
        List<ClassCall> classCalls = classCallRepo.findAll();
        return classCalls;
    }

    @Override
    public Optional<ClassCall> getClassById(int id) {
        Optional<ClassCall> classCall = classCallRepo.findById(id);
        if(classCall.isEmpty()) throw new IllegalArgumentException("Class with ID " + id + " not found");
        else return classCall;
    }

    @Override
    public List<ClassCall> getClassBySemester(String semester) {
        List<ClassCall> classCall = classCallRepo.findBySemester(semester, Sort.by(Sort.Direction.ASC, "id"));
        if(classCall.isEmpty()) throw new IllegalArgumentException("No class in semester " + semester);
        return classCall;
    }

    @Override
    public ClassCall updateClass(int id, ClassCall classCall) {
        Optional<ClassCall> existingClassCallOptional = classCallRepo.findById(id);
        if (existingClassCallOptional.isEmpty()) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        ClassCall existingClassCall = existingClassCallOptional.get();
        existingClassCall.setDay(classCall.getDay());
        existingClassCall.setStartPeriod(classCall.getStartPeriod());
        existingClassCall.setEndPeriod(classCall.getEndPeriod());
        existingClassCall.setSubjectId(classCall.getSubjectId());
        existingClassCall.setSubjectName(classCall.getSubjectName());
        existingClassCall.setClassRoom(classCall.getClassRoom());
        existingClassCall.setNote(classCall.getNote());
        existingClassCall.setSemester(classCall.getSemester());

        // Save the updated class call to the database
        ClassCall updatedClassCall = classCallRepo.save(existingClassCall);

        return updatedClassCall;
    }

    @Override
    public boolean deleteClass(int id) {
        Optional<ClassCall> existingClassCallOptional = classCallRepo.findById(id);
        if (existingClassCallOptional.isEmpty()) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        classCallRepo.deleteById(id);
        return true;
    }
}
