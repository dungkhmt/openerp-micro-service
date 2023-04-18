package com.hust.baseweb.applications.education.entity;

import com.hust.baseweb.applications.education.classmanagement.enumeration.RegistStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "edu_class_registration")
@EntityListeners(AuditingEntityListener.class)
public class ClassRegistration {
    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_PARTICIPANT = "PARTICIPANT";

    public static final String STATUS_WAITING_FOR_APPROVAL = "WAITING_FOR_APPROVAL";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REFUSED = "REFUSED";

    @EmbeddedId
    private ClassRegistrationId id;

    @Enumerated(EnumType.STRING)
    private RegistStatus status;

    @LastModifiedDate
    private Date lastUpdatedStamp;

    @CreatedDate
    @Column(updatable = false)
    private Date createdStamp;
}
