package com.example.api.controllers.client.dto;

import com.example.api.services.account.dto.ParentSearchOutput;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetParentResponse {
    private Long id;

    private String name;

    private String avatar;

    private Instant dob;

    private String phoneNumber;

    private Instant createdAt;

    private Instant updatedAt;


    public static GetParentResponse from(ParentSearchOutput output) {
        GetParentResponse response = new GetParentResponse();
        response.setId(output.getId());
        response.setName(output.getName());
        response.setAvatar(output.getAvatar());
        response.setDob(output.getDob() != null ? Instant.ofEpochMilli(output.getDob().toEpochMilli()) : null);
        response.setPhoneNumber(output.getPhoneNumber());
        response.setCreatedAt(Instant.now());
        response.setUpdatedAt(Instant.now());
        return response;
    }
}
