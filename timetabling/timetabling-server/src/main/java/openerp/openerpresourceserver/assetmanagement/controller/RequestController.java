package openerp.openerpresourceserver.assetmanagement.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.Request;
import openerp.openerpresourceserver.assetmanagement.service.RequestLogService;
import openerp.openerpresourceserver.assetmanagement.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/request")
public class RequestController {
    private RequestService requestService;
    private RequestLogService requestLogService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllRequests(Principal principal){
        List<Request> requests = requestService.getAllRequests();
        String userId = principal.getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requests);
    }

    @GetMapping("/id/{Id}")
    public ResponseEntity<?> getById(@PathVariable Integer Id){
        Request request = requestService.getById(Id);
        System.out.println("request123 " + request.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(request);
    }

    @PostMapping("/add-new")
    public ResponseEntity<?> createNewRequest(@RequestBody Request request, Principal principal){
        request.setUser_id(principal.getName());
        Request savedRequest = requestService.createNewRequest(request);
        requestLogService.createRequestLog(savedRequest.getId(), principal.getName(), "create");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedRequest);
    }

    @PutMapping("/edit/{Id}")
    public ResponseEntity<?> editRequest(@PathVariable Integer Id, @RequestBody Request request, Principal principal){
        Request savedRequest = requestService.editRequest(Id, request);
        requestLogService.createRequestLog(savedRequest.getId(), principal.getName(), "edit");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(savedRequest);
    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Integer Id, Principal principal){
        requestService.deleteRequest(Id);
        requestLogService.createRequestLog(Id, principal.getName(), "delete");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/approve/{Id}")
    public ResponseEntity<?> approveRequest(@PathVariable Integer Id, Principal principal){
        String approval_id = principal.getName();
        Request savedRequest = requestService.approveRequest(Id, approval_id);
        requestLogService.createRequestLog(savedRequest.getId(), principal.getName(), "approve");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(savedRequest);

    }

    @PutMapping("/reject/{Id}")
    public ResponseEntity<?> rejectRequest(@PathVariable Integer Id, Principal principal){
        String rejection_id = principal.getName();
        Request savedRequest = requestService.rejectRequest(Id, rejection_id);
        requestLogService.createRequestLog(savedRequest.getId(), principal.getName(), "reject");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(savedRequest);
    }

    @GetMapping("/get-by-creator")
    public ResponseEntity<?> getCreatorRequests(Principal principal){
        String user_id = principal.getName();
        List<Request> requests = requestService.getCreatorRequests(user_id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requests);
    }

    @GetMapping("/get-by-admin")
    public ResponseEntity<?> getAdminRequests(Principal principal){
        String user_id = principal.getName();
        List<Request> requests = requestService.getAdminRequests(user_id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requests);
    }

    @PutMapping("/payback/{Id}")
    public ResponseEntity<?> paybackRequests(@PathVariable Integer Id, Principal principal){
        String approval_id = principal.getName();
        Request savedRequest = requestService.paybackRequest(Id, approval_id);
        requestLogService.createRequestLog(savedRequest.getId(), principal.getName(), "done");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(savedRequest);
    }

    @GetMapping("/top-users")
    public ResponseEntity<?> getTopUsers(){
        List<String> users = requestService.getTopUsers();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @GetMapping("/get-by-user/{user_id}")
    public ResponseEntity<?> getByUser(@PathVariable String user_id){
        List<Request> requests = requestService.getCreatorRequests(user_id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(requests);
    }
}