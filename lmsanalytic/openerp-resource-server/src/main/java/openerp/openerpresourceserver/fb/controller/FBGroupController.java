package openerp.openerpresourceserver.fb.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.fb.entity.FbGroup;
import openerp.openerpresourceserver.fb.model.ModelInputImportSyncFbGroups;
import openerp.openerpresourceserver.fb.model.ModelResponseFbGroup;
import openerp.openerpresourceserver.fb.repo.FbGroupRepo;
import openerp.openerpresourceserver.fb.service.FbGroupService;
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
public class FBGroupController {
    private FbGroupService fbGroupService;
    private FbGroupRepo fbGroupRepo;
    @Secured("ROLE_ADMIN")
    @GetMapping("/get-all-fb-groups")
    public ResponseEntity<?> getAllFbGroups(Principal principal){
        log.info("getAllFbGroups, userId = " + principal.getName());
        List<ModelResponseFbGroup> res = fbGroupService.getAllGroups();
        return ResponseEntity.ok().body(res);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/import-sync-fb-groups")
    public ResponseEntity<?> importSyncFbGroups(
            Principal principal,
            @RequestParam("inputJson") String inputJson,
            @RequestParam("file") MultipartFile file
    ) {
        log.info("importSyncFbGroups, start...");
        Gson gson = new Gson();
        ModelInputImportSyncFbGroups modelUpload = gson.fromJson(
                inputJson,
                ModelInputImportSyncFbGroups.class);
        String batchId = modelUpload.getBatchId();
        log.info("importSyncFbGroups, batchId = " + batchId);
        List<FbGroup> res = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            log.info("importSyncFbGroups, lastRowNum = " + lastRowNum);

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                int columns = row.getLastCellNum();
                log.info("row " + i + " has columns = " + columns);
                String groupId = "";
                String groupName = "";
                String groupType = "";
                String members = "";
                String link = "";
                int nbMembers = 0;
                if(columns > 2) {
                    Cell c = row.getCell(2);
                    groupId = c.getStringCellValue();
                    log.info("groupId = " + c.getStringCellValue());
                }
                if(columns > 3) {
                    Cell c = row.getCell(3);
                    if(c!=null) {
                        if (c.getCellType()!=null && c.getCellType().equals(CellType.NUMERIC)) {
                            groupName = c.getNumericCellValue() + "";
                        } else {
                            groupName = c.getStringCellValue();
                        }
                    }
                    log.info("groupName = " + groupName);
                }
                if(columns > 4) {
                    Cell c = row.getCell(4);
                    if(c!=null) {
                        if (c.getCellType()!=null && c.getCellType().equals(CellType.NUMERIC)) {
                            groupType = c.getNumericCellValue() + "";
                        } else {
                            groupType = c.getStringCellValue();
                        }
                    }
                    log.info("groupType = " + groupType);
                }
                if(columns > 6) {
                    Cell c = row.getCell(6);
                    if(c!=null) {
                        if (c.getCellType()!=null && c.getCellType().equals(CellType.NUMERIC)) {
                            members = c.getNumericCellValue() + "";
                        } else {
                            members = c.getStringCellValue();
                        }
                    }

                    if(members.contains("K")) {
                        String[] s = members.split("K");
                        double s0 = Double.valueOf(s[0]);
                        nbMembers = (int)s0*1000;
                    }else if( members.contains("M")) {
                        String[] s = members.split("M");
                        double s0 = Double.valueOf(s[0]);
                        nbMembers = (int)s0*1000000;

                    }else{
                        nbMembers = Integer.valueOf(members);
                    }
                    log.info("members = " + members + " nbMembers = " + nbMembers);
                }
                if(columns > 7) {
                    Cell c = row.getCell(7);
                    if(c!=null) {
                        if (c.getCellType()!=null && c.getCellType().equals(CellType.NUMERIC)) {
                            link = c.getNumericCellValue() + "";
                        } else {
                            link = c.getStringCellValue();
                        }
                    }
                    log.info("link = " + link);
                }


                FbGroup g = fbGroupRepo.findById(groupId).orElse(null);
                if(g != null){
                    // update with new info
                    g.setLink(link);
                    g.setGroupName(groupName);
                    g.setGroupType(groupType);
                    g.setNumberMembers(nbMembers);
                    g.setCreateStamp(new Date());
                }else {
                    g = new FbGroup();
                    g.setId(groupId);
                    g.setLink(link);
                    g.setGroupName(groupName);
                    g.setGroupType(groupType);
                    g.setNumberMembers(nbMembers);
                    g.setCreateStamp(new Date());
                }
                g = fbGroupRepo.save(g);
                res.add(g);
            }

            return ResponseEntity.ok().body(res);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("null");
    }
}
