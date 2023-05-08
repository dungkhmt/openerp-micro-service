package com.hust.baseweb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ModelUserSearchResponse {
    private String userName;
    private String middleName;
    private String firstName;
    private String lastName;
    private String status;

}
