package com.hust.baseweb.rest.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hust.baseweb.entity.Party;
import com.hust.baseweb.entity.SecurityGroup;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * DPersonUserLogin
 */
@Data
@Entity
@Table(name = "user_login")
public class DPersonUserLogin {

    @Id
    @Column(name = "user_login_id", updatable = false, nullable = false)
    private String userLoginId;

    private boolean isSystem;

    private boolean enabled;

    @JoinColumn(name = "party_id", referencedColumnName = "party_id")
    @OneToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Party party;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_login_security_group",
        joinColumns = @JoinColumn(name = "user_login_id", referencedColumnName = "user_login_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "group_id"))
    private List<SecurityGroup> roles;
    private Date disabledDateTime;


    private Date createdStamp;


    private Date lastUpdatedStamp;
}
