package com.example.api.controllers.admin.dto;

import com.example.api.services.account.dto.ParentAddInput;
import com.example.shared.utils.DateConvertUtil;
import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
public class ParentAddRequest {
    private String name;
    private String avatar;
    private String dob;
    private String phoneNumber;
    private String username;
    private String password;
    private List<Long> studentIds;

    public ParentAddInput toInput() {
        Instant dob = DateConvertUtil.convertStringToInstant(this.dob);
        return ParentAddInput.builder()
            .name(this.name)
            .avatar(this.avatar)
            .dob(dob)
            .phoneNumber(this.phoneNumber)
            .username(this.username)
            .password(this.password)
            .studentIds(this.studentIds)
            .build();
    }
}
