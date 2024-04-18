package thesisdefensejuryassignment.thesisdefenseserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseSession;
import thesisdefensejuryassignment.thesisdefenseserver.repo.DefenseSessionRepo;

import java.util.List;
@Log4j2
@AllArgsConstructor
@Service

public class DefenseSessionServiceImpl implements DefenseSessionService{
    @Autowired
    private DefenseSessionRepo defenseSessionRepo;
    @Override
    public List<DefenseSession> getAllDefenseSession() {
        return defenseSessionRepo.findAll();
    }
}
