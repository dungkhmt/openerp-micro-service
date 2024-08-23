package openerp.openerpresourceserver.trainingprogcourse.service.impl;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisite;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgCourseRepo;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgPrerequisiteRepo;
import openerp.openerpresourceserver.trainingprogcourse.service.TrainingProgCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
}
