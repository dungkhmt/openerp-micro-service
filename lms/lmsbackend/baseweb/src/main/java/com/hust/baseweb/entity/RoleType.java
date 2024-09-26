package com.hust.baseweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class RoleType {

    @Id
    @Column(name = "role_type_id")
    private String roleTypeId;

}
