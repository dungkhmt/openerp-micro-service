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
public class ApplicationType {

    @Id
    @Column(name = "application_type_id")
    private String applicationTypeId;
    private String description;
    private Date createdStamp;
    private Date lastUpdatedStamp;

    public ApplicationType(String applicationTypeId, String description) {
        this.applicationTypeId = applicationTypeId;
        this.description = description;
    }

    public ApplicationType() {
    }
}
