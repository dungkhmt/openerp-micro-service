package com.hust.baseweb.applications.education.recourselink.repo;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EducationResourceRepo extends JpaRepository<EducationResource, UUID>, CrudRepository<EducationResource,UUID> {
    @Query(value = "select * from edu_resource er where er.domain_id = :domainId",nativeQuery = true)
    Page<EducationResource> findByDomainId(UUID domainId,Pageable pageable);

    @Query(value = "select * from edu_resource er where er.id = :id and er.domain_id = :domainId",nativeQuery = true)
    Optional<EducationResource> findByIdAndDomainId(UUID id, UUID domainId);

    @Query(value = "select * from edu_resource er where er.domain_id = :domainId " +
                   "and er.description like %:des%",nativeQuery = true)
    List<EducationResource> findAllByDescriptionAndDomainId(String des,UUID domainId);
}
