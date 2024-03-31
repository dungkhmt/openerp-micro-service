package openerp.openerpresourceserver.service;
import openerp.openerpresourceserver.entity.Experience;

import java.util.List;
public interface ExperienceService {
    List<Experience> getAllByUserId(String id);
    Experience getById(Integer id);
    Experience save(Experience experience);
    void deleteById(Integer id);
}
