package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Request;
import openerp.openerpresourceserver.service.RequestService;
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

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllRequests(Principal principal){
        System.out.println("pripri " + principal.getName());
        List<Request> requests = requestService.getAllRequests();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(requests);
    }

    @PostMapping("/add-new")
    public ResponseEntity<?> createNewRequest(@RequestBody Request request, Principal principal){
        request.setUser_id(principal.getName());
        Request savedRequest = requestService.createNewRequest(request);
        System.out.println(savedRequest.toString());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("");
    }

    @PutMapping("/edit/{Id}")
    public ResponseEntity<?> editRequest(@PathVariable Integer Id, @RequestBody Request request){
        Request savedRequest = requestService.editRequest(Id, request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(savedRequest);
    }

//    public ResponseEntity<?> approveRequest(@PathVariable Integer Id){
//
//    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Integer Id){
        requestService.deleteRequest(Id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
