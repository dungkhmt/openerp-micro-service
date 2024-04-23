package com.hust.baseweb.applications.examclassandaccount.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.examclassandaccount.entity.ExamClass;
import com.hust.baseweb.applications.examclassandaccount.entity.ExamClassUserloginMap;
import com.hust.baseweb.applications.examclassandaccount.entity.RandomGeneratedUserLogin;
import com.hust.baseweb.applications.examclassandaccount.model.*;
import com.hust.baseweb.applications.examclassandaccount.repo.RandomGeneratedUserLoginRepo;
import com.hust.baseweb.applications.examclassandaccount.service.ExamClassService;
import com.hust.baseweb.applications.examclassandaccount.service.ExamClassUserloginMapService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin
public class ExamClassAccountController {

    @Autowired
    private ExamClassService examClassService;
    @Autowired
    private ExamClassUserloginMapService examClassUserloginMapService;
    @Autowired
    RandomGeneratedUserLoginRepo randomGeneratedUserLoginRepo;

    @Secured("ROLE_ADMIN")
    @GetMapping("/get-all-exam-class")
    public ResponseEntity<?> getAllExamClass(Principal principal) {
        List<ExamClass> res = examClassService.getAllExamClass();
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/create-exam-class")
    public ResponseEntity<?> createExamClass(Principal principal, @RequestBody ModelCreateExamClass m) {
        ExamClass ec = examClassService.createExamClass(principal.getName(), m);
        return ResponseEntity.ok().body(ec);
    }

    @GetMapping("/get-exam-class-userlogin-map/{examClassId}")
    public ResponseEntity<?> getExamClassUserLoginMap(Principal principal, @PathVariable UUID examClassId) {
        List<ExamClassUserloginMap> res = examClassUserloginMapService.getExamClassUserloginMap(examClassId);
        return ResponseEntity.ok().body(res);
    }
    @Secured("ROLE_ADMIN")
    @PostMapping("/create-a-generated-userlogin")
    public ResponseEntity<?> createAGeneratedUserLogin(Principal principal, @RequestBody ModelCreateGeneratedUserLogin m){
        RandomGeneratedUserLogin u = new RandomGeneratedUserLogin(m.getUserLoginId(),m.getPassword(),RandomGeneratedUserLogin.STATUS_ACTIVE);
        u = randomGeneratedUserLoginRepo.save(u);
        log.info("createAGeneratedUserLogin, created generated user " + u.getUserLoginId() + "," + u.getPassword());
        return ResponseEntity.ok().body(u);
    }
    @Secured("ROLE_ADMIN")
    @PostMapping("/create-exam-accounts-map")
    public ResponseEntity<?> createExamAccountsMap(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelCreateExamAccountMap modelUpload = gson.fromJson(
            inputJson,
            ModelCreateExamAccountMap.class);
        UUID examClassId = modelUpload.getExamClassId();
        log.info("createExamAccountsMap, testId = " + examClassId);
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            System.out.println("createExamAccountsMap, lastRowNum = " + lastRowNum);
            List<UserLoginModel> users = new ArrayList<UserLoginModel>();
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(0);
                String userLoginId = c.getStringCellValue();
                c = row.getCell(1);
                String studentCode =  c.getStringCellValue();
                c = row.getCell(2);
                String fullName =  c.getStringCellValue();
                UserLoginModel u = new UserLoginModel(userLoginId,studentCode,fullName);
                users.add(u);
                System.out.println("userId = " + c.getStringCellValue());
            }
            List<ExamClassUserloginMap> res = examClassUserloginMapService.createExamClassAccount(examClassId,users);
            return ResponseEntity.ok().body(res);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("null");
    }
    @Secured("ROLE_ADMIN")
    @PostMapping("/update-status-exam-class")
    public ResponseEntity<?> updateStatusExamClass(Principal principal, @RequestBody ModelUpdateStatusExamClass m){
        boolean res = examClassService.updateStatusExamClass(m.getExamClassId(),m.getStatus());
        return ResponseEntity.ok().body(res);
    }
    @Secured("ROLE_ADMIN")
    @GetMapping("/get-exam-class-detail/{examClassId}")
    public ResponseEntity<?> getExamClassDetail(Principal principal, @PathVariable UUID examClassId){
        ModelRepsonseExamClassDetail res = examClassService.getExamClassDetail(examClassId);
        return ResponseEntity.ok().body(res);
    }
}
