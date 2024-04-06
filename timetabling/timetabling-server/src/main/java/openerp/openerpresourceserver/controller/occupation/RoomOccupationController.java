package openerp.openerpresourceserver.controller.occupation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.service.RoomOccupationService;


@RestController
@RequestMapping("/room-occupation")
public class RoomOccupationController {

    @Autowired
    private RoomOccupationService roomService; 

    @GetMapping("/get-all")
    public ResponseEntity<List<RoomOccupation>> getRoomOccupation (@RequestParam("semester") String semester) {
        System.out.println("\n\n\n" + semester + "\n\n\n");
        try {
            return ResponseEntity.ok(roomService.getRoomOccupationsBySemester(semester));
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.badRequest().body(null);
        }
    }

}
