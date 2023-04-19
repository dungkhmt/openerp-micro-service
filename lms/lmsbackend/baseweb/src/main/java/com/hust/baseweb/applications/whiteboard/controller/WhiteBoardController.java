package com.hust.baseweb.applications.whiteboard.controller;

import com.hust.baseweb.applications.whiteboard.model.*;
import com.hust.baseweb.applications.whiteboard.service.WhiteboardServiceImpl;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WhiteBoardController {
    private UserService userService;
    private WhiteboardServiceImpl whiteboardService;

    @PostMapping("/whiteboards")
    public void createWhiteboard(
        Principal principal,
        @RequestBody CreateWhiteboardModel input){

        UserLogin u = userService.findById(principal.getName());
        whiteboardService.createWhiteboard(u.getUserLoginId(), input.getWhiteboardId(), input.getWhiteboardName(), input.getClassSessionId());
    }

    @GetMapping("/whiteboards/{sessionId}")
    public ResponseEntity<List<GetListWhiteboardModel>> getWhiteboards(
        @PathVariable UUID sessionId){

        List<GetListWhiteboardModel> getListWhiteboardModels = whiteboardService.getWhiteboards(sessionId);

        return ResponseEntity.ok().body(getListWhiteboardModels);
    }

    @PostMapping("/whiteboards/save")
    public void saveWhiteboardsData(
        @RequestBody SaveWhiteboardDataModel input,
        Principal principal){

        UserLogin u = userService.findById(principal.getName());
        whiteboardService.saveWhiteboardData(input, u.getUserLoginId());
    }

    @GetMapping("/whiteboards/detail/{whiteboardId}")
    public ResponseEntity<WhiteboardDetailModel> getWhiteboardDetail(
        @PathVariable String whiteboardId){

        WhiteboardDetailModel whiteboardDetail = whiteboardService.getWhiteboardDetail(whiteboardId);
        return ResponseEntity.ok().body(whiteboardDetail);
    }

    @PutMapping("/whiteboards/user/{whiteboardId}")
    public ResponseEntity<AddUserToWhiteboardResultModel> addUserToWhiteboard(
        @PathVariable String whiteboardId, @RequestBody AddUserToWhiteboardModel input, Principal principal){

        UserLogin userLogin = userService.findById(principal.getName());
        AddUserToWhiteboardResultModel addUserToWhiteboardResultModel = whiteboardService.addUserToWhiteboard(whiteboardId, userLogin, input);

        return ResponseEntity.ok().body(addUserToWhiteboardResultModel);
    }

    @GetMapping("/whiteboards/user/{whiteboardId}")
    public void getUserWhiteboard(
        @PathVariable String whiteboardId, Principal principal){

        UserLogin userLogin = userService.findById(principal.getName());
        whiteboardService.getUserWhiteboard(whiteboardId, userLogin);
    }

    @PostMapping("/whiteboards/user/{whiteboardId}")
    public ResponseEntity<ChangeRoleStatusModel> changeRoleStatusUserWhiteboard(
        @PathVariable String whiteboardId, @RequestBody ChangeRoleStatusModel input){
        ChangeRoleStatusModel changeRoleStatusModel = whiteboardService.changeRoleStatusUserWhiteboard(whiteboardId, input);

        return ResponseEntity.ok().body(changeRoleStatusModel);
    }

    @GetMapping("/whiteboards/user/{whiteboardId}/list-pending")
    public ResponseEntity<ListDrawRequestPendingModel> getListDrawRequestPending(
        @PathVariable String whiteboardId, Principal principal){

        UserLogin userLogin = userService.findById(principal.getName());
        ListDrawRequestPendingModel listDrawRequestPendingModel = whiteboardService.getListDrawRequestPending(whiteboardId, userLogin);

        return ResponseEntity.ok().body(listDrawRequestPendingModel);
    }

    @GetMapping("/whiteboards/user/{whiteboardId}/list-users")
    public ResponseEntity<List<UsersInWhiteboardModel>> getListUsersInWhiteboard(
        @PathVariable String whiteboardId){

        List<UsersInWhiteboardModel> listUsersInWhiteboard = whiteboardService.getListUsersInWhiteboard(whiteboardId);

        return ResponseEntity.ok().body(listUsersInWhiteboard);
    }

    @DeleteMapping("/whiteboards")
    public ResponseEntity<DeleteWhiteboardResultModel> deleteWhiteboard(
        Principal principal,
        @RequestBody DeleteWhiteboardModel input){

        UserLogin u = userService.findById(principal.getName());
        DeleteWhiteboardResultModel result = whiteboardService.deleteWhiteboard(u.getUserLoginId(), input);

        return ResponseEntity.ok().body(result);
    }
}
