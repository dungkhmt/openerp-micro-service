package openerp.openerpresourceserver.service;


import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.employee.EmployeeQueryRequest;
import openerp.openerpresourceserver.dto.request.employee.EmployeeRequest;
import openerp.openerpresourceserver.dto.response.employee.EmployeeResponse;
import openerp.openerpresourceserver.dto.response.employee.SimpleEmployeeResponse;
import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.projection.EmployeeResponseProjection;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {
    Employee getEmployeeInfo();

    Employee updateProfile(EmployeeRequest dto);

    Page<EmployeeResponse> getEmployeesByProperties(EmployeeQueryRequest dto, PagingRequest pagingRequest);

    EmployeeResponseProjection getEmployeeById(Long id) throws NotFoundException;

    Employee addEmployee(EmployeeRequest dto) throws BadRequestException;

    Employee updateEmployee(EmployeeRequest dto) throws BadRequestException, NotFoundException;

    List<Employee> deleteEmployee(List<Long> idList) throws NotFoundException;

    List<SimpleEmployeeResponse> getAllEmployees();
}
