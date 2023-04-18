package com.hust.baseweb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

//import java.util.Date;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class UserLoginWithPersonModel {
//
//    private String userLoginId;
//
//    private String email;
//
//    private String affiliations;
//    private String firstName;
//
//    private String middleName;
//
//    private String lastName;
//
//    private String gender;
//
//    private Date birthDate;
//
//    public String getFullName() {
//        return StringUtils.joinWith(" ", firstName, middleName, lastName);
//    }
//}

import java.util.Date;

public interface UserLoginWithPersonModel {

    String getUserLoginId();

    String getEmail();

    String getAffiliations();

    String getFirstName();

    String getMiddleName();

    String getLastName();

    String getGender();

    Date getBirthDate();

    default String getFullName() {
        return StringUtils.joinWith(" ", getFirstName(), getMiddleName(), getLastName());
    }
}
