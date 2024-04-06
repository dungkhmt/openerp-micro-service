package openerp.openerpresourceserver.service;


import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.Education;
import openerp.openerpresourceserver.entity.EmployeeCV;
import openerp.openerpresourceserver.repo.EmployeeCVRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class EmployeeCVImpl implements EmployeeCVService {
    EmployeeCVRepo employeeCVRepo;

    @Override
    public EmployeeCV getById(Integer id) {
        Optional<EmployeeCV> result = employeeCVRepo.findById(id);
        EmployeeCV employeeCV = null;
        if(result.isPresent()) {
            employeeCV = result.get();
        }
        else {
            throw new RuntimeException("EmployeeCV id not found: " +  id);
        }
        return employeeCV;
    }

    @Override
    public List<EmployeeCV> getAllByUserId(String id) {
        return employeeCVRepo.findByUserId(id);
    }

    @Override
    public EmployeeCV save(EmployeeCV employeeCV) {
        return employeeCVRepo.save(employeeCV);
    }

    @Override
    public void deleteById(Integer id) {
        employeeCVRepo.deleteById(id);
    }
}
