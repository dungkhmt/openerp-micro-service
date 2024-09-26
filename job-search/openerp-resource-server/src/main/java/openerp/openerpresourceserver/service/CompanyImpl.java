package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.CVEducation;
import openerp.openerpresourceserver.entity.Company;
import openerp.openerpresourceserver.repo.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CompanyImpl implements CompanyService {
    CompanyRepo companyRepo;

    @Override
    public Company getByUserId(String id) {
        return companyRepo.findByUserId(id);
    }

    @Override
    public Company getById(Integer id) {
        Optional<Company> result = companyRepo.findById(id);
        Company company = null;
        if(result.isPresent()) {
            company = result.get();
        }
        else {
            throw new RuntimeException("company with id not found: " +  id);
        }
        return company;
    }

    @Override
    public List<Company> getAll() {
        return companyRepo.findAll();
    }

    @Override
    public Company save(Company company) {
        return companyRepo.save(company);
    }

    @Override
    public void deleteById(Integer id) {
        companyRepo.deleteById(id);
    }


}
