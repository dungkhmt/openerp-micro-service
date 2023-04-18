package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.DefenseJury;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.ThesisDefensePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ThesisDefensePlanRepo extends JpaRepository<ThesisDefensePlan, String> {

    Optional<ThesisDefensePlan> findByName(String name);
    Optional<ThesisDefensePlan> findByNameAndAndId(String name, String id);
}
