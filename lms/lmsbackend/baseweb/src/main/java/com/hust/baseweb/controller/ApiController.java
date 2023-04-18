package com.hust.baseweb.controller;


import com.hust.baseweb.entity.*;
import com.hust.baseweb.model.ModelPageUserSearchResponse;
import com.hust.baseweb.model.PasswordChangeModel;
import com.hust.baseweb.service.ApplicationService;
import com.hust.baseweb.service.PersonService;
import com.hust.baseweb.service.SecurityGroupService;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.lettuce.core.dynamic.annotation.Param;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ApiController {

    private UserService userService;

    private PersonService personService;

    private ApplicationService applicationService;

    private SecurityGroupService securityGroupService;

    @GetMapping("/")
    public ResponseEntity<Map> home(@CurrentSecurityContext(expression = "authentication.name") String name) {
        Map<String, String> response = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();

        response.put("user", name);
        headers.set("Access-Control-Expose-Headers", "X-Auth-Token");

        return ResponseEntity.ok().headers(headers).body(response);
    }


    @GetMapping("/check-authority")
    public ResponseEntity<?> checkAuthorities(Principal principal, @RequestParam String applicationId) {

        Map<String, String> response = null;
        UserLogin userLogin = userService.findById(principal.getName());
        Application application = applicationService.getById(applicationId);
        if (application == null) {

            response = new HashMap<>();
            response.put("status", "SUCESSS");
            response.put("result", "NOT_FOUND");

            return ResponseEntity.ok().body(response);
        }
        List<SecurityPermission> permissionList = new ArrayList<>();
        for (SecurityGroup securityGroup : userLogin.getRoles()) {
            permissionList.addAll(securityGroup.getPermissions());
        }
        Set<String> permissionSet = permissionList.stream().map(permission -> permission.getPermissionId())
                                                  .collect(Collectors.toSet());
        if (permissionSet.contains(application.getPermission().getPermissionId())) {

            response = new HashMap<>();
            response.put("status", "SUCESSS");
            response.put("result", "INCLUDED");
        } else {
            response = new HashMap<>();
            response.put("status", "SUCESSS");
            response.put("result", "NOT_INCLUDED");
        }
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/my-account")
    public ResponseEntity<?> getAccount(Principal principal) {
        UserLogin userLogin = userService.findById(principal.getName());
        Party party = userLogin.getParty();
        Person person = personService.findByPartyId(party.getPartyId());
        Map<String, String> response = new HashMap<>();
        response.put("name", person.getFullName());
        response.put("partyId", person.getPartyId().toString());
        response.put("user", principal.getName());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody PasswordChangeModel passwordChangeModel) {
        log.info("changePassword, userlogin = " +
                 principal.getName() +
                 " password = " +
                 passwordChangeModel.getNewPassword());
        UserLogin userLogin = userService.findById(principal.getName());

        // TO BE CHECKED
        //if (UserLogin.PASSWORD_ENCODER.matches(passwordChangeModel.getCurrentPassword(), userLogin.getPassword())) {
        if (true) {
            UserLogin user = userService.updatePassword(userLogin, passwordChangeModel.getNewPassword());
            log.info("changePassword, userlogin = " +
                     principal.getName() +
                     " password = " +
                     passwordChangeModel.getNewPassword() +
                     " successfully");
            return ResponseEntity.ok().body("OK");

        } else {
            log.info("changePassword, userlogin = " +
                     principal.getName() +
                     " password = " +
                     passwordChangeModel.getNewPassword() +
                     " ERROR current password  = " +
                     passwordChangeModel.getCurrentPassword() +
                     " DIFFERS " +
                     userLogin.getPassword() +
                     " not correct");

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password isn't correct");
    }
    /*
    @GetMapping("/logout") USE default built by SPRING
    public ResponseEntity<String> logout(Principal principal) {
    	return ResponseEntity.ok().body("");
    }
    */
    
    /*
    @PostMapping("/post-location")
    public ResponseEntity postLocation(Principal principal, @RequestBody PostLocationInputModel input){
    	
    	UserLogin userLogin=userService.findById(principal.getName());
    	TrackLocations tl = trackLocationsService.save(input, userLogin.getParty());
        return ResponseEntity.ok().body(tl.getTrackLocationId());
    }
    @PostMapping("/get-location")
    public ResponseEntity getLocation(Principal principal, @RequestBody GetLocationInputModel input){
    	Point p = trackLocationsService.getLocation(input.getPartyId());
    	if(p == null) return ResponseEntity.ok().body(null);
    	return ResponseEntity.ok().body(p);
    }
    @GetMapping("/get-track-locations")
    public ResponseEntity<List> getTrackLocations(Principal principal){
    	System.out.println("getTrackLocations");
    	UserLogin userLogin=userService.findById(principal.getName());
    	List<TrackLocations> lst = trackLocationsService.getListLocations();
    	List<TrackLocationsOutputModel> ret_lst = lst.stream().map(e -> new TrackLocationsOutputModel(e)).collect(Collectors.toList());
    	
    	return ResponseEntity.ok().body(ret_lst);
    }
    */

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok().body(securityGroupService.getRoles());
    }

    @GetMapping("screen-security")
    public ResponseEntity<?> getScrSecurInfo(Principal principal) {
        return ResponseEntity.ok().body(applicationService.getScrSecurInfo(principal.getName()));
    }

    @GetMapping("/search-user")
    public ResponseEntity<?> searchUser(Pageable pageable,
                                        @Param("keyword") String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        ModelPageUserSearchResponse res = userService.searchUser(pageable, keyword);

        return ResponseEntity.status(200).body(res);
    }

    /*Jedis jedis = new Jedis("localhost");
        Map<String, String> res = jedis.hgetAll("spring:session:sessions:154894ef-efe4-4acf-b479-bce0275694fd");
        return ResponseEntity.ok().body(res.get("sessionAttr:SPRING_SECURITY_CONTEXT"));*/

    @GetMapping("/ping")
    public ResponseEntity<?> ping(@CurrentSecurityContext(expression = "authentication.name") String name) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("user", name);
        return ResponseEntity.ok().body(responseBody);
    }
}

