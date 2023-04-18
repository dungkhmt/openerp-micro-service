package com.hust.baseweb.applications.education.thesisdefensejury.service;

import com.hust.baseweb.applications.education.recourselink.entity.EducationResourceDomain;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.DefenseJury;
import com.hust.baseweb.applications.education.thesisdefensejury.entity.DefenseJuryTeacher;
import com.hust.baseweb.applications.education.thesisdefensejury.models.DefenseJuryIM;
import com.hust.baseweb.applications.education.thesisdefensejury.models.DefenseJuryOM;
import com.hust.baseweb.applications.education.thesisdefensejury.models.Response;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisWithDefenseJuryIM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DefenseJuryService {
    DefenseJury createDefenseJury(DefenseJuryIM jury);
    Optional<DefenseJury> findById(UUID id);
    DefenseJuryOM getDefenseJury(DefenseJury jury);
    Page<DefenseJury> findAll(Pageable pageable);
    Response findAllBelongPlanID(String planId);
    List<DefenseJuryOM> searchByDefenseJury(String name);
    Response getListDefenseJuryTeachers(UUID defenseJuryID);
    Response deleteTheisByIdAtIt(ThesisWithDefenseJuryIM request,UUID juryId);
    Response addTheisByIdAtIt(ThesisWithDefenseJuryIM request,UUID juryId);
}
