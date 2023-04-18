package com.hust.baseweb.applications.education.recourselink.service;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResource;
import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import com.hust.baseweb.applications.education.recourselink.enumeration.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EducationResourceDomainService {
    Page<EducationResourceDomain> findAll(Pageable pageable);

    Boolean createDomain(EducationResourceDomain domain);
    EducationResourceDomain updateDomain(UUID id,EducationResourceDomain domain);
    Optional<EducationResourceDomain> findById(UUID id);
}
