package com.example.api.controllers.client.dto;

import com.example.api.services.account.dto.StudentSearchOutput;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStudentResponse {
    private Long id;

    private String name;

    private String avatar;

    private Instant dob;

    private String phoneNumber;

    private String studentClass;

    private Long parentId;

    private String parentName;

    private String parentPhoneNumber;

    private Instant createdAt;

    private Instant updatedAt;


    public static GetStudentResponse from(StudentSearchOutput output) {
        GetStudentResponse response = new GetStudentResponse();
        response.setId(output.getId());
        response.setName(output.getName());
        response.setAvatar(output.getAvatar());
        response.setDob(output.getDob() != null ? Instant.ofEpochMilli(output.getDob().toEpochMilli()) : null);
        response.setPhoneNumber(output.getPhoneNumber());
        response.setStudentClass(output.getStudentClass());
        response.setParentId(output.getParentId());
        response.setParentName(output.getParentName());
        response.setParentPhoneNumber(output.getParentPhoneNumber());
        response.setCreatedAt(output.getCreatedAt() != null ?
            Instant.ofEpochMilli(output.getCreatedAt().toEpochMilli()) : null);
        response.setUpdatedAt(output.getUpdatedAt() != null ?
            Instant.ofEpochMilli(output.getUpdatedAt().toEpochMilli()) : null);
        return response;
    }
}
