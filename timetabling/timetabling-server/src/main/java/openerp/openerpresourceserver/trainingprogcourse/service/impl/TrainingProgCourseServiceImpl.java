package openerp.openerpresourceserver.trainingprogcourse.service.impl;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisite;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisiteId;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgCourseRepo;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgPrerequisiteRepo;
import openerp.openerpresourceserver.trainingprogcourse.service.TrainingProgCourseService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainingProgCourseServiceImpl implements TrainingProgCourseService {

    private final TrainingProgCourseRepo trainingProgCourseRepo;
    private final TrainingProgPrerequisiteRepo trainingProgPrerequisiteRepo;

    @Autowired
    public TrainingProgCourseServiceImpl(TrainingProgCourseRepo trainingProgCourseRepo, TrainingProgPrerequisiteRepo trainingProgPrerequisiteRepo) {
        this.trainingProgCourseRepo = trainingProgCourseRepo;
        this.trainingProgPrerequisiteRepo = trainingProgPrerequisiteRepo;
    }

    @Override
    @Transactional
    public void create(RequestTrainingProgCourse request) {
        TrainingProgCourse newTrainingProgCourse = new TrainingProgCourse();

        try {
            // Kiểm tra nếu học phần  đã tồn tại
            if (trainingProgCourseRepo.existsById(request.getId())) {
                throw new IllegalArgumentException("Học phần với mã " + request.getId() + " đã tồn tại");
            }

            // Tạo học phần  mới
            newTrainingProgCourse.setId(request.getId());
            newTrainingProgCourse.setCourseName(request.getCourseName());
            newTrainingProgCourse.setCredit(request.getCredit());
            newTrainingProgCourse.setStatus("active"); // hoặc enum
            newTrainingProgCourse.setCreateStamp(new Date());
            newTrainingProgCourse.setLastUpdated(new Date());


            newTrainingProgCourse.setPrerequisites(new HashSet<>());

            // Lưu khọc phần  mới trước
            newTrainingProgCourse = trainingProgCourseRepo.save(newTrainingProgCourse);

            // Xử lý danh sách học phần  tiên quyết
            if (request.getPrerequisites() != null && !request.getPrerequisites().isEmpty()) {
                for (String prerequisiteId : request.getPrerequisites()) {
                    // Kiểm tra nếu học phần  tiên quyết tồn tại
                    TrainingProgCourse coursePrerequisite = trainingProgCourseRepo.findById(prerequisiteId)
                            .orElseThrow(() -> new IllegalArgumentException("Môn học tiên quyết với Id  " + prerequisiteId + " không tồn tại."));

                    // Tạo đối tượng TrainingProgPrerequisite
                    TrainingProgPrerequisite prerequisite = new TrainingProgPrerequisite();
                    TrainingProgPrerequisiteId prerequisiteIds = new TrainingProgPrerequisiteId();
                    prerequisiteIds.setCourseId(newTrainingProgCourse.getId());
                    prerequisiteIds.setPrerequisiteCourseId(coursePrerequisite.getId());
                    prerequisite.setId(prerequisiteIds);
                    prerequisite.setCourse(newTrainingProgCourse);
                    prerequisite.setPrerequisiteCourse(coursePrerequisite);
                    prerequisite.setCreateStamp(new Date());
                    prerequisite.setLastUpdated(new Date());

                    // Thêm vào danh sách prerequisites của course mới
                    newTrainingProgCourse.getPrerequisites().add(prerequisite);
                }

                // Cập nhật lại học phần  với danh sách học phần  tiên quyết mới
                trainingProgCourseRepo.save(newTrainingProgCourse);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to add course", e);
        }
    }
    @Override
    @Transactional
    public void update(String id, RequestTrainingProgCourse request) {
        try {
            // Kiểm tra nếu học phần  tồn tại
            TrainingProgCourse existingCourse = trainingProgCourseRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mã học phần với id : " + id + " không tồn tại."));

            // Cập nhật thông tin học phần cơ bản
            existingCourse.setCourseName(request.getCourseName());
            existingCourse.setCredit(request.getCredit());
            existingCourse.setStatus(request.getStatus());
            existingCourse.setLastUpdated(new Date());

            // Xóa tất cả các học phần  tiên quyết hiện tại
            existingCourse.getPrerequisites().clear();

            // Xử lý danh sách học phần tiên quyết mới
            if (request.getPrerequisites() != null && !request.getPrerequisites().isEmpty()) {
                for (String prerequisiteId : request.getPrerequisites()) {
                    TrainingProgCourse prerequisiteCourse = trainingProgCourseRepo.findById(prerequisiteId)
                            .orElseThrow(() -> new IllegalArgumentException("Môn học tiên quyết với Id   " + prerequisiteId + "không tồn tại."));

                    TrainingProgPrerequisite prerequisite = new TrainingProgPrerequisite();
                    TrainingProgPrerequisiteId prerequisiteIds = new TrainingProgPrerequisiteId();
                    prerequisiteIds.setCourseId(existingCourse.getId());
                    prerequisiteIds.setPrerequisiteCourseId(prerequisiteCourse.getId());
                    prerequisite.setId(prerequisiteIds);
                    prerequisite.setCourse(existingCourse);
                    prerequisite.setPrerequisiteCourse(prerequisiteCourse);
                    prerequisite.setCreateStamp(new Date());
                    prerequisite.setLastUpdated(new Date());

                    existingCourse.getPrerequisites().add(prerequisite);
                }
            }

            // Lưu các thay đổi
            trainingProgCourseRepo.save(existingCourse);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update course", e);
        }
    }
    @Override
    public ResponseTrainingProgCourse getDetail(String id) {
        Optional<TrainingProgCourse> courseOpt = trainingProgCourseRepo.findById(id);

        if (!courseOpt.isPresent()) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }

        TrainingProgCourse course = courseOpt.get();

        // ghep thong tin thuong
        ResponseTrainingProgCourse response = new ResponseTrainingProgCourse();
        response.setId(course.getId());
        response.setCourseName(course.getCourseName());
        response.setCredit(course.getCredit());
        response.setStatus(course.getStatus());
        response.setCreateStamp(course.getCreateStamp());
        response.setLastUpdated(course.getLastUpdated());

        // Lay danh sach thong tin hoc phan  tien quyet
        List<String> prerequisiteId = course.getPrerequisites().stream()
                .map(prerequisite -> prerequisite.getPrerequisiteCourse().getId())
                .collect(Collectors.toList());
        response.setPrerequisites(prerequisiteId);

        return response;
    }

    @Override
    @Transactional
    public void delete(String id) {
        Optional<TrainingProgCourse> course = trainingProgCourseRepo.findById(id);

        if (!course.isPresent()) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }
        course.get().setStatus("inactive");
        trainingProgCourseRepo.save(course.get());
        course.get().getPrerequisites().clear();
        trainingProgCourseRepo.save(course.get());
    }


    @Override
    public List<ResponseTrainingProgCourse> getAll() {
        List<TrainingProgCourse> courses = trainingProgCourseRepo.findAll();
        List<ResponseTrainingProgCourse> responseList = new ArrayList<>();

        for (TrainingProgCourse course : courses) {
            ResponseTrainingProgCourse response = new ResponseTrainingProgCourse();

            // ghep thong tin thuong
            response.setId(course.getId());
            response.setCourseName(course.getCourseName());
            response.setCredit(course.getCredit());
            response.setStatus(course.getStatus());
            response.setCreateStamp(course.getCreateStamp());
            response.setLastUpdated(course.getLastUpdated());

            // Lay danh sach thong tin hoc phan tien quyet
            List<String> prerequisiteId = course.getPrerequisites().stream()
                    .map(prerequisite -> prerequisite.getPrerequisiteCourse().getId())
                    .collect(Collectors.toList());
            response.setPrerequisites(prerequisiteId);

            responseList.add(response);
        }
        return responseList;
    }

    @Override
    @Transactional
    public int importTrainingProgCourse(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            int numberOfData = 0;
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel file is empty");
            }

            // Validate header
            if (!"Mã học phần".equals(headerRow.getCell(0).getStringCellValue()) ||
                    !"Tên học phần".equals(headerRow.getCell(1).getStringCellValue()) ||
                    !"Số tín chỉ".equals(headerRow.getCell(2).getStringCellValue()) ||
                    !"Học phần tiên quyết".equals(headerRow.getCell(3).getStringCellValue())) {
                throw new IllegalArgumentException("Excel file headers are invalid");
            }

            // Read data rows
            int rowNum = 1;
            Row dataRow = sheet.getRow(rowNum);
            while (dataRow != null) {
                String id = dataRow.getCell(0).getStringCellValue().trim();
                String courseName = dataRow.getCell(1).getStringCellValue().trim();
                Long credits = (long) dataRow.getCell(2).getNumericCellValue();
                Cell prerequisitesCell = dataRow.getCell(3);
                String prerequisitesStr = (prerequisitesCell != null)
                        ? prerequisitesCell.getStringCellValue().trim()
                        : "";
                // Check for existing course
                if (trainingProgCourseRepo.existsById(id)) {
                    rowNum++;
                    dataRow = sheet.getRow(rowNum);
                    continue;
                }

                // Create new course
                TrainingProgCourse course = new TrainingProgCourse();
                course.setId(id);
                course.setCourseName(courseName);
                course.setCredit(credits);
                course.setStatus("active");
                course.setCreateStamp(new Date());
                course.setLastUpdated(new Date());

                // Process prerequisites
                Set<TrainingProgPrerequisite> prerequisites = new HashSet<>();
                if (!prerequisitesStr.isEmpty()) {
                    String[] prerequisiteIds = prerequisitesStr.split(",");
                    for (String prerequisiteId : prerequisiteIds) {
                        prerequisiteId = prerequisiteId.trim();
                        TrainingProgCourse prerequisiteCourse = trainingProgCourseRepo.findById(prerequisiteId)
                                .orElseThrow(() -> new IllegalArgumentException("Học phần tiên quyết không tồn tại."));
                        TrainingProgPrerequisite prerequisite = new TrainingProgPrerequisite();
                        TrainingProgPrerequisiteId prerequisiteIdObj = new TrainingProgPrerequisiteId();
                        prerequisiteIdObj.setCourseId(course.getId());
                        prerequisiteIdObj.setPrerequisiteCourseId(prerequisiteCourse.getId());
                        prerequisite.setId(prerequisiteIdObj);
                        prerequisite.setCourse(course);
                        prerequisite.setPrerequisiteCourse(prerequisiteCourse);
                        prerequisite.setCreateStamp(new Date());
                        prerequisite.setLastUpdated(new Date());

                        prerequisites.add(prerequisite);
                    }
                }
                course.setPrerequisites(prerequisites);

                // Save the course
                trainingProgCourseRepo.save(course);
                numberOfData++;

                rowNum++;
                dataRow = sheet.getRow(rowNum);
            }

            return numberOfData;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading Excel file", e);
        }
    }
}
