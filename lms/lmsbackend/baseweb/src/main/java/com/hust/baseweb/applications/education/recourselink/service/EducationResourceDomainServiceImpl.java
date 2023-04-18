package com.hust.baseweb.applications.education.recourselink.service;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import com.hust.baseweb.applications.education.recourselink.enumeration.ErrorCode;
import com.hust.baseweb.applications.education.recourselink.repo.EducationResourceRepo;
import com.hust.baseweb.applications.education.recourselink.repo.ResourceDomainRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
@Slf4j
public class EducationResourceDomainServiceImpl implements EducationResourceDomainService{

    private final ResourceDomainRepo resourceDomainRepo;

    @Override
    public Page<EducationResourceDomain> findAll(Pageable pageable) {
        return resourceDomainRepo.findAll(pageable);
    }

    @Override
    public Boolean createDomain(EducationResourceDomain domain) {
        // Todo: handle check if id is existed.
//        Optional<EducationResourceDomain> rd = resourceDomainRepo.findById(domain.getId());
//        if (rd.isPresent()){
//            return false;
//        }
//        if (Objects.equals(domain.getName(),rd.get().getName())) {
//            return false;
//        }
        List<EducationResourceDomain> rd = resourceDomainRepo.findByName(domain.getName());
        if (rd != null) {
            for (EducationResourceDomain ele : rd) {
                if (Objects.equals(domain.getName(), ele.getName())) {
                    return false;
                }
            }
        }
        resourceDomainRepo.save(domain);
        return true;
    }

    @Override
    public EducationResourceDomain updateDomain(UUID Id, EducationResourceDomain domain){
        Optional<EducationResourceDomain> rd = resourceDomainRepo.findById(Id);
        if (!rd.isPresent()){
            return null;
        }
        EducationResourceDomain ed = rd.get();
        ed.setName(domain.getName());
        ed.setUpdateDateTime(LocalDateTime.now());
        resourceDomainRepo.save(ed);
        rd = resourceDomainRepo.findById(Id);
        return rd.get();
    }

    @Override
    public Optional<EducationResourceDomain> findById(UUID id) {
        return resourceDomainRepo.findById(id);
    }
}
