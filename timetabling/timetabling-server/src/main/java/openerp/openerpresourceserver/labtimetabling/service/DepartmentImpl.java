package openerp.openerpresourceserver.labtimetabling.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Department;
import openerp.openerpresourceserver.labtimetabling.repo.DepartmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DepartmentImpl implements DepartmentService {
    private DepartmentRepo departmentRepo;
    @Override
    public List<Department> getAllDepartments() {
        return departmentRepo.findAll();
    }
}
