package com.hust.baseweb.applications.education.thesisdefensejury.repo;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.AcademicKeyword;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.ThesisDefensePlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicKeywordRepo extends JpaRepository<AcademicKeyword, String> {

}
