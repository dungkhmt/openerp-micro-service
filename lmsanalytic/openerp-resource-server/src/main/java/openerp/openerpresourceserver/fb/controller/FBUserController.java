package openerp.openerpresourceserver.fb.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.fb.entity.FbGroup;
import openerp.openerpresourceserver.fb.entity.FbUser;
import openerp.openerpresourceserver.fb.entity.FbUserGroup;
import openerp.openerpresourceserver.fb.model.ModelInputImportSyncFbGroups;
import openerp.openerpresourceserver.fb.model.ModelInputImportSyncFbUsers;
import openerp.openerpresourceserver.fb.model.ModelResponseUser;
import openerp.openerpresourceserver.fb.repo.FbGroupRepo;
import openerp.openerpresourceserver.fb.repo.FbUserGroupRepo;
import openerp.openerpresourceserver.fb.repo.FbUserRepo;
import openerp.openerpresourceserver.fb.service.FbUserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class FBUserController {
    private FbUserService fbUserService;
    private FbUserRepo fbUserRepo;
    private FbUserGroupRepo fbUserGroupRepo;
    private FbGroupRepo fbGroupRepo;

    @Secured("ROLE_ADMIN")
    @GetMapping("/get-all-fb-users")
    public ResponseEntity<?> getAllUsers(Principal principal){
        List<ModelResponseUser> res = fbUserService.getAllUsers();
        return ResponseEntity.ok().body(res);
    }
    @Secured("ROLE_ADMIN")
    @PostMapping("/import-sync-fb-users")
    public ResponseEntity<?> importSyncFbUsers(
            Principal principal,
            @RequestParam("inputJson") String inputJson,
            @RequestParam("file") MultipartFile file
    ) {
        /*
        Gson gson = new Gson();
        ModelInputImportSyncFbUsers modelUpload = gson.fromJson(
                inputJson,
                ModelInputImportSyncFbUsers.class);
        String batchId = modelUpload.getBatchId();
        log.info("importSyncFbUsers, batchId = " + batchId);
        List<FbUser> res = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            log.info("importSyncFbUsers, lastRowNum = " + lastRowNum);

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                int columns = row.getLastCellNum();
                log.info("row " + i + " has columns = " + columns);
                String userId = "";
                String groupId = "";
                String name = "";
                String link = "";
                int nbMembers = 0;
                if(columns > 1) {
                    Cell c = row.getCell(1);
                    groupId = c.getStringCellValue();
                    log.info("groupId = " + c.getStringCellValue());
                }
                if(columns > 2) {
                    Cell c = row.getCell(2);
                    userId = c.getStringCellValue();
                    log.info("userId = " + c.getStringCellValue());
                }
                if(columns > 3) {
                    Cell c = row.getCell(3);
                    if(c!=null) {
                        if (c.getCellType()!=null && c.getCellType().equals(CellType.NUMERIC)) {
                            name = c.getNumericCellValue() + "";
                        } else {
                            name = c.getStringCellValue();
                        }
                    }
                    log.info("name = " + name);
                }
                if(columns > 6) {
                    Cell c = row.getCell(6);
                    if(c!=null) {
                        if (c.getCellType()!=null && c.getCellType().equals(CellType.NUMERIC)) {
                            link = c.getNumericCellValue() + "";
                        } else {
                            link = c.getStringCellValue();
                        }
                    }
                    log.info("link = " +link);
                }

                FbUser u = fbUserRepo.findById(groupId).orElse(null);
                if(u != null){
                    // update with new info
                    u.setLink(link);
                    u.setName(name);
                    u.setGroupId(groupId);
                    u.setLastUpdated(new Date());
                }else {
                    u = new FbUser();
                    u.setId(userId);
                    u.setLink(link);
                    u.setName(name);
                    u.setCreateStamp(new Date());
                }
                u = fbUserRepo.save(u);

                FbUserGroup ug = fbUserGroupRepo.findByUserIdAndGroupId(userId, groupId);
                if(ug == null){
                    FbGroup g = fbGroupRepo.findById(groupId).orElse(null);
                    if(g != null) {
                        Date d = new Date();
                        ug = new FbUserGroup(userId, groupId, d, d);
                        ug = fbUserGroupRepo.save(ug);
                    }
                }else{
                    // do nothing as group not exist
                }
                res.add(u);
            }
            */
        try {
            List<FbUser> res = fbUserService.synchronizeUsers(inputJson, file);
            return ResponseEntity.ok().body(res);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok().body("null");
        }
        //}catch (Exception e){
        //    e.printStackTrace();
        //}
        //return ResponseEntity.ok().body(res);

    }
}
