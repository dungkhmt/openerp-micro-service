package com.example.shared.db.dto;

import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;

public interface GetListEmployeeDTO {
    Employee getEmployee();
    Bus getBus();
}
