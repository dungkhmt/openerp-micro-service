package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.Company;
import openerp.openerpresourceserver.repo.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CompanyImpl implements CompanyService {
    CompanyRepo companyRepo;

    @Override
    public Company getByUserId(String id) {
        return companyRepo.findByUserId(id);
    }
}
