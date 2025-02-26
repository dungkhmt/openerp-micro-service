package openerp.openerpresourceserver.fb.service;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.fb.entity.FbGroup;
import openerp.openerpresourceserver.fb.entity.FbUser;
import openerp.openerpresourceserver.fb.entity.FbUserGroup;
import openerp.openerpresourceserver.fb.model.ModelInputImportSyncFbUsers;
import openerp.openerpresourceserver.fb.model.ModelResponseUser;
import openerp.openerpresourceserver.fb.repo.FbGroupRepo;
import openerp.openerpresourceserver.fb.repo.FbUserGroupRepo;
import openerp.openerpresourceserver.fb.repo.FbUserRepo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class FbUserServiceImpl implements FbUserService{
    private FbUserRepo fbUserRepo;
    private FbUserGroupRepo fbUserGroupRepo;
    private FbGroupRepo fbGroupRepo;
    @Override
    public List<ModelResponseUser> getAllUsers() {
        List<FbUser> L = fbUserRepo.findAll();
        List<ModelResponseUser> res = L.stream().map(u -> new ModelResponseUser(u.getId(),u.getGroupId(),u.getName(),u.getLink(),u.getCreateStamp())).toList();
        return res;
    }

    @Override
    @Transactional
    public List<FbUser> synchronizeUsers(String inputJson, MultipartFile file) throws Exception {
        Gson gson = new Gson();
        ModelInputImportSyncFbUsers modelUpload = gson.fromJson(
                inputJson,
                ModelInputImportSyncFbUsers.class);
        String batchId = modelUpload.getBatchId();
        log.info("synchronizeUsers, batchId = " + batchId);
        List<FbUser> res = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            log.info("synchronizeUsers, lastRowNum = " + lastRowNum);

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

            return res;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

        //return null;
    }
}
