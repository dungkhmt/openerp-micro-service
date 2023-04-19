package com.hust.baseweb.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="account_activation")

public class AccountActivation {
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_ACTIVATED = "ACTIVATED";
    public static final String STATUS_DISABLED = "DISABLED";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    @Column(name="user_login_id")
    private String userLoginId;

    @Column(name="status_id")
    private String statusId;

    @Column(name="created_stamp")
    private Date createdStamp;
}
