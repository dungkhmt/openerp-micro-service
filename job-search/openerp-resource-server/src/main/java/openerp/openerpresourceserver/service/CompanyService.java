package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Company;

public interface CompanyService {
    Company getByUserId(String id);
}
