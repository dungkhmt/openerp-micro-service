package com.hust.baseweb.applications.examclassandaccount.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginModel {
    private String userLoginId;
    private String studentCode;
    private String fullName;
}
