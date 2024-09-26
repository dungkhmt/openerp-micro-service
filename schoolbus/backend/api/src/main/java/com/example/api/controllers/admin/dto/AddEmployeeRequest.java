package com.example.api.controllers.admin.dto;

import com.example.api.services.employee.dto.AddEmployeeInput;
import com.example.shared.enumeration.EmployeeRole;
import com.example.shared.utils.DateConvertUtil;
import java.time.Instant;
import lombok.Data;

@Data
public class AddEmployeeRequest {
    private Long accountId;
    private String name;
    private String phoneNumber;
    private String address;
    private String avatar;
    private String dob;
    private Long busId;
    private String numberPlate;
    private EmployeeRole role;

    // for create account
    private String username;
    private String password;

    public AddEmployeeInput toInput() {
        return AddEmployeeInput.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .address(address)
                .avatar(avatar)
                .dob(DateConvertUtil.convertStringToInstant(dob))
                .busId(busId)
                .role(role)
                .numberPlate(numberPlate)
                .username(username)
                .password(password)
                .build();
    }
}
