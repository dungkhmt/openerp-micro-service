package com.example.api.controllers.admin.dto;

import com.example.api.services.account.dto.StudentAddInput;
import com.example.shared.utils.DateConvertUtil;
import java.time.Instant;
import lombok.Data;

@Data
public class StudentAddRequest {
    private Long parentId;
    private String name;
    private String avatar;
    private String dob;
    private String phoneNumber;
    private String studentClass;

    public StudentAddInput toInput() {
        return StudentAddInput.builder()
            .parentId(this.parentId)
            .name(this.name)
            .avatar(this.avatar)
            .dob(DateConvertUtil.convertStringToInstant(this.dob))
            .phoneNumber(this.phoneNumber)
            .studentClass(this.studentClass)
            .build();
    }
}
