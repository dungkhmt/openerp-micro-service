package com.hust.baseweb.applications.education.thesisdefensejury.service;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.Thesis;
import com.hust.baseweb.applications.education.thesisdefensejury.models.Response;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisFilter;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisIM;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisOM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ThesisService {

    Thesis createThesis(ThesisIM thesis);

    ThesisOM findById(UUID id);

    Page<ThesisOM> findAll(Pageable pageable);

    List<ThesisOM> searchByThesisName(String name);

    Response deleteThesis(UUID id, String UserId);

    Response editThesis(ThesisIM thesis);

    Response disableThesisWithDefenseJury(UUID id, UUID defenseJuryId);

    Response findAllBelongPlanID(String planId);

    Response filterThesis(ThesisFilter filter);
}
