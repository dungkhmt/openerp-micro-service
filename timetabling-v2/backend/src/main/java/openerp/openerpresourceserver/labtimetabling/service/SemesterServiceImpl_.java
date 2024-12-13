package openerp.openerpresourceserver.labtimetabling.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.labtimetabling.entity.Semester_;
import openerp.openerpresourceserver.labtimetabling.repo.SemesterRepo_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class SemesterServiceImpl_ implements SemesterService {
    private SemesterRepo_ semesterRepo;
    @Override
    public List<Semester_> getAll() {
        return semesterRepo.findAll();
    }
}
