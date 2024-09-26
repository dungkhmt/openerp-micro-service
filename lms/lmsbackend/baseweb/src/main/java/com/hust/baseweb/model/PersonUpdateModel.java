package com.hust.baseweb.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class PersonUpdateModel {

    private String firstName;
    private String lastName;
    private String middleName;
    private Date birthDate;
    private String partyCode;
    private List<String> roles;
    private String enabled;
    private String email;

    public PersonUpdateModel() {
    }
}
