package com.example.api.services.employee;

import com.example.api.services.employee.dto.AddEmployeeInput;
import com.example.api.services.employee.dto.GetListEmployeeOutput;
import com.example.api.services.employee.dto.ListEmployeeFilterParam;
import com.example.api.services.employee.dto.UpdateEmployeeInput;
import com.example.shared.db.dto.GetListEmployeeDTO;
import com.example.shared.db.entities.Account;
import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;
import com.example.shared.db.repo.AccountRepository;
import com.example.shared.db.repo.BusRepository;
import com.example.shared.db.repo.EmployeeRepository;
import com.example.shared.enumeration.EmployeeRole;
import com.example.shared.enumeration.UserRole;
import com.example.shared.exception.MyException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{
    private final EmployeeRepository employeeRepository;
    private final BusRepository busRepository;
    private final AccountRepository accountRepository;

    @Override
    public Page<GetListEmployeeOutput> getListEmployee(ListEmployeeFilterParam filterParam,
                                                       Pageable pageable) {
        try {
            if (filterParam.getRoles() != null && filterParam.getRoles().isEmpty()) {
                filterParam.setRoles(null);
            }

            Page<GetListEmployeeDTO> listEmployeeDTOS = employeeRepository.getListEmployee(
                    filterParam.getId(),
                    filterParam.getName(),
                    filterParam.getPhoneNumber(),
                    filterParam.getBusId(),
                    filterParam.getBusNumberPlate(),
                    filterParam.getRoles(),
                    pageable
            );
            return listEmployeeDTOS.map(GetListEmployeeOutput::fromDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(null,
                    "GET_LIST_EMPLOYEE_ERROR",
                    "Invalid input",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public void addEmployee(AddEmployeeInput input) {
        // validate if employee already exists
        Employee employee = employeeRepository.findByPhoneNumber(input.getPhoneNumber())
            .orElse(null);
        if (employee != null) {
            throw new MyException(null,
                    "EMPLOYEE_ALREADY_EXISTS",
                    "Employee already exists",
                    HttpStatus.BAD_REQUEST);
        }

        // validate if account already exists
        if (input.getUsername() == null || input.getPassword() == null) {
            throw new MyException(null,
                    "INVALID_INPUT",
                    "Username and password are required",
                    HttpStatus.BAD_REQUEST);
        }
        Account account = accountRepository.findByUsername(input.getUsername())
            .orElse(null);
        if (account != null) {
            throw new MyException(null,
                    "USERNAME_ALREADY_EXISTS",
                    "Username already exists",
                    HttpStatus.BAD_REQUEST);
        }
        account = Account.builder()
            .username(input.getUsername())
            .password(input.getPassword())
            .role(UserRole.EMPLOYEE)
            .build();
        account = accountRepository.save(account);


        if (input.getBusId() != null) {
            Bus bus = busRepository.findById(input.getBusId())
                .orElseThrow(() -> new MyException(null,
                    "BUS_NOT_FOUND",
                    "Bus not found",
                    HttpStatus.NOT_FOUND));
            validateDriverAndEmployeeRole(input, bus);
        }

        if (input.getNumberPlate() != null) {
            Bus bus = busRepository.findByNumberPlate(input.getNumberPlate());
                if (bus == null) {
                    throw new MyException(null,
                    "BUS_NOT_FOUND",
                    "Bus not found",
                    HttpStatus.NOT_FOUND);
                }
            validateDriverAndEmployeeRole(input, bus);

            input.setBusId(bus.getId());
        }

        employee = Employee.builder()
                .account(account)
                .name(input.getName())
                .phoneNumber(input.getPhoneNumber())
                .avatar(input.getAvatar())
                .dob(input.getDob())
                .busId(input.getBusId())
                .role(input.getRole())
                .build();
        employeeRepository.save(employee);


        if (input.getBusId() != null) {
            Bus bus = busRepository.findById(input.getBusId())
                .orElseThrow(() -> new MyException(null,
                    "BUS_NOT_FOUND",
                    "Bus not found",
                    HttpStatus.NOT_FOUND));
            if (input.getRole() == EmployeeRole.DRIVER) {
                bus.setDriverId(employee.getId());
            }
            if (input.getRole() == EmployeeRole.DRIVER_MATE) {
                bus.setDriverMateId(employee.getId());
            }
            busRepository.save(bus);
        }
    }

    private void validateDriverAndEmployeeRole(AddEmployeeInput input, Bus bus) {
        if (bus.getDriverId() != null && input.getRole() == EmployeeRole.DRIVER) {
            throw new MyException(null,
                "BUS_ALREADY_ASSIGNED",
                "Bus already assigned to another driver",
                HttpStatus.BAD_REQUEST);
        }
        if (bus.getDriverMateId() != null && input.getRole() == EmployeeRole.DRIVER_MATE) {
            throw new MyException(null,
                "BUS_ALREADY_ASSIGNED",
                "Bus already assigned to another driver mate",
                HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public void updateEmployee(UpdateEmployeeInput input) {
        Employee employee = employeeRepository.findById(input.getId())
            .orElseThrow(() -> new MyException(null,
                    "EMPLOYEE_NOT_FOUND",
                    "Employee not found",
                    HttpStatus.NOT_FOUND));

        Bus bus = null;
        if (input.getBusId() != null && input.getBusNumberPlate() == null) {
            bus = busRepository.findById(input.getBusId())
                .orElseThrow(() -> new MyException(null,
                    "BUS_NOT_FOUND",
                    "Bus not found",
                    HttpStatus.NOT_FOUND));
        }
        if (input.getBusId() != null && bus.getDriverId() != null
            && !bus.getDriverId().equals(input.getId())
            && input.getRole() != null
            && input.getRole() == EmployeeRole.DRIVER) {
            throw new MyException(null,
                    "BUS_ALREADY_ASSIGNED",
                    "Bus already assigned to another driver",
                    HttpStatus.BAD_REQUEST);
        }
        if (input.getBusId() != null && bus.getDriverMateId() != null
            && !bus.getDriverMateId().equals(input.getId())
            && input.getRole() != null
            && input.getRole() == EmployeeRole.DRIVER_MATE) {
            throw new MyException(null,
                    "BUS_ALREADY_ASSIGNED",
                    "Bus already assigned to another driver mate",
                    HttpStatus.BAD_REQUEST);
        }

        if (input.getBusNumberPlate() != null) {
            bus = busRepository.findByNumberPlate(input.getBusNumberPlate());
            if (bus == null) {
                throw new MyException(null,
                    "BUS_NOT_FOUND",
                    "Bus not found",
                    HttpStatus.NOT_FOUND);
            }
            if (bus.getDriverId() != null && !bus.getDriverId().equals(input.getId())
                && input.getRole() != null
                && input.getRole() == EmployeeRole.DRIVER) {
                throw new MyException(null,
                    "BUS_ALREADY_ASSIGNED",
                    "Bus already assigned to another driver",
                    HttpStatus.BAD_REQUEST);
            }
            if (bus.getDriverMateId() != null && !bus.getDriverMateId().equals(input.getId())
                && input.getRole() != null
                && input.getRole() == EmployeeRole.DRIVER_MATE) {
                throw new MyException(null,
                    "BUS_ALREADY_ASSIGNED",
                    "Bus already assigned to another driver mate",
                    HttpStatus.BAD_REQUEST);
            }
            input.setBusId(bus.getId());
        }

        if (input.getBusId() != null) {
            if (input.getRole() == EmployeeRole.DRIVER) {
                bus.setDriverId(input.getId());
            }
            if (input.getRole() == EmployeeRole.DRIVER_MATE) {
                bus.setDriverMateId(input.getId());
            }
            busRepository.save(bus);
        } else {
            if (employee.getBusId() != null) {
                bus = busRepository.findById(employee.getBusId())
                    .orElseThrow(() -> new MyException(null,
                        "BUS_NOT_FOUND",
                        "Bus not found",
                        HttpStatus.NOT_FOUND));
                if (bus.getDriverId() != null && bus.getDriverId().equals(input.getId())) {
                    bus.setDriverId(null);
                }
                if (bus.getDriverMateId() != null && bus.getDriverMateId().equals(input.getId())) {
                    bus.setDriverMateId(null);
                }
                busRepository.save(bus);
            }
        }

        employee.setName(input.getName());
        employee.setPhoneNumber(input.getPhoneNumber());
        employee.setAvatar(input.getAvatar());
        employee.setDob(input.getDob());
        employee.setBusId(input.getBusId());
        employee.setRole(input.getRole());
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new MyException(null,
                    "EMPLOYEE_NOT_FOUND",
                    "Employee not found",
                    HttpStatus.NOT_FOUND));

        if (employee.getBusId() != null) {
            Bus bus = busRepository.findById(employee.getBusId())
                .orElseThrow(() -> new MyException(null,
                    "BUS_NOT_FOUND",
                    "Bus not found",
                    HttpStatus.NOT_FOUND));
            if (bus.getDriverId() != null && bus.getDriverId().equals(id)) {
                bus.setDriverId(null);
            }
            if (bus.getDriverMateId() != null && bus.getDriverMateId().equals(id)) {
                bus.setDriverMateId(null);
            }
            busRepository.save(bus);
        }
        employeeRepository.delete(employee);
    }

    @Override
    public List<Employee> getAvailableEmployees(EmployeeRole role, String query) {
        return employeeRepository.getAvailableEmployees(role, query);
    }
}
