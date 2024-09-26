package openerp.openerpresourceserver.service;
import openerp.openerpresourceserver.entity.CVSkill;

import java.util.List;
public interface CVSkillService {
    List<CVSkill> getAll();
    List<CVSkill> getAllByCVId(Integer cvId);
    CVSkill getById(Integer id);
    CVSkill save(CVSkill cvSkill);
    void deleteById(Integer id);

}
