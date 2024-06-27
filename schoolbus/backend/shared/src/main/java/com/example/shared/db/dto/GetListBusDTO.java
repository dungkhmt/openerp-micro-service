package com.example.shared.db.dto;

import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;
import java.util.List;

public interface GetListBusDTO {
    Bus getBus();
    Employee getDriver();
    Employee getDriverMate();
}
