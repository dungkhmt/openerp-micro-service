package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.CVEducation;

import java.util.List;

public interface CVEducationService {

    CVEducation getById(Integer id);
    List<CVEducation> getAll();
    List<CVEducation> getAllByCVId(Integer id);

    CVEducation save(CVEducation cvEducation);

    void deleteById(Integer id);

}
