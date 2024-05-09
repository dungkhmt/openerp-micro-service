package openerp.openerpresourceserver.generaltimetabling.controller.occupation;

import java.util.List;

import openerp.openerpresourceserver.generaltimetabling.helper.LearningWeekExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.dto.RoomOccupationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/export")
    public ResponseEntity exportExcel (@RequestParam("semester") String semester, @RequestParam("week") int week) {
        String filename = "Room_Occupation.xlsx";
        InputStreamResource file = new InputStreamResource(roomOccupationService.exportExcel(semester, week));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

}
