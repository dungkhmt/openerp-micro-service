package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.model.dto.request.GroupDto;
import openerp.openerpresourceserver.model.entity.Group;
import openerp.openerpresourceserver.model.entity.Semester;
import openerp.openerpresourceserver.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<Group> createNewGroup(@Valid @RequestBody GroupDto groupDto) {
        try {
            Group group = groupService.create(groupDto);
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Group>> getAllGroup() {
        try {
            List<Group> groupList = groupService.getGroup();
            if (groupList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(groupList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
