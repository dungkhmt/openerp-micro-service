package openerp.openerpresourceserver.trainingprogcourse.service.impl;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisite;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgCourseRepo;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgPrerequisiteRepo;
import openerp.openerpresourceserver.trainingprogcourse.service.TrainingProgCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Set<TrainingProgPrerequisite> prerequisites = new HashSet<>();

        try {
            // Kiểm tra nếu khóa học đã tồn tại
            if (trainingProgCourseRepo.existsById(request.getId())) {
                throw new IllegalArgumentException("Course with ID " + request.getId() + " already exists.");
            }

            // Tạo khóa học mới
            newTrainingProgCourse.setId(request.getId());
            newTrainingProgCourse.setCourseName(request.getCourseName());
            newTrainingProgCourse.setCredit(request.getCredit());
            newTrainingProgCourse.setStatus("active"); // hoặc enum
            newTrainingProgCourse.setCreateStamp(new Date());
            newTrainingProgCourse.setLastUpdated(new Date());

            // Xử lý danh sách môn học tiên quyết
            if (request.getPrerequisites() != null && !request.getPrerequisites().isEmpty()) {
                for (String prerequisiteId : request.getPrerequisites()) {
                    // Kiểm tra nếu môn học tiên quyết tồn tại
//                    if (!trainingProgCourseRepo.existsById(prerequisiteId)) {
//                        throw new IllegalArgumentException("Prerequisite course with ID " + prerequisiteId + " does not exist.");
//                    }
                    Optional<TrainingProgCourse> courseprerequisite = trainingProgCourseRepo.findById(prerequisiteId);
                    if (courseprerequisite.isPresent()) {
                        TrainingProgPrerequisite prerequisite = new TrainingProgPrerequisite();
                        prerequisite.setCourse(newTrainingProgCourse);
                        prerequisite.setPrerequisiteCourse(courseprerequisite.get());
                        prerequisite.setCreateStamp(new Date());
                        prerequisite.setLastUpdated(new Date());
                        prerequisites.add(prerequisite);
                    }

                }

                // Gán danh sách môn học tiên quyết vào khóa học
                newTrainingProgCourse.setPrerequisites(prerequisites);
            }

            // Lưu khóa học mới
            trainingProgCourseRepo.save(newTrainingProgCourse);

            // Lưu danh sách môn học tiên quyết
            trainingProgPrerequisiteRepo.saveAll(prerequisites);

        } catch (Exception e) {
            // Xử lý ngoại lệ (ví dụ: ghi log)
            throw new RuntimeException("Failed to add course", e);
        }
    }

    @Override
    public void update(String id, RequestTrainingProgCourse request) {
        try {
            // Kiểm tra nếu khóa học tồn tại
            Optional<TrainingProgCourse> existingCourseOpt = trainingProgCourseRepo.findById(id);
            if (!existingCourseOpt.isPresent()) {
                throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
            }

            TrainingProgCourse existingCourse = existingCourseOpt.get();

            // Cập nhật thông tin khóa học
            existingCourse.setCourseName(request.getCourseName());
            existingCourse.setCredit(request.getCredit());
            existingCourse.setStatus(request.getStatus());
            existingCourse.setLastUpdated(new Date());

            // Xử lý danh sách môn học tiên quyết
            Set<TrainingProgPrerequisite> prerequisites = new HashSet<>();
            if (request.getPrerequisites() != null && !request.getPrerequisites().isEmpty()) {
                for (String prerequisiteId : request.getPrerequisites()) {
                    Optional<TrainingProgCourse> prerequisiteCourseOpt = trainingProgCourseRepo.findById(prerequisiteId);
                    if (prerequisiteCourseOpt.isPresent()) {
                        TrainingProgPrerequisite prerequisite = new TrainingProgPrerequisite();
                        prerequisite.setCourse(existingCourse);
                        prerequisite.setPrerequisiteCourse(prerequisiteCourseOpt.get());
                        prerequisite.setCreateStamp(new Date());
                        prerequisite.setLastUpdated(new Date());
                        prerequisites.add(prerequisite);
                    } else {
                        throw new IllegalArgumentException("Prerequisite course with ID " + prerequisiteId + " does not exist.");
                    }
                }

                // Cập nhật danh sách môn học tiên quyết
                existingCourse.setPrerequisites(prerequisites);
            }

            // Lưu các thay đổi
            trainingProgCourseRepo.save(existingCourse);

            // Xóa các môn học tiên quyết cũ và lưu các môn học tiên quyết mới
            //trainingProgPrerequisiteRepo.delete(existingCourse);
            trainingProgPrerequisiteRepo.saveAll(prerequisites);

        } catch (Exception e) {
            // Xử lý ngoại lệ (ví dụ: ghi log)
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

        // Lay danh sach thong tin mon hoc tien quyet
        List<String> prerequisiteId = course.getPrerequisites().stream()
                .map(prerequisite -> prerequisite.getPrerequisiteCourse().getId())
                .collect(Collectors.toList());
        response.setPrerequisites(prerequisiteId);

        return response;
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

            // Lay danh sach thong tin mon hoc tien quyet
            List<String> prerequisiteId = course.getPrerequisites().stream()
                    .map(prerequisite -> prerequisite.getPrerequisiteCourse().getId())
                    .collect(Collectors.toList());
            response.setPrerequisites(prerequisiteId);

            responseList.add(response);
        }
        return responseList;
    }

}
