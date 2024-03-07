package thesisdefensejuryassignment.thesisdefenseserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesisdefensejuryassignment.thesisdefenseserver.entity.AcademicKeyword;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;
import thesisdefensejuryassignment.thesisdefenseserver.entity.ThesisDefensePlan;
import thesisdefensejuryassignment.thesisdefenseserver.models.DefenseJuryIM;
import thesisdefensejuryassignment.thesisdefenseserver.repo.AcademicKeywordRepo;
import thesisdefensejuryassignment.thesisdefenseserver.repo.DefenseJuryRepo;
import thesisdefensejuryassignment.thesisdefenseserver.repo.ThesisDefensePlanRepo;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Service


public class DefenseJuryServiceImpl implements DefenseJuryService {

    @Autowired
    private DefenseJuryRepo defenseJuryRepo;
    @Autowired
    private ThesisDefensePlanRepo thesisDefensePlanRepo;
    @Autowired
    private AcademicKeywordRepo academicKeywordRepo;
    @Override
    public DefenseJury createNewDefenseJury(DefenseJuryIM defenseJury) {
        DefenseJury newDefenseJury = new DefenseJury();

        if ((defenseJury.getName().isEmpty()) || (defenseJury.getDefenseDate() == null)
                || (defenseJury.getThesisPlanName() == null)) {
            return null;
        }

        String thesisDefensePlanId = defenseJury.getThesisPlanName();
        ThesisDefensePlan thesisDefensePlan = thesisDefensePlanRepo.findById(thesisDefensePlanId).orElse(null);

        List<AcademicKeyword> academicKeywordList = new LinkedList<>();
        for (int i=0;i<defenseJury.getAcademicKeywordList().size();i++){
            AcademicKeyword foundAcademicKeyword = academicKeywordRepo.findById(defenseJury.getAcademicKeywordList().get(i)).orElse(null);
            if (foundAcademicKeyword == null){
                return null;
            }
            academicKeywordList.add(foundAcademicKeyword);
        }
        newDefenseJury.setThesisDefensePlan(thesisDefensePlan);
        newDefenseJury.setAcademicKeywordList(academicKeywordList);
        newDefenseJury.setName(defenseJury.getName());
        newDefenseJury.setDefenseDate(defenseJury.getDefenseDate());
        newDefenseJury.setCreatedTime(new Date());
        newDefenseJury.setMaxThesis(defenseJury.getMaxThesis());
        return defenseJuryRepo.save(newDefenseJury);
    }
}
