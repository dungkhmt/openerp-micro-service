package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, String> {
}
