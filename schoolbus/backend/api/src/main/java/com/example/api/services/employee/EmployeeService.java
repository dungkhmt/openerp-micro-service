package com.example.api.services.employee;

import com.example.api.services.employee.dto.AddEmployeeInput;
import com.example.api.services.employee.dto.GetListEmployeeOutput;
import com.example.api.services.employee.dto.ListEmployeeFilterParam;
import com.example.api.services.employee.dto.UpdateEmployeeInput;
import com.example.shared.db.entities.Employee;
import com.example.shared.enumeration.EmployeeRole;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<GetListEmployeeOutput> getListEmployee(ListEmployeeFilterParam filterParam, Pageable pageable);

    void addEmployee(AddEmployeeInput input);

    void updateEmployee(UpdateEmployeeInput input);

    void deleteEmployee(Long id);

    List<Employee> getAvailableEmployees(EmployeeRole role, String query);
}
