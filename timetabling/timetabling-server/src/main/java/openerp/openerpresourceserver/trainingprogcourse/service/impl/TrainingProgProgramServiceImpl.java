package openerp.openerpresourceserver.trainingprogcourse.service.impl;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.model.entity.User;
import openerp.openerpresourceserver.generaltimetabling.repo.UserRepo;
import openerp.openerpresourceserver.trainingprogcourse.dto.PaginationDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.ResponseTrainingProgProgramDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.TrainingProgCourseDetail;
import openerp.openerpresourceserver.trainingprogcourse.dto.TrainingProgProgramInfo;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.RequestTrainingProgProgramDTO;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.TrainingProgScheduleUpdateRequest;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgProgram;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgSchedule;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgSemester;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgCourseRepo;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgProgramRepo;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgScheduleRepo;
import openerp.openerpresourceserver.trainingprogcourse.repository.TrainingProgSemesterRepo;
import openerp.openerpresourceserver.trainingprogcourse.service.TrainingProgProgramService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TrainingProgProgramServiceImpl implements TrainingProgProgramService {
    private final TrainingProgProgramRepo trainingProgProgramRepo;
    private final TrainingProgScheduleRepo trainingProgScheduleRepo;
    private final TrainingProgSemesterRepo trainingProgSemesterRepo;
    private final TrainingProgCourseRepo trainingProgCourseRepo;
    private final UserRepo userRepo;

    public TrainingProgProgramServiceImpl(TrainingProgProgramRepo trainingProgProgramRepo, TrainingProgScheduleRepo trainingProgScheduleRepo, TrainingProgSemesterRepo trainingProgSemesterRepo, TrainingProgCourseRepo trainingProgCourseRepo, UserRepo userRepo) {
        this.trainingProgProgramRepo = trainingProgProgramRepo;
        this.trainingProgScheduleRepo = trainingProgScheduleRepo;
        this.trainingProgSemesterRepo = trainingProgSemesterRepo;
        this.trainingProgCourseRepo = trainingProgCourseRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    @Override
    public void create(RequestTrainingProgProgramDTO requestDTO) {
        // Tạo đối tượng TrainingProgProgram mới
        TrainingProgProgram program = new TrainingProgProgram();
        program.setId(requestDTO.getId());
        program.setName(requestDTO.getName());
        program.setCreateStamp(requestDTO.getCreateStamp());
        program.setLastUpdated(requestDTO.getLastUpdated());

        // Tạo danh sách TrainingProgSchedule từ requestDTO
        List<TrainingProgSchedule> schedules = requestDTO.getCourses().stream().map(
                request -> {
                    TrainingProgSchedule schedule = new TrainingProgSchedule();
                    Optional<TrainingProgCourse> optionalTrainingProgCourse = trainingProgCourseRepo.findById(request);
                    if (optionalTrainingProgCourse.isPresent()) {
                        TrainingProgCourse course = optionalTrainingProgCourse.get();
                        schedule.setCourse(course);
                        schedule.setProgram(program); // Đảm bảo đây là chương trình mới tạo
                        schedule.setStatus("active");
                        schedule.setId(UUID.randomUUID());


                        // Giả sử bạn có user đã tồn tại
                        Optional<User> existUser = userRepo.findById("dungpq");
                        if (existUser.isPresent()) {
                            schedule.setCreatedByUserId(existUser.get());
                        }
                    }
                    return schedule;
                }
        ).collect(Collectors.toList());

        // Set danh sách schedules cho program
        program.setSchedules(schedules);

        // Lưu TrainingProgProgram vào database
        trainingProgProgramRepo.save(program); // Điều này sẽ tự động lưu các TrainingProgSchedule do cascade
    }



    @Override
    @Transactional
    public void update(String programId, List<TrainingProgScheduleUpdateRequest> scheduleUpdates) {
        Optional<TrainingProgProgram> optionalProgram = trainingProgProgramRepo.findById(programId);
        if (optionalProgram.isEmpty()) {
            throw new EntityNotFoundException("Program not found with id: " + programId);
        }

        TrainingProgProgram program = optionalProgram.get();

        for (TrainingProgScheduleUpdateRequest updateRequest : scheduleUpdates) {
            Optional<TrainingProgSchedule> optionalSchedule = trainingProgScheduleRepo.findById(updateRequest.getId());
            if (optionalSchedule.isEmpty()) {
                throw new EntityNotFoundException("Schedule not found with id: " + updateRequest.getId());
            }

            TrainingProgSchedule schedule = optionalSchedule.get();

            Optional<TrainingProgSemester> optionalSemester = trainingProgSemesterRepo.findById(updateRequest.getSemesterId());
            if (optionalSemester.isEmpty()) {
                throw new EntityNotFoundException("Semester not found with id: " + updateRequest.getSemesterId());
            }

            schedule.setSemester(optionalSemester.get());
            trainingProgScheduleRepo.save(schedule);
        }
    }


    @Override
    public PaginationDTO<TrainingProgProgramInfo> getAllTrainingProgPrograms(int page, int limit, String keyword) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<TrainingProgProgram> programPage;

        // Nếu keyword không rỗng, tìm theo keyword, nếu rỗng thì lấy tất cả
        if (keyword != null && !keyword.isEmpty()) {
            programPage = trainingProgProgramRepo.findByKeyword(keyword, pageable);
        } else {
            programPage = trainingProgProgramRepo.findAll(pageable);
        }

        // Chuyển đổi từ TrainingProgProgram sang TrainingProgProgramInfo
        PaginationDTO<TrainingProgProgramInfo> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPage(programPage.getNumber());
        paginationDTO.setTotalElement((int) programPage.getTotalElements());
        paginationDTO.setData(programPage.getContent().stream().map(program -> {
            TrainingProgProgramInfo dto = new TrainingProgProgramInfo();
            dto.setId(program.getId());
            dto.setName(program.getName());
            return dto;
        }).collect(Collectors.toList()));

        return paginationDTO;
    }


    @Override
    public ResponseTrainingProgProgramDTO getTrainingProgProgramById(String id) {

        Optional<TrainingProgProgram> trainingProgProgram = trainingProgProgramRepo.findById(id);
        if (!trainingProgProgram.isPresent()) {
            throw new EntityNotFoundException("Program not found with id: " + id);
        }
        TrainingProgProgram program = trainingProgProgram.get();
        ResponseTrainingProgProgramDTO responseDTO = new ResponseTrainingProgProgramDTO();
        responseDTO.setId(program.getId());
        responseDTO.setName(program.getName());
        responseDTO.setCreateStamp(program.getCreateStamp());
        responseDTO.setLastUpdated(program.getLastUpdated());

        List<TrainingProgSchedule> schedules = program.getSchedules();
        List<TrainingProgCourseDetail> courseDetails = schedules.stream().map(
                schedule -> {
                    TrainingProgCourseDetail dto = new TrainingProgCourseDetail();
                    // Lay id de sau update
                    dto.setIdSchedule(schedule.getId());

                    // Lay thong tin can
                    TrainingProgCourse course = schedule.getCourse();
                    dto.setId(course.getId());
                    dto.setCourseName(course.getCourseName());
                    dto.setCredit(course.getCredit());
                    dto.setSemester(schedule.getSemester().getId());
                    List<String> prerequisiteId = course.getPrerequisites().stream()
                            .map(prerequisite -> prerequisite.getPrerequisiteCourse().getId())
                            .collect(Collectors.toList());
                    dto.setPrerequisites(prerequisiteId);

                    return dto;

                }
        ).toList();
        responseDTO.setSchedules(courseDetails);

        return responseDTO;
    }

    @Override
    @Transactional
    public void addCoursesToProgram(String programId, List<String> courseIds) {
        // Tìm chương trình đào tạo
        Optional<TrainingProgProgram> optionalProgram = trainingProgProgramRepo.findById(programId);
        if (optionalProgram.isEmpty()) {
            throw new EntityNotFoundException("Program not found with id: " + programId);
        }

        TrainingProgProgram program = optionalProgram.get();
        Optional<User> existUser = userRepo.findById("dungpq");


        List<TrainingProgSchedule> newSchedules = new ArrayList<>();

        for (String courseId : courseIds) {
            // Tìm môn học dựa
            Optional<TrainingProgCourse> optionalCourse = trainingProgCourseRepo.findById(courseId);
            if (optionalCourse.isEmpty()) {
                throw new EntityNotFoundException("Course not found with id: " + courseId);
            }

            TrainingProgCourse course = optionalCourse.get();

            TrainingProgSchedule schedule = new TrainingProgSchedule();
            schedule.setCourse(course);
            schedule.setProgram(program);
            schedule.setStatus("active");
            if (existUser.isPresent()) {
                schedule.setCreatedByUserId(existUser.get());
            }

            newSchedules.add(schedule);
        }

        trainingProgScheduleRepo.saveAll(newSchedules);

        program.getSchedules().addAll(newSchedules);

        trainingProgProgramRepo.save(program);

    }


}
