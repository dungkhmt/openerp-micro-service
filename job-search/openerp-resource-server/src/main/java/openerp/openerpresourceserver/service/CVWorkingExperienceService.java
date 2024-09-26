package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.CVWorkingExperience;

import java.util.List;

public interface CVWorkingExperienceService {
    List<CVWorkingExperience> getAll();
    List<CVWorkingExperience> getAllByCVId(Integer CVId);
    CVWorkingExperience getById(Integer id);
    CVWorkingExperience save(CVWorkingExperience cvWorkingExperience);
    void deleteById(Integer id);
}
