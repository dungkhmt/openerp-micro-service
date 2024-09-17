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

        try {
            // Kiểm tra nếu học phần  đã tồn tại
            if (trainingProgCourseRepo.existsById(request.getId())) {
                throw new IllegalArgumentException("Course with ID " + request.getId() + " already exists.");
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
                            .orElseThrow(() -> new IllegalArgumentException("Prerequisite course with ID " + prerequisiteId + " does not exist."));

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
                    .orElseThrow(() -> new IllegalArgumentException(" " + id + " does not exist."));

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
                            .orElseThrow(() -> new IllegalArgumentException("Prerequisite course with ID " + prerequisiteId + " does not exist."));

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

//    @Override
//    @Transactional
//    public void delete(String id) {
//        Optional<TrainingProgCourse> course = trainingProgCourseRepo.findById(id);
//
//        if (!course.isPresent()) {
//            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
//        }
//
//        course.get().setStatus("inactive");// hoac enum
//        trainingProgCourseRepo.save(course.get());
//
//        // Xoa hoc phan tien quyet
//        //course.get().getPrerequisites().clear();
//
//        // Xoa hoc phan
//        //trainingProgCourseRepo.delete(course);
//    }


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

}
