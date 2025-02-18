package openerp.openerpresourceserver.fb.repo;

import openerp.openerpresourceserver.fb.entity.FbUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FbUserRepo extends JpaRepository<FbUser, String> {

}
