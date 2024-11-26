package com.hust.baseweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Application {

    @Id
    @Column(name = "application_id")
    private String applicationId;
    @JoinColumn(name = "application_type_id", referencedColumnName = "application_type_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ApplicationType type;
    @JoinColumn(name = "module_id", referencedColumnName = "application_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Application module;
    @JoinColumn(name = "permission_id", referencedColumnName = "permission_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private SecurityPermission permission;
    private Date createdStamp;
    private Date lastUpdatedStamp;

    public Application(String applicationId, ApplicationType type, Application module, SecurityPermission permission) {
        this.applicationId = applicationId;
        this.type = type;
        this.module = module;
        this.permission = permission;

    }

    public Application() {
    }
}
