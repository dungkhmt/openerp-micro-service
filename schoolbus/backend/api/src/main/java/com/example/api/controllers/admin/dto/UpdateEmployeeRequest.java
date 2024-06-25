package com.example.api.controllers.admin.dto;

import com.example.api.services.employee.dto.UpdateEmployeeInput;
import com.example.shared.enumeration.BusStatus;
import com.example.shared.enumeration.EmployeeRole;
import com.example.shared.utils.DateConvertUtil;
import java.time.Instant;
import lombok.Data;

@Data
public class UpdateEmployeeRequest {
    private Long id;
    private String name;
    private String phoneNumber;
    private String dob;
    private String avatar;
    private Long busId;
    private EmployeeRole role;
    private String busNumberPlate;

    public UpdateEmployeeInput toInput() {
        return UpdateEmployeeInput.builder()
                .id(id)
                .name(name)
                .phoneNumber(phoneNumber)
                .dob(DateConvertUtil.convertStringToInstant(dob))
                .avatar(avatar)
                .busId(busId)
                .role(role)
                .busNumberPlate((busNumberPlate == null || busNumberPlate.isEmpty()) ? null : busNumberPlate)
                .build();
    }
}
