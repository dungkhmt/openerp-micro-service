package openerp.openerpresourceserver.thesisdefensejuryassignment.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseRoom;
import openerp.openerpresourceserver.thesisdefensejuryassignment.service.DefenseRoomServiceImpl;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/defense-room")
public class DefenseRoomController {
    @Autowired
    private DefenseRoomServiceImpl defenseRoomService;

    @GetMapping("/get-all")
    public ResponseEntity<List<DefenseRoom>> getAllRoom(){
        return new ResponseEntity<>(defenseRoomService.getAllRoom(), HttpStatus.OK);
    }
}
