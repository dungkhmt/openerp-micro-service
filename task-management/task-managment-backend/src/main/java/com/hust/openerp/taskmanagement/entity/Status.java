package com.hust.openerp.taskmanagement.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "status_item")
public class Status {
    @Id
    @Column(name = "status_id")
    private String statusId;

    @Column(name = "status_type_id")
    private String type;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "description")
    private String description;
    
    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;
    
    @Column(name = "is_default")
    @JsonProperty("isDefault")
    private boolean isDefault;
}
