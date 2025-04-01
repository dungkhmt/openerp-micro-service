package openerp.openerpresourceserver.generaltimetabling.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.generaltimetabling.exception.GroupNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.exception.GroupUsedException;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.CreateGroupRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDeleteRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.PriorityGroupUpdateDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGroupNameRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping("/create-priority")
    public ResponseEntity<String> createNewGroupPriority(@Valid @RequestBody GroupDto groupDto) {
        try {
            groupService.create(groupDto);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (GroupUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-group")
    public ResponseEntity<String> createNewGroup(@Valid @RequestBody CreateGroupRequest group) {
        try {
            groupService.createGroup(group.getGroupName());
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (GroupUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-by-group-id/{groupId}")
    public ResponseEntity<List<GroupDto>> getGroupByGroupId(@PathVariable Long groupId) {
        try {
            List<GroupDto> groupList = groupService.getGroupByGroupId(groupId);
            if (groupList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(groupList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-group")
    public ResponseEntity<List<Group>> getAllGroup() {
        try {
            List<Group> groupList = groupService.getAllGroup();
            if (groupList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(groupList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-group-name")
    public ResponseEntity<String> updateGroupName(@RequestBody UpdateGroupNameRequest updateGroupNameRequest) {
        try {
            groupService.updateGroupName(updateGroupNameRequest.getId(), updateGroupNameRequest.getGroupName());
            return ResponseEntity.ok("Cập nhật tên nhóm thành công!");
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getCustomMessage());
        } catch (GroupUsedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getCustomMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống khi cập nhật nhóm!");
        }
    }


    @PostMapping("/update")
    public ResponseEntity<String> updateGroup(@Valid @RequestBody PriorityGroupUpdateDto requestDto) {
        try {
            groupService.updateGroup(requestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (GroupUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePriorityGroup(@RequestBody GroupDeleteRequest deleteRequest) {
        try {
            groupService.deletePriorityGroup(deleteRequest.getId(), deleteRequest.getRoomId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (GroupUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-by-id")
    public ResponseEntity<String> deleteById(@RequestParam Long id) {
        try {
            groupService.deleteById(id);
            return ResponseEntity.ok("Xóa nhóm thành công với ID: " + id);
        } catch (GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getCustomMessage());
        } catch (GroupUsedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getCustomMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống khi xóa nhóm!");
        }
    }

}
