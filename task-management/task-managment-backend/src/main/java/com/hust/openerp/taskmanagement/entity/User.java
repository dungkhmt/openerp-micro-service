package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Entity
@Table(name = "user_login")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_login_id", updatable = false, nullable = false)
    private String id;

    private String email;

    private String firstName;

    private String lastName;

    private boolean enabled;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastModifiedDate;
}
