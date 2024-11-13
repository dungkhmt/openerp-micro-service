package com.hust.baseweb.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.sql.Date;
import java.util.UUID;

/**
 * Person
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @Id
    @Column(name = "party_id")
    private UUID partyId;

    //@Column(name="first_name")
    private String firstName;

    //@Column(name="middle_name")
    private String middleName;

    //@Column(name="last_name")
    private String lastName;

    //@Column(name="gender")
    private String gender;

    //@Column(name="birth_date")
    private Date birthDate;

    //private String birthDate;
    public String getFullName() {
        return firstName + " " + middleName + " " + lastName;
    }

    public Person(UUID partyId, String firstName, String middleName, String lastName, String gender, Date birthDate) {
        this.partyId = partyId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public BasicInfoModel getBasicInfoModel() {
        return new BasicInfoModel(partyId, firstName + " " + middleName + " " + lastName, gender);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BasicInfoModel {

        private UUID partyId;
        private String fullName;
        private String gender;
    }


    @Transient
    String name;

    @Transient
    String userName;


    @Override
    public String toString() {
        return "Person{" +
               "partyId=" + partyId +
               ", firstName='" + firstName + '\'' +
               ", middleName='" + middleName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", gender='" + gender + '\'' +
               ", birthDate=" + birthDate +
               ", name='" + name + '\'' +
               ", userName='" + userName + '\'' +
               '}';
    }
}

