package com.example.api.controllers.client.dto;

import com.example.api.services.account.dto.StudentUpdateInput;
import com.example.shared.utils.DateConvertUtil;
import lombok.Data;

@Data
public class ClientStudentUpdateRequest {
    private Long id;
    private String name;
    private String avatar;
    private String dob;
    private String phoneNumber;
    private String studentClass;
    private Long parentId;

    public StudentUpdateInput toInput() {
        return StudentUpdateInput.builder()
            .id(this.id)
            .name(this.name)
            .avatar(this.avatar)
            .dob(DateConvertUtil.convertStringToInstant(this.dob))
            .phoneNumber(this.phoneNumber)
            .studentClass(this.studentClass)
            .parentId(this.parentId)
            .build();
    }
}
