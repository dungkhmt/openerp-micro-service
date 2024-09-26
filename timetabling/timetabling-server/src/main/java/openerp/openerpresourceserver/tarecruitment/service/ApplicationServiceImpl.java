package openerp.openerpresourceserver.tarecruitment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.model.entity.User;
import openerp.openerpresourceserver.generaltimetabling.repo.UserRepo;
import openerp.openerpresourceserver.tarecruitment.algorithm.ConvertDataV2;
import openerp.openerpresourceserver.tarecruitment.algorithm.MaxMatching;
import openerp.openerpresourceserver.tarecruitment.entity.dto.ChartDTO;
import openerp.openerpresourceserver.tarecruitment.entity.dto.PaginationDTO;
import openerp.openerpresourceserver.tarecruitment.entity.Application;
import openerp.openerpresourceserver.tarecruitment.entity.ClassCall;
import openerp.openerpresourceserver.tarecruitment.repo.ApplicationRepo;
import openerp.openerpresourceserver.tarecruitment.repo.ClassCallRepo;
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
import java.util.*;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ApplicationServiceImpl implements ApplicationService{

    private ApplicationRepo applicationRepo;
    private UserRepo userRepo;
    private ClassCallRepo classCallRepo;
    private TARecruitment_SemesterService semesterService;
    private MailService mailService;
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
        Optional<Application> existApplication = Optional.ofNullable(applicationRepo.findByUserAndClassCall(existUser.get().getId(), existClassCall.get().getId()));
        if(existApplication.isPresent()) {
            throw new IllegalArgumentException("Application existed");
        }

        application.setApplicationStatus("PENDING");
        application.setAssignStatus("PENDING");
        applicationRepo.save(application);
        String email = application.getEmail();
        String subject = "ĐĂNG KÝ TRỢ GIẢNG";
        String body = "Bạn đã đăng ký trợ giảng cho lớp " + existClassCall.get().getSubjectId() + ", mã lớp " + existClassCall.get().getId();
        log.info(email + subject + body);
        mailService.sendingEmail(email, subject, body);
        return application;
    }

    @Override
    public Application updateApplication(int id, Application application) {
        Optional<Application> existingApplicationOptional = applicationRepo.findById(id);
        if (existingApplicationOptional.isEmpty()) {
            throw new IllegalArgumentException("Application with ID " + id + " not found");
        }

        Application existingApplication = existingApplicationOptional.get();

        existingApplication.setName(application.getName());
        existingApplication.setEmail(application.getEmail());
        existingApplication.setCPA(application.getCPA());
        existingApplication.setEnglishScore(application.getEnglishScore());
        existingApplication.setNote(application.getNote());
        existingApplication.setMssv(application.getMssv());

        Application newApplication = applicationRepo.save(existingApplication);

        return newApplication;
    }

    @Override
    public boolean deleteApplication(int id) {
        Optional<Application> existingApplicationOptional = applicationRepo.findById(id);
        if (existingApplicationOptional.isEmpty()) {
            throw new IllegalArgumentException("Application with ID " + id + " not found");
        }

        applicationRepo.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteMultiApplication(List<Integer> idList) {
        for(int id : idList) {
            applicationRepo.deleteById(id);
        }
        return true;
    }

    @Override
    public Application getApplicationById(int id) {
        Optional<Application> existingApplicationOptional = applicationRepo.findById(id);
        if (existingApplicationOptional.isEmpty()) {
            throw new IllegalArgumentException("Application with ID " + id + " not found");
        }

        Application existingApplication = existingApplicationOptional.get();
        return existingApplication;
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
            if(!Objects.equals(existApplication.getAssignStatus(), "PENDING")) {
                throw new IllegalArgumentException("Đơn xin này đã có trạng thái xếp lớp");
            }
            existApplication.setApplicationStatus(status);
            applicationRepo.save(existApplication);
            String textStatus;
            if("PENDING".equals(status)) textStatus = "ĐANG CHỜ";
            else if ("APPROVED".equals(status)) textStatus = "DUYỆT";
            else textStatus = "TỪ CHỐI";
            String email = existApplication.getEmail();
            String subject = "CẬP NHẬT TRẠNG THÁI ĐƠN ĐĂNG KÝ";
            String body = "Đơn xin vào lớp " + existApplication.getClassCall().getSubjectId() + " đã được cập nhật sang trạng thái " + textStatus;
            mailService.sendingEmail(email, subject, body);
            return existApplication;
        }
    }

    @Override
    public String updateMultipleApplicationStatus(List<Integer> idList, String status) {
        if (!("PENDING".equals(status) || "APPROVED".equals(status) || "REJECTED".equals(status))) {
            throw new IllegalArgumentException("Invalid status");
        }
        else {
            String textStatus;
            int countSuccess = 0;
            if("PENDING".equals(status)) textStatus = "ĐANG CHỜ";
            else if ("APPROVED".equals(status)) textStatus = "DUYỆT";
            else textStatus = "TỪ CHỐI";
            for(int id : idList) {
                Optional<Application> application = applicationRepo.findById(id);
                if(application.isEmpty()) {
                    throw new IllegalArgumentException("Application with id " + id + " did not exist");
                }
                Application existApplication = application.get();
                if(!Objects.equals(existApplication.getAssignStatus(), "PENDING")) {
                    continue;
                }
                existApplication.setApplicationStatus(status);
                applicationRepo.save(existApplication);
                countSuccess++;
                String email = existApplication.getEmail();
                String subject = "CẬP NHẬT TRẠNG THÁI ĐƠN ĐĂNG KÝ";
                String body = "Đơn xin vào lớp " + existApplication.getClassCall().getSubjectId() + " đã được cập nhật sang trạng thái " + textStatus;
                mailService.sendingEmail(email, subject, body);
            }
            return "Cập nhật trạng thái thành công " + countSuccess + " đơn xin trợ giảng";
        }
    }

    // HAVEN'T CHECK THIS
    @Override
    public Application updateAssignStatus(int id, String status) {
        if(!("CANCELED".equals(status) || "APPROVED".equals(status) || "PENDING".equals(status))) {
            throw new IllegalArgumentException("Invalid status");
        }
        else {
            String textStatus;
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
                int day = updateApplication.getClassCall().getDay();

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
                        int existingDay = app.getClassCall().getDay();

                        // Check if there is an overlap in time periods
                        if (day == existingDay && !(endPeriod <= existingStartPeriod || startPeriod >= existingEndPeriod)) {
                            throw new IllegalArgumentException("Trùng lịch với mã lớp " + app.getClassCall().getId());
                        }
                    }
                }

                List<Application> remainApplication = applicationRepo.getAllRemainingApplication(semester, updateApplication.getUser().getId(), updateApplication.getClassCall().getId());
                for(Application app : remainApplication) {
                    if(Objects.equals(app.getAssignStatus(), "APPROVED")) {
                        throw new IllegalArgumentException("Lớp này đã có trợ giảng");
                    }
                }

                for(Application app : remainApplication) {
                    app.setAssignStatus("CANCELED");
                    applicationRepo.save(app);
                    String email = app.getEmail();
                    String subject = "CẬP NHẬT TRẠNG THÁI TRỢ GIẢNG";
                    String body = "Đơn xin trợ giảng lớp " + app.getClassCall().getSubjectId() + " đã bị TỪ CHỐI";
                    mailService.sendingEmail(email, subject, body);
                }

                updateApplication.setAssignStatus(status);

            }

            else updateApplication.setAssignStatus(status);
            applicationRepo.save(updateApplication);

            if("PENDING".equals(status)) textStatus = " đã chuyển sang trạng thái ĐANG CHỜ";
            else if ("APPROVED".equals(status)) textStatus = " đã được DUYỆT";
            else textStatus = " đã bị TỪ CHỐI";

            String email = updateApplication.getEmail();
            String subject = "CẬP NHẬT TRẠNG THÁI TRỢ GIẢNG";
            String body = "Đơn xin trợ giảng lớp " + updateApplication.getClassCall().getSubjectId() + textStatus;
            mailService.sendingEmail(email, subject, body);
            return updateApplication;
        }
    }

    @Override
    public void autoAssignApplication(String semester) {
        List<String> userApplies = applicationRepo.findDistinctUserIdsBySemester(semester, "APPROVED", "PENDING");
        log.info("Found " + userApplies.size() + " user");
        for(String userInfo : userApplies) {
            log.info("There is user " + userInfo);
        }
        List<Application> applications = applicationRepo.findApplicationToAutoAssign("APPROVED", "PENDING", semester);
        log.info("Found " + applications.size() + " applications");
        List<ClassCall> classCalls = applicationRepo.findDistinctClassCallBySemester(semester);
        log.info("Found " + classCalls.size() + " class");

        List<Integer> classIds = new ArrayList<>();
        for(ClassCall elm : classCalls) {
            classIds.add(elm.getId());
        }

//        MaxMatching maxMatching = new MaxMatching(applications, userApplies, classIds);
//        List<Application> assignApplication = maxMatching.getAssignApplications();

        ConvertDataV2 convertDataV2 = new ConvertDataV2(applications, userApplies, classCalls);
        List<Application> assignApplication = convertDataV2.solvingProblem();


        for(Application application : assignApplication) {
            log.info("User " + application.getUser().getId() + " got assign to class id: " + application.getClassCall().getId());
        }

        applications.removeAll(assignApplication);

        for(Application app : applications) {
            app.setAssignStatus("CANCELED");
            applicationRepo.save(app);
            String email = app.getEmail();
            String subject = "CẬP NHẬT TRẠNG THÁI TRỢ GIẢNG";
            String body = "Đơn xin trợ giảng lớp " + app.getClassCall().getSubjectId() + " đã bị TỪ CHỐI";
            mailService.sendingEmail(email, subject, body);
        }

        for(Application app : assignApplication) {
            app.setAssignStatus("APPROVED");
            applicationRepo.save(app);
            String email = app.getEmail();
            String subject = "CẬP NHẬT TRẠNG THÁI TRỢ GIẢNG";
            String body = "Đơn xin trợ giảng lớp " + app.getClassCall().getSubjectId() + " đã được DUYỆT";
            mailService.sendingEmail(email, subject, body);
        }
    }

    @Override
    public void oldAutoAssignApplication(String semester) {
        List<String> userApplies = applicationRepo.findDistinctUserIdsBySemester(semester, "APPROVED", "PENDING");
        log.info("Found " + userApplies.size() + " user");
        for(String userInfo : userApplies) {
            log.info("There is user " + userInfo);
        }
        List<Application> applications = applicationRepo.findApplicationToAutoAssign("APPROVED", "PENDING", semester);
        log.info("Found " + applications.size() + " applications");
        List<ClassCall> classCalls = applicationRepo.findDistinctClassCallBySemester(semester);
        log.info("Found " + classCalls.size() + " class");

        List<Integer> classIds = new ArrayList<>();
        for(ClassCall elm : classCalls) {
            classIds.add(elm.getId());
        }

        MaxMatching maxMatching = new MaxMatching(applications, userApplies, classIds);
        List<Application> assignApplication = maxMatching.getAssignApplications();

//        ConvertDataV2 convertDataV2 = new ConvertDataV2(applications, userApplies, classCalls);
//        List<Application> assignApplication = convertDataV2.solvingProblem();

        for(Application application : assignApplication) {
            log.info("User " + application.getUser().getId() + " got assign to class id: " + application.getClassCall().getId());
        }

        for(Application app : assignApplication) {
            updateAssignStatus(app.getId(), "APPROVED");
        }
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

    @Override
    public PaginationDTO<Application> getTABySemester(String semester, String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Application> applications = applicationRepo.getTABySemester("APPROVED", "APPROVED", semester, search, pageable);

        PaginationDTO<Application> paginationDTO = new PaginationDTO<>();

        paginationDTO.setPage(applications.getNumber());
        paginationDTO.setTotalElement((int) applications.getTotalElements());
        paginationDTO.setData(applications.getContent());

        return paginationDTO;
    }

    @Override
    public List<ChartDTO> getApplicatorEachSemesterData() {
        List<ChartDTO> chart = new ArrayList<>();
        List<String> semesters = semesterService.getAllSemester();
        for(String semester : semesters) {
            List<String> applications = applicationRepo.findAllUserBySemester(semester);
            ChartDTO data = new ChartDTO(semester, applications.size());
            chart.add(data);
        }
        return chart;
    }

    @Override
    public List<ChartDTO> getNumbApplicationEachSemesterData() {
        List<ChartDTO> chart = new ArrayList<>();
        List<String> semesters = semesterService.getAllSemester();
        for(String semester : semesters) {
            List<Application> applications = applicationRepo.getAllApplicationBySemester(semester);
            ChartDTO data = new ChartDTO(semester, applications.size());
            chart.add(data);
        }
        return chart;
    }

    @Override
    public List<ChartDTO> getNumbApplicationApproveEachSemesterData() {
        List<ChartDTO> chart = new ArrayList<>();
        List<String> semesters = semesterService.getAllSemester();
        for(String semester : semesters) {
            List<String> applications = applicationRepo.getTADataBySemester(semester);
            ChartDTO data = new ChartDTO(semester, applications.size());
            chart.add(data);
        }
        return chart;
    }

    @Override
    public List<ChartDTO> dataApplicationEachCourseThisSemester() {
        List<ChartDTO> chart = new ArrayList<>();
        String semester = semesterService.getCurrentSemester();
        List<String> courses = classCallRepo.getDistinctCourseBySemester(semester);
        for(String course : courses) {
            List<Application> applications = applicationRepo.getApplicationByCourseAndSemester(semester, course);
            ChartDTO data = new ChartDTO(course, applications.size());
            chart.add(data);
        }
        return chart;
    }
}
