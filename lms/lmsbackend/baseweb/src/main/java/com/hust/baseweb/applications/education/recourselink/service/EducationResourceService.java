package com.hust.baseweb.applications.education.recourselink.service;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResource;
import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.rmi.server.UID;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EducationResourceService {
    Page<EducationResource> findByDomainId(UUID domainId,Pageable pageable);
    Optional<EducationResource> findByIdAndDomainId(UUID id,UUID domainId);
    Boolean createResource(UUID domainId,EducationResource resource);
    List<EducationResource> findResourceWithFilter(EducationResource request,UUID domainId);
}
