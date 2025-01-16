package openerp.openerpresourceserver.trainingprogcourse.service.impl;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.model.entity.User;
import openerp.openerpresourceserver.generaltimetabling.repo.ScheduleRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.UserRepo;
import openerp.openerpresourceserver.trainingprogcourse.algorithm.CourseScheduler;
import openerp.openerpresourceserver.trainingprogcourse.config.CustomException;
import openerp.openerpresourceserver.trainingprogcourse.dto.*;
import openerp.openerpresourceserver.trainingprogcourse.dto.request.*;
import openerp.openerpresourceserver.trainingprogcourse.enity.*;
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

import java.util.*;
import java.util.stream.Collectors;

import static openerp.openerpresourceserver.trainingprogcourse.algorithm.CourseScheduler.moveCourse;

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
        program.setSemesterCount(requestDTO.getSemesterCount());

        // Tạo danh sách TrainingProgSchedule từ requestDTO
        List<TrainingProgSchedule> schedules = requestDTO.getCourses().stream().map(
                request -> {
                    TrainingProgSchedule schedule = new TrainingProgSchedule();
                    Optional<TrainingProgCourse> optionalTrainingProgCourse = trainingProgCourseRepo.findById(request);
                    if (optionalTrainingProgCourse.isPresent()) {
                        TrainingProgCourse course = optionalTrainingProgCourse.get();
                        schedule.setCourse(course);
                        schedule.setProgram(program);
                        schedule.setStatus("active");
                        schedule.setId(UUID.randomUUID());
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
            throw new EntityNotFoundException("Mã chương trình không tồn tại " + id);
        }
        TrainingProgProgram program = trainingProgProgram.get();
        ResponseTrainingProgProgramDTO responseDTO = new ResponseTrainingProgProgramDTO();
        responseDTO.setId(program.getId());
        responseDTO.setName(program.getName());
        responseDTO.setCreateStamp(program.getCreateStamp());
        responseDTO.setLastUpdated(program.getLastUpdated());
        responseDTO.setSemesterCount(program.getSemesterCount());

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
            throw new EntityNotFoundException("Mã chương trình không tồn tại  " + programId);
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
                throw new EntityNotFoundException("Học phần không tìm thấy với id : " + courseId);
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
            courseIds = trainingProgProgram.getSchedules().stream().map(
                    schedule -> schedule.getCourse().getId())
                    .collect(Collectors.toList());

        } else {
            throw new EntityNotFoundException("Học phần không tìm thấy với id : " + programId);
        }

        return courseIds;

    }

    @Override
    public List<ResponseTrainingProgCourse> getAvailableCourse(String programId) {
        List<ResponseTrainingProgCourse> allCourses = trainingProgCourseServiceImpl.getAll();

        Optional<TrainingProgProgram> optionalProgram = trainingProgProgramRepo.findById(programId);
        if (optionalProgram.isEmpty()) {
            throw new EntityNotFoundException("Mã chương trình không tồn tại  " + programId);
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

    @Override
    public  List<ResponseProgramAlterDTO> courseScheduler(String programId) {
        Optional<TrainingProgProgram> optionalProgram = trainingProgProgramRepo.findById(programId);
        if (optionalProgram.isEmpty()) {
            throw new EntityNotFoundException("Mã chương trình không tồn tại  " + programId);
        }

        TrainingProgProgram program = optionalProgram.get();

        List<TrainingProgSchedule> schedules = program.getSchedules();

        List<TrainingProgCourse> courses = schedules.stream().map(trainingProgSchedule ->{
            TrainingProgCourse course = trainingProgSchedule.getCourse();
            return course;
        } ).toList();

        long semesterCountLong = program.getSemesterCount();
        int semesterCount = (int) semesterCountLong; // Ép kiểu từ long sang int

        CourseScheduler courseScheduler = new CourseScheduler();
        Map<Long, List<String>> linh = courseScheduler.scheduleCourses(courses,semesterCount);

        List<ResponseProgramAlterDTO> responseList = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : linh.entrySet()) {
            Long id = entry.getKey();
            List<String> course1 = entry.getValue();

            for (String course : course1) {
                ResponseProgramAlterDTO dto = new ResponseProgramAlterDTO();
                dto.setId(course);
                dto.setSemester(String.valueOf(id+1));
                responseList.add(dto);
            }
        }

        return responseList;

    }

    @Override
    public Boolean updateSemesterCount(RequestSemesterCountDTO requestSemesterCountDTO) {

        String programId = requestSemesterCountDTO.getProgramId();
        long semester = requestSemesterCountDTO.getSemesterCount();
        Optional<TrainingProgProgram> optionalProgram = trainingProgProgramRepo.findById(programId);
        if (optionalProgram.isEmpty()) {
            throw new EntityNotFoundException("Mã chương trình không tồn tại  " + programId);
        }

        TrainingProgProgram program = optionalProgram.get();
        program.setSemesterCount(semester);
        trainingProgProgramRepo.save(program);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteCourse(RequestDeleteTrainingProgProgramDTO request) {
        String programId = request.getProgramId();
        List<String> courseIds = request.getCourseIds();
        Optional<TrainingProgProgram> optionalProgram = trainingProgProgramRepo.findById(programId);
        if (optionalProgram.isEmpty()) {
            throw new EntityNotFoundException("Mã chương trình không tồn tại  " + programId);
        }

        TrainingProgProgram program = optionalProgram.get();

        for(String courseId : courseIds) {
          Optional <TrainingProgSchedule> trainingProgSchedule = trainingProgScheduleRepo.findByProgramIdAndCourseId(programId,courseId);
            trainingProgSchedule.ifPresent(trainingProgScheduleRepo::delete);

        }
        return true;
    }

    @Override
    public List<ResponseCourseChangeDTO> changeCourse(RequestChangeCourseDTO request) {
        String programId = request.getProgramId();
        int targetSemester = (int) request.getTargetSemester();
        String targetCourseId = request.getCourseId();

        // Kiểm tra xem chương trình có tồn tại không
        Optional<TrainingProgProgram> optionalProgram = trainingProgProgramRepo.findById(programId);
        if (optionalProgram.isEmpty()) {
            throw new EntityNotFoundException("Mã chương trình không tồn tại  " + programId);
        }
        TrainingProgProgram program = optionalProgram.get();
        List<TrainingProgCourse> courses = getCoursesFromProgram(program);
        int totalSemesters = program.getSemesterCount().intValue();
        Map<String, Integer> initialSemesters = buildInitialSemesters(program);
        CourseScheduler courseScheduler = new CourseScheduler();

        Map<String, Integer> movedCourses = moveCourse(targetCourseId, targetSemester, courses, initialSemesters, totalSemesters);


        // Lọc và chỉ giữ các phần tử từ movedCourses mà giá trị của chúng khác với initialSemesters
        return movedCourses.entrySet().stream()
                .filter(entry -> !entry.getValue().equals(initialSemesters.get(entry.getKey())))
                .map(entry -> new ResponseCourseChangeDTO(entry.getKey(), String.valueOf(entry.getValue())))
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public int deletePrograms(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Danh sách không được để trống!");
        }

        // Fetch all programs by IDs
        List<TrainingProgProgram> programs = trainingProgProgramRepo.findAllById(ids);
        if (programs.isEmpty()) {
            throw new IllegalArgumentException("Danh sách không được để trống!");
        }

        // Delete schedules associated with these programs
        List<TrainingProgSchedule> schedulesToDelete = programs.stream()
                .flatMap(program -> program.getSchedules().stream())
                .collect(Collectors.toList());
        if (!schedulesToDelete.isEmpty()) {
            trainingProgScheduleRepo.deleteAll(schedulesToDelete);
        }

        // Delete programs
        trainingProgProgramRepo.deleteAll(programs);

        return programs.size();
    }



    private Map<String, Integer> buildInitialSemesters(TrainingProgProgram program) {
        Map<String, Integer> initialSemesters = new HashMap<>();


        for (TrainingProgSchedule schedule : program.getSchedules()) {

            Integer semesterId = Integer.valueOf(schedule.getSemester().getId());
            TrainingProgCourse course = schedule.getCourse();
            initialSemesters.put(course.getId(), semesterId);

        }
        return initialSemesters;
    }

    public List<TrainingProgCourse> getCoursesFromProgram(TrainingProgProgram program) {

        return program.getSchedules().stream()
                .map(TrainingProgSchedule::getCourse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



}
