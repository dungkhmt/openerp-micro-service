package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Skill;
import openerp.openerpresourceserver.entity.User;

import java.util.List;

public interface SkillService {
    List<Skill> getAllByUserId(String userId);
    Skill getById(Integer id);
    Skill save(Skill skill);
    void deleteById(Integer id);
}
