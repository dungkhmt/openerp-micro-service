package thesisdefensejuryassignment.thesisdefenseserver.service;

import thesisdefensejuryassignment.thesisdefenseserver.entity.ThesisDefensePlan;

import java.util.List;

public interface ThesisDefensePlanService {
    List<ThesisDefensePlan> getAllThesisDefensePlan();

    ThesisDefensePlan createThesisDefensePlan(ThesisDefensePlan graduationTerm);

    ThesisDefensePlan getThesisDefensePlanById(String id);
}
