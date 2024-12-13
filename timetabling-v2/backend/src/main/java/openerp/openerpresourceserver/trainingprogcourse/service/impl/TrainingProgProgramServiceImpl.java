package openerp.openerpresourceserver.trainingprogcourse.service.impl;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.model.entity.User;
import openerp.openerpresourceserver.generaltimetabling.repo.ScheduleRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.UserRepo;
import openerp.openerpresourceserver.trainingprogcourse.config.CustomException;
import openerp.openerpresourceserver.trainingprogcourse.dto.*;
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
    private final TrainingProgCourseServiceImpl trainingProgCourseServiceImpl;

    public TrainingProgProgramServiceImpl(TrainingProgProgramRepo trainingProgProgramRepo, TrainingProgScheduleRepo trainingProgScheduleRepo, TrainingProgSemesterRepo trainingProgSemesterRepo, TrainingProgCourseRepo trainingProgCourseRepo, UserRepo userRepo, ScheduleRepo scheduleRepo, TrainingProgCourseServiceImpl trainingProgCourseServiceImpl) {
        this.trainingProgProgramRepo = trainingProgProgramRepo;
        this.trainingProgScheduleRepo = trainingProgScheduleRepo;
        this.trainingProgSemesterRepo = trainingProgSemesterRepo;
        this.trainingProgCourseRepo = trainingProgCourseRepo;
        this.userRepo = userRepo;
        this.trainingProgCourseServiceImpl = trainingProgCourseServiceImpl;
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
        trainingProgProgramRepo.save(program);
    }


    @Override
    @Transactional
    public void update(List<TrainingProgScheduleUpdateRequest> scheduleUpdates) throws CustomException {
        for (TrainingProgScheduleUpdateRequest scheduleUpdate : scheduleUpdates) {
            Optional<TrainingProgSchedule> trainingProgScheduleOptional = trainingProgScheduleRepo.findByProgramIdAndCourseId(scheduleUpdate.getProgramId(), scheduleUpdate.getCourseId());

            if (trainingProgScheduleOptional.isEmpty()) {
                throw new CustomException("Không tìm thấy lịch học cho chương trình và môn học với ID: " + scheduleUpdate.getCourseId());
            }

            TrainingProgSchedule trainingProgSchedule = trainingProgScheduleOptional.get();
            Optional<TrainingProgSemester> trainingProgSemester = trainingProgSemesterRepo.findById(scheduleUpdate.getSemesterId());

            if (trainingProgSemester.isPresent()) {
                trainingProgSchedule.setSemester(trainingProgSemester.get());
            } else {
                throw new CustomException("Không tìm thấy  cho chương trình và môn học với ID: " + scheduleUpdate.getCourseId());
            }

            trainingProgScheduleRepo.save(trainingProgSchedule);
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
                    if(schedule.getSemester() != null){
                    dto.setSemester(schedule.getSemester().getId());}
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

        // Lấy danh sách các môn học đã có trong chương trình
        List<String> existingCourseIds = program.getSchedules().stream()
                .map(schedule -> schedule.getCourse().getId())
                .collect(Collectors.toList());

        List<TrainingProgSchedule> newSchedules = new ArrayList<>();

        for (String courseId : courseIds) {
            // Kiểm tra xem môn học đã có trong chương trình chưa
            if (existingCourseIds.contains(courseId)) {
                continue;
            }

            // Tìm môn học dựa trên courseId
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

        // Lưu các schedule mới vào database nếu có
        if (!newSchedules.isEmpty()) {
            trainingProgScheduleRepo.saveAll(newSchedules);
            program.getSchedules().addAll(newSchedules);
            trainingProgProgramRepo.save(program);
        }
    }


    @Override
    public List<String> getListCourseProgram(String programId) {
        List<String> courseIds = new ArrayList<>();

        // kiem tra chuong trinh co ton tai
        Optional<TrainingProgProgram> trainingProgProgramOptional = trainingProgProgramRepo.findById(programId);
        if (trainingProgProgramOptional.isPresent()) {

            TrainingProgProgram trainingProgProgram = trainingProgProgramOptional.get();
            courseIds = trainingProgProgram.getSchedules().stream().map(schedule -> schedule.getCourse().getId())
                    .collect(Collectors.toList());


        } else {
            throw new EntityNotFoundException("Course not found with id: " + programId);
        }

        return courseIds;

    }

    @Override
    public List<ResponseTrainingProgCourse> getAvailableCourse(String programId) {
        List<ResponseTrainingProgCourse> allCourses = trainingProgCourseServiceImpl.getAll();

        Optional<TrainingProgProgram> optionalProgram = trainingProgProgramRepo.findById(programId);
        if (optionalProgram.isEmpty()) {
            throw new EntityNotFoundException("Program not found with id: " + programId);
        }

        TrainingProgProgram program = optionalProgram.get();

        List<String> programCourseIds = program.getSchedules().stream()
                .map(schedule -> schedule.getCourse().getId())
                .toList();

        // 3. Lọc ra các môn học không có trong chương trình
        List<ResponseTrainingProgCourse> availableCourses = allCourses.stream()
                .filter(course -> !programCourseIds.contains(course.getId())) // Lọc những môn học chưa có trong chương trình
                .collect(Collectors.toList());

        return availableCourses;
    }


}
