package com.hust.baseweb.applications.education.thesisdefensejury.service;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.ThesisDefensePlan;
import com.hust.baseweb.applications.education.thesisdefensejury.models.Response;
import com.hust.baseweb.applications.education.thesisdefensejury.models.ThesisDefensePlanIM;

import java.util.List;

public interface ThesisDefensePlanService {
    List<ThesisDefensePlan> getAllThesisDefensePlan();
    Response createThesisDefensePlan(ThesisDefensePlanIM request);
    Response findById(String id);
}
