package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Company;
import openerp.openerpresourceserver.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepo extends JpaRepository<Company, Integer> {
    public Company findByUserId(String UserId);
}
