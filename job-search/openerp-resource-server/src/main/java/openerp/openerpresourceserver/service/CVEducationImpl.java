package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.CVEducation;
import openerp.openerpresourceserver.repo.CVEducationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CVEducationImpl implements CVEducationService{
    CVEducationRepo cvEducationRepo;

    @Override
    public CVEducation getById(Integer id) {
        Optional<CVEducation> result = cvEducationRepo.findById(id);
        CVEducation cvEducation = null;
        if(result.isPresent()) {
            cvEducation = result.get();
        }
        else {
            throw new RuntimeException("cvEducationRepo id not found: " +  id);
        }
        return cvEducation;
    }

    @Override
    public List<CVEducation> getAll() {
        return cvEducationRepo.findAll();
    }

    @Override
    public List<CVEducation> getAllByCVId(Integer id) {
        return cvEducationRepo.findBycvId(id);
    }

    @Override
    public CVEducation save(CVEducation cvEducation) {
        return cvEducationRepo.save(cvEducation);
    }

    @Override
    public void deleteById(Integer id) {
        cvEducationRepo.deleteById(id);
    }
}
