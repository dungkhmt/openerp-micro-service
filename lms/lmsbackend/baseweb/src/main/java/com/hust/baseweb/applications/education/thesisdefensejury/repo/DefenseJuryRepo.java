package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.DefenseJury;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.Thesis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DefenseJuryRepo extends JpaRepository<DefenseJury, UUID> {
    Page<DefenseJury> findAll(Pageable pageable);
    @Override
    @Query(value = "select * from defense_jury d where d.id= :Id", nativeQuery = true)
    Optional<DefenseJury> findById(UUID Id);

    @Query(value = "select * from defense_jury df where df.name like :name% ", nativeQuery = true)
    List<DefenseJury> findByName(String name);

    @Query(value = "select * from defense_jury d where d.name like %:name%", nativeQuery = true)
    List<DefenseJury> findAllByName(String name);

    @Query(value = "select * from defense_jury d where d.thesis_defense_plan_id = :planId", nativeQuery = true)
    List<DefenseJury> findAllByPlanId(String planId);
}
