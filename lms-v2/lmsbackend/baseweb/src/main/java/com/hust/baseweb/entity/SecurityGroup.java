package com.hust.baseweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
public class SecurityGroup {

    @Id
    @Column(name = "group_id")
    private String groupId;

    private String description;

    @JoinTable(name = "SecurityGroupPermission",
               joinColumns = @JoinColumn(name = "group_id",
                                         referencedColumnName = "group_id"),
               inverseJoinColumns = @JoinColumn(name = "permission_id",
                                                referencedColumnName = "permission_id"))
    @OneToMany(fetch = FetchType.LAZY)
    private List<SecurityPermission> permissions;

    @Column(name = "group_name")
    private String name;

    private Date createdStamp;


    private Date lastUpdatedStamp;

    public SecurityGroup() {
    }
}
