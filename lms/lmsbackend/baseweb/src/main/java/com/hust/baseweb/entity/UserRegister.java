package com.hust.baseweb.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRegister {
    public static final String STATUS_DISABLED = "USER_DISABLED";
    public static final String STATUS_REGISTERED = "USER_REGISTERED";
    public static final String STATUS_APPROVED = "USER_APPROVED";

    @Id
    @Column(name = "user_login_id")
    private String userLoginId;      // varchar(60),
    private String password;         // varchar(20),
    private String email;            // varchar(30),
    private String firstName;         // varchar(100),
    private String middleName;         // varchar(100),
    private String lastName;         // varchar(100),

    private String registeredRoles;
    //private String affiliationId;
    private String affiliations;

    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private StatusItem statusItem;         // varchar(60),

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InputModel {

        private String userLoginId;      // varchar(60),
        private String password;         // varchar(20),
        private String email;            // varchar(30),
        private String firstName;         // varchar(100),
        private String middleName;         // varchar(100),
        private String lastName;         // varchar(100),

        private String registeredRoles;
        private String affiliations;

        public UserRegister createUserRegister(StatusItem statusItem) {
            return new UserRegister(
                userLoginId,
                password,
                email,
                firstName,
                middleName,
                lastName,
                registeredRoles,
                affiliations,
                statusItem);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OutputModel {

        private String userLoginId;      // varchar(60),
        private String email;            // varchar(30),
        private String firstName;         // varchar(100),
        private String middleName;        // varchar(100),
        private String lastName;          // varchar(100),
    }

    public OutputModel toOutputModel() {
        return new OutputModel(userLoginId, email, firstName, middleName, lastName);
    }
}
