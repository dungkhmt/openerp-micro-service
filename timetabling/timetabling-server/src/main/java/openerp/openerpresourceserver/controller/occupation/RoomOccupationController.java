package openerp.openerpresourceserver.controller.occupation;

import java.util.Arrays;
import java.util.List;

import openerp.openerpresourceserver.helper.LearningWeekExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
        try {
            return ResponseEntity.ok(roomService.getRoomOccupationsBySemester(semester));
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/export")
    public ResponseEntity exportExcel (@RequestParam("semester") String semester, @RequestParam("weeks") String weeks) {
        List<Integer> learningWeeks  = LearningWeekExtractor.extract(weeks);
        System.out.println(learningWeeks);
        String filename = "Room_Conflict.xlsx";
        InputStreamResource file = new InputStreamResource(roomService.exportExcel(semester, learningWeeks));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

}
