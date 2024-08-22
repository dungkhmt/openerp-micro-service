package com.example.api.controllers.admin.dto;

import com.example.shared.enumeration.RequestRegistrationStatus;
import java.util.List;
import lombok.Data;

@Data
public class RequestRegistrationFilterParam {
    private String studentName;
    private String parentName;
    private List<RequestRegistrationStatus> statuses;
    private String address;
}
