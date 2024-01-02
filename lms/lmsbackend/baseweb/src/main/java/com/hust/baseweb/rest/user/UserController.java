package com.hust.baseweb.rest.user;

import com.hust.baseweb.applications.programmingcontest.model.ModelSearchUserResult;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

// @RepositoryRestController
// @ExposesResourceFor(DPerson.class)
@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    //    public static final String EDIT_REL = "edit";
//
//    public static final String DELETE_REL = "delete";
    private UserService userService;

//    private PartyService partyService;
//
//    private SecurityGroupService securityGroupService;
//
//    private UserRegisterRepo userRegisterRepo;

    @GetMapping("/users")
    public ResponseEntity<?> searchUserBaseKeyword(
        Pageable pageable,
        @Param("keyword") String keyword
    ) {
        if (keyword == null) {
            keyword = "";
        }
        Page<ModelSearchUserResult> resp = userService.findUserByKeyword(pageable, keyword);
        return ResponseEntity.status(200).body(resp);
    }

    /**
     * > This function returns a user's detail by login id
     *
     * @param userLoginId The user login id of the user whose details are to be fetched.
     * @return A ResponseEntity object is being returned.
     */
    @GetMapping("/users/{userLoginId}/detail")
    public ResponseEntity<?> getUserDetailByLoginId(@PathVariable String userLoginId) {
        return ResponseEntity.ok(userService.findPersonByUserLoginId(userLoginId));
    }

