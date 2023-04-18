package com.hust.baseweb.applications.education.recourselink.entity;

import com.hust.baseweb.applications.education.recourselink.enumeration.ResourceStatus;
import com.hust.baseweb.entity.UserLogin;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "edu_resource") // Entity map voi bang edu_resource
@NoArgsConstructor
public class EducationResource {

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public EducationResourceDomain getDomainId() {
        return domainId;
    }

    public void setDomainId(EducationResourceDomain domainId) {
        this.domainId = domainId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceStatus getStatusId() {
        return statusId;
    }

    public void setStatusId(ResourceStatus statusId) {
        this.statusId = statusId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;


    private String link;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "domain_id",referencedColumnName="id", nullable = false)
    private EducationResourceDomain domainId;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "user_login_id",referencedColumnName="id", nullable = false)
     //private UserLogin userLogin;

    private String description;

    // Van de nam o day khi kieu du lieu Java khong tuong ung voi kieu cua cot du lieu trong db
    @Enumerated(EnumType.STRING)
    private ResourceStatus statusId;
}
