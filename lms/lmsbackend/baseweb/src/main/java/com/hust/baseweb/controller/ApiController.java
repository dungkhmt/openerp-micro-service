package com.hust.baseweb.controller;


import com.hust.baseweb.model.ModelPageUserSearchResponse;
import com.hust.baseweb.service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ApiController {

    private UserService userService;

//    private PersonService personService;
//
//    private ApplicationService applicationService;
//
//    private SecurityGroupService securityGroupService;
//
//    @GetMapping("/")
//    public ResponseEntity<Map> home(@CurrentSecurityContext(expression = "authentication.name") String name) {
//        Map<String, String> response = new HashMap<>();
//        HttpHeaders headers = new HttpHeaders();
//
//        response.put("user", name);
//        headers.set("Access-Control-Expose-Headers", "X-Auth-Token");
//
//        return ResponseEntity.ok().headers(headers).body(response);
//    }

    /**
     * If the user is not in the database, add them. If they are in the database, update their
     * information to synchronize with Keycloak
     *
     * @param token The JWT token that was passed in the request.
     */
    @GetMapping("/")
    public void syncUser(JwtAuthenticationToken token) {
        Jwt principal = (Jwt) token.getPrincipal();
        userService.synchronizeUser(
            principal.getClaim("preferred_username"),
            principal.getClaim("email"),
            principal.getClaim("given_name"),
            principal.getClaim("family_name"));
    }

//    /**
//     * It checks if the user has the permission to access the application
//     *
//     * @param principal     This is the user who is currently logged in.
//     * @param applicationId The application id of the application you want to check the user's
//     *                      permission.
//     * @return A map with the key "status" and the value "SUCCESS" and a key "result" with the value
//     * "INCLUDED" or "NOT_INCLUDED"
//     */
//    @Deprecated
//    @GetMapping("/check-authority")
//    public ResponseEntity<?> checkAuthorities(Principal principal, @RequestParam String applicationId) {
//
//        Map<String, String> response = null;
//        UserLogin userLogin = userService.findById(principal.getName());
//        Application application = applicationService.getById(applicationId);
//        if (application == null) {
//
//            response = new HashMap<>();
//            response.put("status", "SUCESSS");
//            response.put("result", "NOT_FOUND");
//
//            return ResponseEntity.ok().body(response);
//        }
//        List<SecurityPermission> permissionList = new ArrayList<>();
//        for (SecurityGroup securityGroup : userLogin.getRoles()) {
//            permissionList.addAll(securityGroup.getPermissions());
//        }
//        Set<String> permissionSet = permissionList.stream().map(permission -> permission.getPermissionId())
//                                                  .collect(Collectors.toSet());
//        if (permissionSet.contains(application.getPermission().getPermissionId())) {
//
//            response = new HashMap<>();
//            response.put("status", "SUCESSS");
//            response.put("result", "INCLUDED");
//        } else {
//            response = new HashMap<>();
//            response.put("status", "SUCESSS");
//            response.put("result", "NOT_INCLUDED");
//        }
//        return ResponseEntity.ok().body(response);
//
//    }
//
//    @GetMapping("/my-account")
//    public ResponseEntity<?> getAccount(Principal principal) {
//        UserLogin userLogin = userService.findById(principal.getName());
//        Party party = userLogin.getParty();
//        Person person = personService.findByPartyId(party.getPartyId());
//       Map<String, String> response = new HashMap<>();
//        response.put("name", person.getFullName());
//        response.put("partyId", person.getPartyId().toString());
//        response.put("user", principal.getName());
//        return ResponseEntity.ok().body(response);
//    }
//
//    @PostMapping("/change-password")
//    public ResponseEntity<?> changePassword(Principal principal, @RequestBody PasswordChangeModel passwordChangeModel) {
//        log.info("changePassword, userlogin = " +
//                 principal.getName() +
//                 " password = " +
//                 passwordChangeModel.getNewPassword());
//        UserLogin userLogin = userService.findById(principal.getName());
//
//        // TO BE CHECKED
//        //if (UserLogin.PASSWORD_ENCODER.matches(passwordChangeModel.getCurrentPassword(), userLogin.getPassword())) {
//        if (true) {
//            UserLogin user = userService.updatePassword(userLogin, passwordChangeModel.getNewPassword());
//            log.info("changePassword, userlogin = " +
//                     principal.getName() +
//                     " password = " +
//                     passwordChangeModel.getNewPassword() +
//                     " successfully");
//            return ResponseEntity.ok().body("OK");
//
//        } else {
//            log.info("changePassword, userlogin = " +
//                     principal.getName() +
//                     " password = " +
//                     passwordChangeModel.getNewPassword() +
//                     " ERROR current password  = " +
//                     passwordChangeModel.getCurrentPassword() +
//                     " DIFFERS " +
//                     userLogin.getPassword() +
//                     " not correct");
//
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password isn't correct");
//    }
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

//    @GetMapping("/roles")
//    public ResponseEntity<?> getRoles() {
//        return ResponseEntity.ok().body(securityGroupService.getRoles());
//    }
//
//    @GetMapping("screen-security")
//    public ResponseEntity<?> getScrSecurInfo(Principal principal) {
//        return ResponseEntity.ok().body(applicationService.getScrSecurInfo(principal.getName()));
//    }

    /**
     * > This function is used to search for users by their name or email
     *
     * @param pageable This is a parameter that is used to paginate the results.
     * @param keyword  the keyword to search for
     * @return ResponseEntity.status(200).body(res);
     */
    @GetMapping("/search-user")
    public ResponseEntity<?> searchUser(
        Pageable pageable,
        @Param("keyword") String keyword
    ) {
        if (keyword == null) {
            keyword = "";
        }
        ModelPageUserSearchResponse res = userService.searchUser(pageable, keyword);

        return ResponseEntity.status(200).body(res);
    }

    /*Jedis jedis = new Jedis("localhost");
        Map<String, String> res = jedis.hgetAll("spring:session:sessions:154894ef-efe4-4acf-b479-bce0275694fd");
        return ResponseEntity.ok().body(res.get("sessionAttr:SPRING_SECURITY_CONTEXT"));*/

}

