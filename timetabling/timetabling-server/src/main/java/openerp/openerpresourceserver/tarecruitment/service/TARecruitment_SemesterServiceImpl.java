package openerp.openerpresourceserver.tarecruitment.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.tarecruitment.repo.TARecruitmentSemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class TARecruitment_SemesterServiceImpl implements TARecruitment_SemesterService{

    private TARecruitmentSemesterRepo semesterRepo;
    @Override
    public List<String> getAllSemester() {
        return semesterRepo.getAllSemester();
    }

    @Override
    public String getCurrentSemester() {
        Date currentDate = new Date();
        return semesterRepo.getCurrentSemester(currentDate);
    }
}
