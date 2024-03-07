package thesisdefensejuryassignment.thesisdefenseserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import thesisdefensejuryassignment.thesisdefenseserver.entity.ThesisDefensePlan;
import thesisdefensejuryassignment.thesisdefenseserver.repo.ThesisDefensePlanRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@AllArgsConstructor
@Service

public class ThesisDefensePlanServiceImpl implements ThesisDefensePlanService {


    private ThesisDefensePlanRepo graduationTermRepo;
    @Override
    public List<ThesisDefensePlan> getAllThesisDefensePlan() {

        return graduationTermRepo.findAll();
    }

    @Override
    public ThesisDefensePlan createThesisDefensePlan(ThesisDefensePlan graduationTerm) {
        return graduationTermRepo.save(graduationTerm);
    }
    @Override
    public ThesisDefensePlan getThesisDefensePlanById(String id) {
        ThesisDefensePlan foundDefensePlan = graduationTermRepo.findById(id).orElse(null);
        return foundDefensePlan;
    }
}
