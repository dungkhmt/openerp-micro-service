package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Company;

import java.util.List;

public interface CompanyService {
    Company getByUserId(String id);
    Company getById(Integer id);
    List<Company> getAll();
    Company save(Company company);

    void deleteById(Integer id);
}
