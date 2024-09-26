package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.EmployeeCV;

import java.util.List;

public interface EmployeeCVService {
    EmployeeCV getById(Integer id);
    List<EmployeeCV> getAllByUserId(String id);
    EmployeeCV save(EmployeeCV employeeCV);
    void deleteById(Integer id);
}
