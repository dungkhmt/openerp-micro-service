package com.hust.baseweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class SecurityPermission {

    @Id
    @Column(name = "permission_id")
    private String permissionId;

    private String description;


    private Date createdStamp;


    private Date lastUpdatedStamp;

    public SecurityPermission() {
    }
}
