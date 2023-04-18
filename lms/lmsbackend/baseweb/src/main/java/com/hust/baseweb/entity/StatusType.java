package com.hust.baseweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Setter
@Getter
public class StatusType {

    @Id
    @Column(name = "status_type_id")
    private String id;

    @JoinColumn(name = "parent_type_id", referencedColumnName = "status_type_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private StatusType parentType;

    private String description;

    private Date createdStamp;
    private Date lastUpdatedStamp;

    public StatusType(String id, StatusType parentType, String description) {
        this.id = id;
        this.parentType = parentType;
        this.description = description;
    }

    public StatusType() {
    }
}

