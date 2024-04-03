package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Role;
import openerp.openerpresourceserver.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo  extends JpaRepository<Role, Integer> {
}
