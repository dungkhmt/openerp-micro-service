package openerp.openerpresourceserver.generaltimetabling.controller.occupation;

import java.util.List;

import openerp.openerpresourceserver.generaltimetabling.model.GetEmptyRoomsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.service.RoomOccupationService;


@RestController
@RequestMapping("/room-occupation")
public class RoomOccupationController {

    @Autowired
    private RoomOccupationService roomOccupationService;

    @GetMapping("/get-all")
    public ResponseEntity<List<RoomOccupation>> getRoomOccupation (@RequestParam("semester") String semester) {
        try {
            return ResponseEntity.ok(roomOccupationService.getRoomOccupationsBySemester(semester));
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/")
    public ResponseEntity<List<RoomOccupation>> requestGetRoomOccupationsBySemesterAndWeekIndex(@RequestParam("semester")String semester, @RequestParam("weekIndex") int weekIndex) {
        return ResponseEntity.ok(roomOccupationService.getRoomOccupationsBySemesterAndWeekIndex(semester, weekIndex));
    }





    @PostMapping("/empty-room")
    public ResponseEntity requestGetEmptyRooms(
            @RequestParam("semester") String semester,
            @RequestBody GetEmptyRoomsRequest request) {
        return ResponseEntity.ok(roomOccupationService.getRoomsNotOccupiedBySemesterAndWeekDayCrewStartAndEndSLot(
                semester,
                request.getCrew(),
                request.getWeek(),
                request.getWeekDay(),
                request.getStartTime(),
                request.getEndTime()
        ));
    }
}
