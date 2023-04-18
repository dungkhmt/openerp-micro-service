package com.hust.baseweb.rest.user;

import com.hust.baseweb.applications.education.exception.SimpleResponse;
import com.hust.baseweb.entity.Party;
import com.hust.baseweb.entity.SecurityGroup;
import com.hust.baseweb.entity.SecurityPermission;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.ModelAssignGroupAllUsersInput;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.model.PersonUpdateModel;
import com.hust.baseweb.model.UpdatePasswordModel;
import com.hust.baseweb.entity.UserRegister;
import com.hust.baseweb.repo.UserRegisterRepo;
import com.hust.baseweb.model.dto.DPersonDetailModel;
import com.hust.baseweb.service.PartyService;
import com.hust.baseweb.service.SecurityGroupService;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserController
 */
// @RepositoryRestController
// @ExposesResourceFor(DPerson.class)
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class UserController {

    public static final String EDIT_REL = "edit";
    public static final String DELETE_REL = "delete";
    private UserService userService;
    private PartyService partyService;
    private SecurityGroupService securityGroupService;
    private UserRegisterRepo userRegisterRepo;

    @GetMapping("/users/{userLoginId}/detail")
    public ResponseEntity<?> getUserDetailByLoginId(@PathVariable String userLoginId) {
        return ResponseEntity.ok(userService.findPersonByUserLoginId(userLoginId));
    }

    @PostMapping(path = "/user")
    public ResponseEntity<?> save(
        @RequestBody PersonModel personModel,
        Principal principal
    ) {
        // Resources<String> resources = new Resources<String>(producers);\\
        Party party;
        try {
            party = userService.createAndSaveUserLogin(personModel);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            party.getPartyId());

    }

    @PutMapping(path = "/user/{partyId}")
    public ResponseEntity<?> update(
        @RequestBody PersonUpdateModel personUpdateModel,
        Principal principal, @PathVariable String partyId
    ) {
        Party party;
        party = userService.update(personUpdateModel, UUID.fromString(partyId));


        return ResponseEntity.status(HttpStatus.OK).body(party.getPartyId());
    }

    @GetMapping(path = "/users")
    public ResponseEntity<?> getUsers(
        Pageable page,
        @RequestParam(name = "search", required = false) String searchString,
        @RequestParam(name = "filter", required = false) String filterString
    ) {
        log.info("::getUsers, searchString = " + searchString);


        return ResponseEntity.ok().body(
            userService.findPersonByFullName(page, searchString));
    }

    @GetMapping(path = "/users/search")
    public ResponseEntity<?> getCustomSearchedUsers(
        Pageable page,
        String userLoginId,
        Principal principal
    ) {
        log.info("::getCustomSearchedUsers, userLoginId = " + userLoginId);

        return ResponseEntity.ok().body(
            userService.findUsersByUserLoginId(page, userLoginId)
        );
    }

    @GetMapping(
        value = "users",
        params = {"securityGroups", "search", "page", "size"}
    )
    public ResponseEntity<?> getUserLoginWithPersonModelBySecurityGroup(
        @RequestParam("securityGroups") Collection<String> securityGroupIds,
        @RequestParam String search, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
            userService.findAllUserLoginWithPersonModelBySecurityGroupId(securityGroupIds, search, pageable)
        );
    }

    @GetMapping("/user-login-ids")
    public ResponseEntity<?> getEnabledUserLoginIds(
        @RequestParam(name = "search", required = false, defaultValue = "") String partOfLoginId,
        @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit
    ) {
        return ResponseEntity.ok(userService.getAllEnabledLoginIdsContains(partOfLoginId, limit));
    }

    @GetMapping(path = "/get-security-groups")
    public ResponseEntity<?> getSecurityGroups(Principal principal) {
        List<SecurityGroup> securityGroups = securityGroupService.findAll();
        return ResponseEntity.ok().body(securityGroups);
    }


    @GetMapping(path = "/users/{partyId}")
    public ResponseEntity<?> getUsersDetail(
        @PathVariable String partyId,
        Principal principal
    ) {
        DPerson p = userService.findByPartyId(partyId);
        DPersonDetailModel detailModel = new DPersonDetailModel(p);
        UserLogin userLogin = userService.findById(principal.getName());
        UserLogin u = userService.findById(p.getUserLogin().getUserLoginId());
        UserRegister ur = userRegisterRepo.findById(u.getUserLoginId()).orElse(null);
        log.info("users get Detail info " + u.getUserLoginId());

        if(ur != null){
            detailModel.setEmail(ur.getEmail());
            log.info("users get Detail info email found = " + ur.getEmail());
        }else{
            log.info("users get Detail info user register not found = ");
        }
        if(u.isEnabled()) {
            detailModel.setEnabled("Y");
            log.info("getUsersDetail, userLoginId " + u.getUserLoginId() + " is enabled");
        }else {
            detailModel.setEnabled("N");
            log.info("getUsersDetail, userLoginId " + u.getUserLoginId() + " is NOT enabled");
        }

        List<SecurityPermission> permissionList = new ArrayList<>();
        for (SecurityGroup sg : userLogin.getRoles()) {
            permissionList.addAll(sg.getPermissions());
        }
        List<SecurityPermission> lf = permissionList
            .stream()
            .filter(pe -> "USER_CREATE".equals(pe.getPermissionId()))
            .collect(Collectors.toList());
        if (lf.size() > 0) {
            detailModel.add(new Link("/user", EDIT_REL));
            detailModel.add(new Link("/user", DELETE_REL));
        }
        return ResponseEntity.ok().body(detailModel);
    }

    @DeleteMapping(path = "/users/{partyId}")
    public ResponseEntity<?> delete(
        @PathVariable String partyId,
        Principal principal
    ) {
        partyService.disableParty(partyId);
        return ResponseEntity.ok("");
    }

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

    @GetMapping("/get-all-user-login-ids")
    public ResponseEntity<List<String>> getAllUserLoginIds() {
        return ResponseEntity.ok(userService
                                     .getAllUserLogins()
                                     .stream()
                                     .map(UserLogin::getUserLoginId)
                                     .collect(Collectors.toList()));
    }

    @PostMapping("/user/updatepassword2")
    public ResponseEntity<?> update(Principal principal, @RequestBody UpdatePasswordModel input) {
        log.info("okController: ");
        UserLogin u = userService.updatePassword2(input.getUserLoginId(), input.getPassword());
        return ResponseEntity.ok().body(u);
    }

    @PostMapping("/user/updatepassword3/{partyId}")
    public ResponseEntity<?> update3(
        Principal principal,
        @PathVariable String partyId,
        @RequestBody UpdatePasswordModel input
    ) {
        DPerson p = userService.findByPartyId(partyId);
        DPersonDetailModel detailModel = new DPersonDetailModel(p);
        UserLogin u = userService.updatePassword2(detailModel.getUserLoginId(), input.getPassword());
        return ResponseEntity.ok().body(u);
    }

    @PostMapping("/user/assign-group-all-users")
    public ResponseEntity<?> assignGroup2AllUsers(Principal principal, @RequestBody ModelAssignGroupAllUsersInput I){
        SimpleResponse res = userService.assignGroup2AllUsers(I);
        return ResponseEntity.ok().body(res.getMessage());
    }
}
