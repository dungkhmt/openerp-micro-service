package com.hust.baseweb.applications.education.recourselink.service;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResource;
import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import com.hust.baseweb.applications.education.recourselink.repo.EducationResourceRepo;
import com.hust.baseweb.applications.education.recourselink.repo.ResourceDomainRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
@Slf4j
public class EducationResourceServiceImpl implements EducationResourceService{

    private final EducationResourceRepo resourceRepository;
    private final ResourceDomainRepo resourceDomainRepo;
    @Override
    public Page<EducationResource> findByDomainId(UUID domainId,Pageable pageable) {
       return resourceRepository.findByDomainId(domainId,pageable);
    }

    @Override
    public Optional<EducationResource> findByIdAndDomainId(UUID id, UUID domainId) {
        return resourceRepository.findByIdAndDomainId(id,domainId);
    }

    @Override
    public Boolean createResource(UUID domainId, EducationResource resource) {
        Optional<EducationResourceDomain> rd = resourceDomainRepo.findById(domainId);
        if (!rd.isPresent()){
            return false;
        }
        EducationResourceDomain ed = rd.get();
        resource.setDomainId(ed);
        resourceRepository.save(resource);
        return true;
    }

    @Override
    public List<EducationResource> findResourceWithFilter(EducationResource request, UUID domainId) {
        return resourceRepository.findAllByDescriptionAndDomainId(request.getDescription(),domainId);
    }
}
