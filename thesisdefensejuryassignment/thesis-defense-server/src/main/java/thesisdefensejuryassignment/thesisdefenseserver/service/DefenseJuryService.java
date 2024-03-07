package thesisdefensejuryassignment.thesisdefenseserver.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;
import thesisdefensejuryassignment.thesisdefenseserver.models.DefenseJuryIM;


public interface DefenseJuryService {
    public DefenseJury createNewDefenseJury(DefenseJuryIM defenseJury);
}