//    @PostMapping(path = "/user")
//    public ResponseEntity<?> save(
//        @RequestBody PersonModel personModel,
//        Principal principal
//    ) {
//        // Resources<String> resources = new Resources<String>(producers);\\
//        Party party;
//        try {
//            party = userService.createAndSaveUserLogin(personModel);
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(
//                e.getMessage());
//        }
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(
//            party.getPartyId());
//
//    }
//
//    @PutMapping(path = "/user/{partyId}")
//    public ResponseEntity<?> update(
//        @RequestBody PersonUpdateModel personUpdateModel,
//        Principal principal, @PathVariable String partyId
//    ) {
//        Party party;
//        party = userService.update(personUpdateModel, UUID.fromString(partyId));
//
//
//        return ResponseEntity.status(HttpStatus.OK).body(party.getPartyId());
//    }

    /**
     * It returns a list of users, with pagination, and the ability to search by name
     *
     * @param page         This is a Pageable object that is used to paginate the results.
     * @param searchString The search string that the user entered in the search box.
     * @return A list of users
     */
    @Secured("ROLE_TEACHER")
    @GetMapping(path = "/statistics/users")
    public ResponseEntity<?> getUsers(
        Pageable page,
        @RequestParam(name = "search", required = false) String searchString
    ) {
        Page<ModelSearchUserResult> res = userService.findUserByKeyword(page, searchString);
        return ResponseEntity.ok().body(res);
    }

//    @GetMapping(path = "/users/search")
//    public ResponseEntity<?> getCustomSearchedUsers(
//        Pageable page,
//        String userLoginId,
//        Principal principal
//    ) {
//        log.info("::getCustomSearchedUsers, userLoginId = " + userLoginId);
//
//        return ResponseEntity.ok().body(
//            userService.findUsersByUserLoginId(page, userLoginId)
//        );
//    }

    /**
     * > This function returns a list of users with their person model, filtered by security group,
     * search term, page number and page size
     *
     * @param securityGroupIds A collection of security group IDs.
     * @param search           The search string to search for.
     * @param page             The page number
     * @param size             The number of items to return per page.
     * @return A list of users with their person model and security groups.
     */
    @GetMapping(
        value = "users",
        params = {"securityGroups", "search", "page", "size"}
    )
    public ResponseEntity<?> getUserLoginWithPersonModelBySecurityGroup(
        @RequestParam("securityGroups") Collection<String> securityGroupIds,
        @RequestParam String search, @RequestParam int page, @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
            userService.findAllUserLoginWithPersonModelBySecurityGroupId(securityGroupIds, search, pageable)
        );
    }

    /**
     * > This function returns a list of all enabled user login ids that contain the given string
     *
     * @param partOfLoginId The part of the login id that you want to search for.
     * @param limit         The maximum number of results to return.
     * @return A list of all enabled user login ids that contain the search string.
     */
    @GetMapping("/user-login-ids")
    public ResponseEntity<?> getEnabledUserLoginIds(
        @RequestParam(name = "search", required = false, defaultValue = "") String partOfLoginId,
        @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit
    ) {
        return ResponseEntity.ok(userService.getAllEnabledLoginIdsContains(partOfLoginId, limit));
    }

//    @GetMapping(path = "/get-security-groups")
//    public ResponseEntity<?> getSecurityGroups(Principal principal) {
//        List<SecurityGroup> securityGroups = securityGroupService.findAll();
//        return ResponseEntity.ok().body(securityGroups);
//    }
//
//
//    @GetMapping(path = "/users/{partyId}")
//    public ResponseEntity<?> getUsersDetail(
//        @PathVariable String partyId,
//        Principal principal
//    ) {
//        DPerson p = userService.findByPartyId(partyId);
//        DPersonDetailModel detailModel = new DPersonDetailModel(p);
//        UserLogin userLogin = userService.findById(principal.getName());
//        UserLogin u = userService.findById(p.getUserLogin().getUserLoginId());
//        UserRegister ur = userRegisterRepo.findById(u.getUserLoginId()).orElse(null);
//        log.info("users get Detail info " + u.getUserLoginId());
//
//        if(ur != null){
//            detailModel.setEmail(ur.getEmail());
//            log.info("users get Detail info email found = " + ur.getEmail());
//        }else{
//            log.info("users get Detail info user register not found = ");
//        }
//        if(u.isEnabled()) {
//            detailModel.setEnabled("Y");
//            log.info("getUsersDetail, userLoginId " + u.getUserLoginId() + " is enabled");
//        }else {
//            detailModel.setEnabled("N");
//            log.info("getUsersDetail, userLoginId " + u.getUserLoginId() + " is NOT enabled");
//        }
//
//        List<SecurityPermission> permissionList = new ArrayList<>();
//        for (SecurityGroup sg : userLogin.getRoles()) {
//            permissionList.addAll(sg.getPermissions());
//        }
//        List<SecurityPermission> lf = permissionList
//            .stream()
//            .filter(pe -> "USER_CREATE".equals(pe.getPermissionId()))
//            .collect(Collectors.toList());
//        if (lf.size() > 0) {
//            detailModel.add(new Link("/user", EDIT_REL));
//            detailModel.add(new Link("/user", DELETE_REL));
//        }
//        return ResponseEntity.ok().body(detailModel);
//    }
//
//    @DeleteMapping(path = "/users/{partyId}")
//    public ResponseEntity<?> delete(
//        @PathVariable String partyId,
//        Principal principal
//    ) {
//        partyService.disableParty(partyId);
//        return ResponseEntity.ok("");
//    }

	/*
	@GetMapping(path = "/users")
	public ResponseEntity<?> getUsers(Pageable page, @RequestParam(name = "filtering", required = false) String filterString) {
     SortAndFiltersInput sortAndFiltersInput = null; if (filterString != null) {
       String[] filterSpl = filterString.split(",");
       SearchCriteria[] searchCriterias = new SearchCriteria[filterSpl.length];
       for (int i = 0; i <filterSpl.length; i++) { String tmp = filterSpl[i];
       if (tmp != null) {
       Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?)-");//
       (\w+?)(:|<|>)(\w+?), Matcher matcher = pattern.matcher(tmp + "-"); while
       (matcher.find()) { LOG.info(matcher.group(0)); searchCriterias[i] = new
       SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)); } } }
       sortAndFiltersInput = new SortAndFiltersInput(searchCriterias, null);
       sortAndFiltersInput = CommonUtils.rebuildQueryDsl(DTOPerson.mapPair,
       sortAndFiltersInput); LOG.info(sortAndFiltersInput.toString()); }
       LOG.info("::getUsers, pages = " + page.toString()); Page<DPerson> pg =
       userService.findAllPerson(page, sortAndFiltersInput); List<DTOPerson> lst =
       new ArrayList<DTOPerson>(); List<DPerson> lPerson = pg.getContent(); lst =
       lPerson.stream().map(p -> new DTOPerson(p)).collect(Collectors.toList());
       Page<DTOPerson> dtoPerson = new PageImpl<DTOPerson>(lst, page,
       pg.getTotalElements()); return ResponseEntity.ok().body(dtoPerson);
       }
    */

//    @GetMapping("/get-all-user-login-ids")
//    public ResponseEntity<List<String>> getAllUserLoginIds() {
//        return ResponseEntity.ok(userService
//                                     .getAllUserLogins()
//                                     .stream()
//                                     .map(UserLogin::getUserLoginId)
//                                     .collect(Collectors.toList()));
//    }
//
//    @PostMapping("/user/updatepassword2")
//    public ResponseEntity<?> update(Principal principal, @RequestBody UpdatePasswordModel input) {
//        log.info("okController: ");
//        UserLogin u = userService.updatePassword2(input.getUserLoginId(), input.getPassword());
//        return ResponseEntity.ok().body(u);
//    }
//
//    @PostMapping("/user/updatepassword3/{partyId}")
//    public ResponseEntity<?> update3(
//        Principal principal,
//        @PathVariable String partyId,
//        @RequestBody UpdatePasswordModel input
//    ) {
//        DPerson p = userService.findByPartyId(partyId);
//        DPersonDetailModel detailModel = new DPersonDetailModel(p);
//        UserLogin u = userService.updatePassword2(detailModel.getUserLoginId(), input.getPassword());
//        return ResponseEntity.ok().body(u);
//    }
//
//    @PostMapping("/user/assign-group-all-users")
//    public ResponseEntity<?> assignGroup2AllUsers(Principal principal, @RequestBody ModelAssignGroupAllUsersInput I){
//        SimpleResponse res = userService.assignGroup2AllUsers(I);
//        return ResponseEntity.ok().body(res.getMessage());
//    }
}
