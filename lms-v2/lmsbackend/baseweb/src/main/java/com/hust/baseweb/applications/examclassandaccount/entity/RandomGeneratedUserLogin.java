package com.hust.baseweb.applications.examclassandaccount.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "random_generated_user_login")
public class RandomGeneratedUserLogin {
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_DISABLED = "DISABLED";

    @Id
    @Column(name = "user_login_id")
    private String userLoginId;

    @Column(name="password")
    private String password;

    @Column(name = "status")
    private String status;
}
