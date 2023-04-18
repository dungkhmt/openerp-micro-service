package com.hust.baseweb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonModel {

    private String userName;

    private String email;

    private String password;

    private List<String> roles;

    private String partyCode;

    private String firstName;

    private String lastName;

    private String middleName;

    private String gender;

    private Date birthDate;

    private String affiliations;

    public String getFullName(){
        return lastName + " " + middleName + " " + firstName;
    }

    public PersonModel(String userName, String lastName, String middleName, String firstName){
        this.userName = userName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
    }
    public PersonModel(
        String userName,
        String password,
        List<String> roles,
        String partyCode,
        String firstName,
        String lastName,
        String middleName,
        String gender,
        Date birthDate,
        String affiliations
    ) {
        this.userName = userName;
        this.password = password;
        this.roles = roles;
        this.partyCode = partyCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.affiliations = affiliations;
    }


}
