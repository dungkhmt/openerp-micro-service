package com.hust.baseweb.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.education.exception.SimpleResponse;
import com.hust.baseweb.applications.programmingcontest.entity.UserRegistrationContestEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelAddUserToContest;
import com.hust.baseweb.applications.programmingcontest.model.ModelUploadExcelParticipantToContestInput;
import com.hust.baseweb.entity.RegisteredAffiliation;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.*;
import com.hust.baseweb.service.RegisteredAffiliationService;
import com.hust.baseweb.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
@RestController
@CrossOrigin
@Log4j2
public class UserRegisterController {

    private UserService userService;

    @Autowired
    private RegisteredAffiliationService registeredAffiliationService;

    public UserRegisterController(UserService userService) {
        this.userService = userService;
    }


    /*@PostMapping("/user/register")
    public ResponseEntity<UserRegister.OutputModel> registerUser(@RequestBody UserRegister.InputModel inputModel) {
        return ResponseEntity.ok(userService.registerUser(inputModel));
    }

    @GetMapping("/user/get-all-register-user")
    public ResponseEntity<List<UserRegister.OutputModel>> findAllRegisterUser() {
        return ResponseEntity.ok(userService.findAllRegisterUser());
    }

    @GetMapping("/user/approve-register/{userLoginId}")
    public ResponseEntity<Boolean> approveRegisterUser(@PathVariable String userLoginId) {
        return ResponseEntity.ok(userService.approveRegisterUser(userLoginId));
    }*/

    @GetMapping("/public/get-registered-affiliations")
    public ResponseEntity<?> getRegisteredAffiliations(){
        List<RegisteredAffiliation> affiliations = registeredAffiliationService.findAll();

        return ResponseEntity.ok().body(affiliations);

    }
    @PostMapping("/user/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterIM im) {
        SimpleResponse res = userService.register(im);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/public/user/resetpassword/{userLoginId}")
    public ResponseEntity<?> resetPassword(@PathVariable String userLoginId){
        log.info("resetPassword, userLoginId = " + userLoginId);
        SimpleResponse res = userService.resetPassword(userLoginId);
        return ResponseEntity.ok().body(res);
    }
    @GetMapping("/user/registration-list")
    public ResponseEntity<?> getAllRegists() {
        return ResponseEntity.ok().body(userService.getAllRegists());
    }

    @PostMapping("/user/approve-registration")
    public ResponseEntity<?> approve(@RequestBody ApproveRegistrationIM im) {
        SimpleResponse res = userService.approve(im);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/user/approve-registration-send-email-for-activation")
    public ResponseEntity<?> approveRegistrationSendEmailForAccountActivation(@RequestBody ApproveRegistrationIM im){
        SimpleResponse res = userService.approveCreateAccountActivationSendEmail(im);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/public/activate-account/{activattionId}")
    public ResponseEntity<?> activateAccount(@PathVariable UUID activattionId){
        log.info("activateAccount, activationId = " + activattionId);
        SimpleResponse res = userService.activateAccount(activattionId);
        return ResponseEntity.ok().body("OK");
    }


    @PostMapping("/user/disable-registration")
    public ResponseEntity<?> disableUserRegistration(Principal principal, @RequestBody DisableUserRegistrationIM input){
        SimpleResponse res = userService.disableUserRegistration(input);
        return ResponseEntity.status(res.getStatus()).body(res);
    }
    @GetMapping("/get-user-detail/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable String userId){
        log.info("getUserDetail userId = " + userId);
        PersonModel p = userService.findPersonByUserLoginId(userId);
        log.info("getUserDetail, found personModel {}",p);
        return ResponseEntity.ok().body(p);
    }

    @PostMapping("/create-userlogin-list-from-excel")
    public ResponseEntity<?> createUserLoginListFromExel(Principal principal,
                                                             @RequestParam("inputJson") String inputJson,
                                                             @RequestParam("file") MultipartFile file) {
        Gson gson = new Gson();
        ModelInputCreateUserLoginList model = gson.fromJson(
            inputJson,ModelInputCreateUserLoginList.class);

        List<String> uploadedUsers = new ArrayList();
        //String contestId = modelUpload.getContestId();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            //System.out.println("uploadExcelStudentListOfQuizTest, lastRowNum = " + lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(0);

                String userId = c.getStringCellValue();
                UserLogin u = userService.findById(userId);
                if(u != null){
                    log.info("createUserLoginListFromExel, user " + userId + " ALREADY EXISTS");
                    continue;
                }
                c = row.getCell(1);
                String fullname = c.getStringCellValue();
                String[] s = fullname.split(" ");
                String lastName = "";
                String middleName = "";
                String firstName = "";
                if(s != null && s.length > 0){
                    firstName = fullname;
                }

                c = row.getCell(2);
                String email = c.getStringCellValue();

                RegisterIM im = new RegisterIM();
                im.setUserLoginId(userId);
                im.setPassword(userId);
                im.setEmail(email);
                im.setFirstName(firstName);
                im.setLastName(lastName);
                im.setMiddleName(middleName);
                im.setAffiliations(model.getAffiliations());
                im.setRoles(model.getRoles());

                SimpleResponse res1 = userService.register(im);

                ApproveRegistrationIM im1 = new ApproveRegistrationIM();
                im1.setUserLoginId(userId);
                im1.setRoles(model.getRoles());
                SimpleResponse res2 = userService.approveCreateAccountActivationSendEmail(im1);

                uploadedUsers.add(userId);

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(uploadedUsers);
    }

}
