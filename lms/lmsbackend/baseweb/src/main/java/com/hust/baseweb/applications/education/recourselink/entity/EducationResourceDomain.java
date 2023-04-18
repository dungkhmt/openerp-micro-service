package com.hust.baseweb.applications.education.recourselink.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "edu_resource_domain")
public class EducationResourceDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID Id;

    @Column(name = "name")
    private String name;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updateDateTime;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private LocalDateTime createDateTime;
//    @OneToMany(mappedBy= "domainId")
//    private List<EducationResource> resourceId;
    public EducationResourceDomain(){}
    public EducationResourceDomain(String _recourse_domain_name){
        this.name  = _recourse_domain_name;
    }
    @Override
    public String toString() {
        return "education_resource_domain{" +
               "name='" + name + '\'' +
               '}';
    }

}
