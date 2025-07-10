package openerp.openerpresourceserver.controller;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.model.DashBoard;
import openerp.openerpresourceserver.recommend.model.Course;
import openerp.openerpresourceserver.service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/dash-board")

public class DashboardController {
    private DashBoardService dashBoardService;

    @GetMapping()
    public ResponseEntity<?> getDashboard() {
        DashBoard dashBoard =  dashBoardService.getDashBoard();
        return ResponseEntity.ok().body(dashBoard);
    }
}
