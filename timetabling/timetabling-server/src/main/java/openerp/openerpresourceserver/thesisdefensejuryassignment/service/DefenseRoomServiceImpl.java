package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseRoom;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.DefenseRoomRepo;

import java.util.List;

@Log4j2
@AllArgsConstructor
@Service
public class DefenseRoomServiceImpl implements DefenseRoomService{
    @Autowired
    private DefenseRoomRepo defenseRoomRepo;
    @Override
    public List<DefenseRoom> getAllRoom() {
        return defenseRoomRepo.findAll();
    }
}
