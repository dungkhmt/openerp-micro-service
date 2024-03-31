package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Education;

import java.util.List;

public interface EducationService {

    List<Education> getAllByUserId(String id);
    Education getById(Integer id);
    Education save(Education education);
    void deleteById(Integer id);

}
