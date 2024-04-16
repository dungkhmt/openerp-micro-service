package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.algorithm.MaxMatching;
import openerp.openerpresourceserver.dto.PaginationDTO;
import openerp.openerpresourceserver.entity.Application;
import openerp.openerpresourceserver.entity.ClassCall;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.repo.ApplicationRepo;
import openerp.openerpresourceserver.repo.ClassCallRepo;
import openerp.openerpresourceserver.repo.UserRepo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ApplicationServiceImpl implements ApplicationService{

    private ApplicationRepo applicationRepo;
    private UserRepo userRepo;
    private ClassCallRepo classCallRepo;
    @Override
    public Application createApplication(Application application) {
        Optional<User> existUser = userRepo.findById(application.getUser().getId());
        if (existUser.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + application.getUser().getId() + " not found");
        }
        Optional<ClassCall> existClassCall = classCallRepo.findById(application.getClassCall().getId());
        if(existClassCall.isEmpty()) {
            throw new IllegalArgumentException("Class with ID " + application.getClassCall().getId() + " not found");
        }
        application.setApplicationStatus("PENDING");
        application.setAssignStatus("PENDING");
        applicationRepo.save(application);
        return application;
    }

    @Override
    public PaginationDTO<Application> getMyApplications(String userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Application> applicationPage = applicationRepo.findByUserId(userId, pageable);

        PaginationDTO<Application> paginationDTO = new PaginationDTO<>();

        paginationDTO.setPage(applicationPage.getNumber());
        paginationDTO.setTotalElement((int) applicationPage.getTotalElements());
        paginationDTO.setData(applicationPage.getContent());

        return paginationDTO;
    }

    @Override
    public PaginationDTO<Application> getApplicationByClassId(int classCallId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Application> applicationPage = applicationRepo.findByClassCallId(classCallId, pageable);

        PaginationDTO<Application> paginationDTO = new PaginationDTO<>();

        paginationDTO.setPage(applicationPage.getNumber());
        paginationDTO.setTotalElement((int) applicationPage.getTotalElements());
        paginationDTO.setData(applicationPage.getContent());

        return paginationDTO;
    }

    @Override
    public List<Application> getUniqueApplicator() {
        return applicationRepo.findDistinctApplicationsByUser();
    }

    @Override
    public PaginationDTO<Application> getApplicationBySemester(String semester, String search, String applicationStatus, int page, int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        if(applicationStatus.equals("ALL")) applicationStatus = "";
        Page<Application> applicationPage = applicationRepo.findApplicationsByClassSemester(semester, search, applicationStatus, pageable);

        PaginationDTO<Application> paginationDTO = new PaginationDTO<>();

        paginationDTO.setPage(applicationPage.getNumber());
        paginationDTO.setTotalElement((int) applicationPage.getTotalElements());
        paginationDTO.setData(applicationPage.getContent());

        return paginationDTO;
    }

    @Override
    public PaginationDTO<Application> getApplicationByApplicationStatusAndSemester(String applicationStatus,
                                                                                   String semester, String search, String assignStatus, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        if(assignStatus.equals("ALL")) assignStatus = "";
        log.info(search + " + " + assignStatus);
        Page<Application> applicationPage = applicationRepo.findByApplicationStatusAndSemester(applicationStatus, semester, search, assignStatus, pageable);

        PaginationDTO<Application> paginationDTO = new PaginationDTO<>();

        paginationDTO.setPage(applicationPage.getNumber());
        paginationDTO.setTotalElement((int) applicationPage.getTotalElements());
        paginationDTO.setData(applicationPage.getContent());

        return paginationDTO;
    }

    @Override
    public Application updateApplicationStatus(int id, String status) {
        if (!("PENDING".equals(status) || "APPROVED".equals(status) || "REJECTED".equals(status))) {
            throw new IllegalArgumentException("Invalid status");
        }
        else {
            Optional<Application> application = applicationRepo.findById(id);
            if(application.isEmpty()) {
                throw new IllegalArgumentException("Application with id " + id + " did not exist");
            }
            Application existApplication = application.get();
            existApplication.setApplicationStatus(status);
            applicationRepo.save(existApplication);
            return existApplication;
        }
    }

    // HAVEN'T CHECK THIS
    @Override
    public Application updateAssignStatus(int id, String status) {
        if(!("CANCELED".equals(status) || "APPROVED".equals(status) || "PENDING".equals(status))) {
            throw new IllegalArgumentException("Invalid status");
        }
        else {
            // Find that application by id
            Optional<Application> application = applicationRepo.findById(id);
            if(application.isEmpty()) {
                throw new IllegalArgumentException("Application with id " + id + " did not exist");
            }
            Application updateApplication = application.get();
            // If status is approved then check
            if ("APPROVED".equals(status)) {
                int startPeriod = updateApplication.getClassCall().getStartPeriod();
                int endPeriod = updateApplication.getClassCall().getEndPeriod();

                String semester = updateApplication.getClassCall().getSemester();

                // Search all the user application that's have been approved
                String userId = updateApplication.getUser().getId();
                List<Application> existedApplications = applicationRepo.findApplicationByUserIdAndAssignStatus(userId, "APPROVED", semester);

                // If !null then
                if (!existedApplications.isEmpty()) {
                    // Query each one, to find if the time is conflict, if yes throw error
                    for (Application app : existedApplications) {
                        int existingStartPeriod = app.getClassCall().getStartPeriod();
                        int existingEndPeriod = app.getClassCall().getEndPeriod();

                        // Check if there is an overlap in time periods
                        if (!(endPeriod <= existingStartPeriod || startPeriod >= existingEndPeriod)) {
                            throw new IllegalArgumentException("Trùng lịch với mã lớp " + app.getClassCall().getId());
                        }
                    }
                }
            }

            updateApplication.setAssignStatus(status);
            applicationRepo.save(updateApplication);
            return updateApplication;
        }
    }

    @Override
    public int[][] autoAssignApplication(String semester) {
        List<String> userApplies = applicationRepo.findDistinctUserIdsBySemester(semester);
        log.info("Found " + userApplies.size() + " user");
        List<Application> applications = applicationRepo.findApplicationToAutoAssign("APPROVED", "PENDING", semester);
        log.info("Found " + applications.size() + " applications");
        List<Integer> classCalls = applicationRepo.findDistinctClassCallIdsBySemester(semester);
        log.info("Found " + classCalls.size() + " class");
        // could improve this
        MaxMatching maxMatching = new MaxMatching(applications, userApplies, classCalls);
        List<Application> assignApplications = maxMatching.getAssignApplications();

        for(Application app : applications) {
            app.setAssignStatus("CANCELED");
            applicationRepo.save(app);
        }

        for(Application app : assignApplications) {
            app.setAssignStatus("APPROVED");
            applicationRepo.save(app);
        }
        return maxMatching.getGraph();
    }

    @Override
    public byte[] generateExcelFile(String semester) throws IOException {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách sinh viên trợ giảng");

            List<Application> applications = applicationRepo.findApplicationToAutoAssign("APPROVED", "APPROVED", semester);

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("STT");
            headerRow.createCell(1).setCellValue("Tên sinh viên");
            headerRow.createCell(2).setCellValue("MSSV");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Số điện thoại");
            headerRow.createCell(5).setCellValue("CPA");
            headerRow.createCell(6).setCellValue("Điểm tiếng anh");
            headerRow.createCell(7).setCellValue("Môn học");
            headerRow.createCell(8).setCellValue("Mã môn học");
            headerRow.createCell(9).setCellValue("Mã lớp");
            headerRow.createCell(10).setCellValue("Phòng học");
            headerRow.createCell(11).setCellValue("Ngày");
            headerRow.createCell(12).setCellValue("Tiết bắt đầu");
            headerRow.createCell(13).setCellValue("Tiết kết thúc");
            headerRow.createCell(14).setCellValue("Ghi chú");

            int rowNums = 1;
            for(Application app : applications) {
                Row row = sheet.createRow(rowNums);
                row.createCell(0).setCellValue(rowNums);
                row.createCell(1).setCellValue(app.getName());
                row.createCell(2).setCellValue(app.getMssv());
                row.createCell(3).setCellValue(app.getEmail());
                row.createCell(4).setCellValue(app.getPhoneNumber());
                row.createCell(5).setCellValue(app.getCPA());
                row.createCell(6).setCellValue(app.getEnglishScore());
                row.createCell(7).setCellValue(app.getClassCall().getSubjectName());
                row.createCell(8).setCellValue(app.getClassCall().getSubjectId());
                row.createCell(9).setCellValue(app.getClassCall().getId());
                row.createCell(10).setCellValue(app.getClassCall().getClassRoom());
                row.createCell(11).setCellValue(app.getClassCall().getDay());
                row.createCell(12).setCellValue(app.getClassCall().getStartPeriod());
                row.createCell(13).setCellValue(app.getClassCall().getEndPeriod());
                row.createCell(14).setCellValue(app.getNote());
                rowNums++;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return outputStream.toByteArray();
        }
    }
}
