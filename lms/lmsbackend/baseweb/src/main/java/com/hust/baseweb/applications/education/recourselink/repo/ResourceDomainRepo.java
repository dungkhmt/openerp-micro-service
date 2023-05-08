package com.hust.baseweb.applications.education.recourselink.repo;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ResourceDomainRepo extends JpaRepository<EducationResourceDomain, UUID> {
    Page<EducationResourceDomain> findAll(Pageable pageable);
    @Override
    @Query(value = "select * from edu_resource_domain where id= :Id",nativeQuery = true)
    Optional<EducationResourceDomain> findById(UUID Id);

    @Query(value = "select * from edu_resource_domain rd where rd.name like :name%",nativeQuery = true)
    List<EducationResourceDomain> findByName(String name);
}
