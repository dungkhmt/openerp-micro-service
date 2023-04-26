package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelUserRegisteredClassInfo {
//    private String email;
//    private String fullName;
    private String userName;
    private String middleName;
    private String firstName;
    private String lastName;
    private String status;
    //private String roleId;
//    public ModelUserRegisteredClassInfo(String email, String userName, String middleName, String firstName, String lastName) {
//        this.email = email;
//        this.userName = userName;
//        this.middleName = middleName;
//        this.firstName = firstName;
//        this.lastName = lastName;
//    }
}
