package com.example.api.services.employee.dto;

import com.example.shared.db.dto.GetListEmployeeDTO;
import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.Employee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetListEmployeeOutput {
    private Employee employee;
    private Bus bus;

    public static GetListEmployeeOutput fromDto(GetListEmployeeDTO dto) {
        return GetListEmployeeOutput.builder()
            .employee(dto.getEmployee())
            .bus(dto.getBus())
            .build();
    }
}
