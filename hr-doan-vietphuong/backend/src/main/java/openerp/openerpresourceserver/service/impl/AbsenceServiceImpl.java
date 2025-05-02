package openerp.openerpresourceserver.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.absence.AbsenceQueryRequest;
import openerp.openerpresourceserver.dto.request.absence.AbsenceRequest;
import openerp.openerpresourceserver.dto.request.absence.ManagerAbsenceRequest;
import openerp.openerpresourceserver.dto.response.absence.AbsenceResponse;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.enums.*;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.AbsenceRepository;
import openerp.openerpresourceserver.repo.AbsenceTypeRepository;
import openerp.openerpresourceserver.repo.AttendanceReportRepository;
import openerp.openerpresourceserver.repo.EmployeeRepository;
import openerp.openerpresourceserver.repo.specification.AbsenceSpecification;
import openerp.openerpresourceserver.service.AbsenceService;
import openerp.openerpresourceserver.service.MailService;
import openerp.openerpresourceserver.service.SyncAttendanceService;
import openerp.openerpresourceserver.util.Constants;
import openerp.openerpresourceserver.util.DateUtil;
import openerp.openerpresourceserver.util.ObjectUtil;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static openerp.openerpresourceserver.util.Constants.ABSENCE;
import static openerp.openerpresourceserver.util.Constants.NO_ABSENCE;
import static openerp.openerpresourceserver.util.DateUtil.*;

@Service
@RequiredArgsConstructor
public class AbsenceServiceImpl implements AbsenceService {
    private final MailService mailService;
    private final SyncAttendanceService syncAttendanceService;
    private final AbsenceRepository absenceRepository;
    private final AbsenceTypeRepository absenceTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceReportRepository attendanceReportRepository;

    @Value("#{'${email.list}'.split(',')}")
    private List<String> ccList;

    @Value("${manager.approval.link}")
    private String approvalLink;

    @Override
    public Page<AbsenceResponse> getAbsencesByUserLogin(AbsenceQueryRequest dto, PagingRequest pagingRequest) {
        String email = SecurityUtil.getUserEmail();
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.by(Sort.Direction.DESC, "updatedAt"));
        Specification<Absence> specification = Specification
                .where(AbsenceSpecification.hasUserEmail(email))
                .and(AbsenceSpecification.hasStatus(dto.getStatus()))
                .and(AbsenceSpecification.hasType(AbsenceTypeEnum.ABSENCE.ordinal()))
                .and(AbsenceSpecification.hasCreatedAtBetween(dto.getFrom(), dto.getTo()));
        Page<Absence> page = absenceRepository.findAll(specification, pageable);
        return page.map(this::convertToAbsenceResponse);
    }

    @Override
    public List<AbsenceType> getAbsencesTypes() {
        Employee employee = employeeRepository.findByEmail(SecurityUtil.getUserEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        List<AbsenceType> absenceTypes = absenceTypeRepository.findByTypeAndStatusOrderById(AbsenceTypeEnum.ABSENCE.ordinal(), StatusEnum.ACTIVE.ordinal());
        if (employee.getPosition().getIsOfficial()) {
            return absenceTypes;
        }
        return absenceTypes.stream()
                .filter(absenceType -> !absenceType.getHasValue())
                .toList();
    }

    @Override
    public AbsenceResponse addAbsence(AbsenceRequest dto) throws MessagingException, BadRequestException {
        AbsenceType absenceType = absenceTypeRepository.findById(dto.getAbsenceTypeId())
                .orElseThrow(() -> new NotFoundException("Absence type not found"));
        Employee employee = employeeRepository.findByEmail(SecurityUtil.getUserEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        if (Boolean.TRUE.equals(!employee.getPosition().getIsOfficial()) && Boolean.TRUE.equals(absenceType.getHasValue())) {
            throw new BadRequestException("Unofficial employee cannot make annual leave");
        }
        Employee lead = null;
        if (!employee.getPosition().getIsLead()) {
            lead = employeeRepository.findById(employee.getOrganization().getLeadId())
                    .orElseThrow(() -> new NotFoundException("Lead not found"));
        } else {
            Organization currentOrg = employee.getOrganization().getParent();
            while (currentOrg != null) {
                Employee orgLead = employeeRepository.findById(currentOrg.getLeadId())
                        .orElseThrow(() -> new NotFoundException("Lead not found"));
                if (!orgLead.getId().equals(employee.getId())) {
                    lead = orgLead;
                    break;
                }
                currentOrg = currentOrg.getParent();
            }
            if (lead == null) {
                throw new NotFoundException("No other lead found in ancestor organizations");
            }
        }
        List<LocalDateTime> timeList = calculateStartTimeAndEndTimeForAbsence(dto, employee.getAttendanceRange());
        double userAnnualLeave = employee.getAnnualLeave();
        double absenceCount = calculateAbsenceCount(timeList.get(0), timeList.get(1), employee.getAttendanceRange());
        if (Boolean.TRUE.equals(absenceType.getHasValue()) && absenceType.getCode().equals(ABSENCE)) {
            if (absenceCount > userAnnualLeave)
                throw new BadRequestException("Cannot make annual leave! You only have " + userAnnualLeave + " left.");
        }
        List<String> optionalCcList = new ArrayList<>();
        if (dto.getOrganizationIdList() != null) {
            List<Employee> employees = employeeRepository.findByOrganizations(dto.getOrganizationIdList());
            optionalCcList.addAll(employees.stream()
                    .map(Employee::getEmail)
                    .toList()
            );
        }
        if (dto.getCcList() != null) {
            optionalCcList.addAll(dto.getCcList());
        }
        optionalCcList.add(employee.getEmail());
        optionalCcList.remove(lead.getEmail());
        optionalCcList = optionalCcList.stream().distinct().toList();
        Absence absence = Absence.builder()
                .employee(employee)
                .absenceType(absenceType)
                .startTime(timeList.get(0))
                .endTime(timeList.get(1))
                .type(AbsenceTypeEnum.ABSENCE.ordinal())
                .note(dto.getNote())
                .length(absenceCount)
                .updatedBy(SecurityUtil.getUserEmail())
                .status(AbsenceStatus.PENDING.ordinal())
                .leadId(lead.getId())
                .ccList(optionalCcList)
                .build();
        absence = absenceRepository.save(absence);
        sendEmail(absence, lead.getEmail());
        return convertToAbsenceResponse(absence);
    }

    @Override
    public Page<AbsenceResponse> getAbsencesByProperties(AbsenceQueryRequest dto, PagingRequest pagingRequest) {
        String email = SecurityUtil.getUserEmail();
        Employee lead = employeeRepository.findByEmailAndPositionIsLead(email, true)
                .orElseThrow(() -> new NotFoundException("Lead not found or you are not lead"));
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.by(Sort.Direction.DESC, "updatedAt"));
        Specification<Absence> specification = Specification
                .where(AbsenceSpecification.hasStatus(dto.getStatus()))
                .and(AbsenceSpecification.hasUserEmail(dto.getEmail()))
                .and(AbsenceSpecification.hasType(AbsenceTypeEnum.ABSENCE.ordinal()))
                .and(AbsenceSpecification.hasCreatedAtBetween(dto.getFrom(), dto.getTo()))
                .and(AbsenceSpecification.hasLeadId(lead.getId()))
                .and(AbsenceSpecification.hasNotUserEmail(lead.getEmail()));
        Page<Absence> page = absenceRepository.findAll(specification, pageable);
        return page.map(this::covertToAbsenceResponseForManager);
    }

    @Override
    @Transactional
    public List<AbsenceResponse> approveAbsences(ManagerAbsenceRequest dto) throws BadRequestException {
        List<Absence> result = new ArrayList<>();
        for (long id : dto.getIdList()) {
            Absence absence = absenceRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Absence not found"));
            if (absence.getStatus() != AbsenceStatus.PENDING.ordinal()) continue;
            AbsenceType absenceType = absence.getAbsenceType();
            Employee employee = absence.getEmployee();
            if (Boolean.TRUE.equals(absenceType.getHasValue()) && absenceType.getCode().equals(ABSENCE)) {
                double decreasedAnnualLeave = employee.getAnnualLeave() - absence.getLength();
                if (decreasedAnnualLeave < 0) {
                    throw new BadRequestException("Not enough annual leave for this employee");
                }
                employee.setAnnualLeave(decreasedAnnualLeave);
                employeeRepository.save(employee);
            }
            AttendanceRange attendanceRange = employee.getAttendanceRange();
            LocalTime startTime = attendanceRange.getStartTime();
            LocalTime endTime = attendanceRange.getEndTime();
            LocalDate startDate = absence.getStartTime().toLocalDate();
            LocalDate endDate = absence.getEndTime().toLocalDate();
            List<Integer> dates = DateUtil.getDatesInDateRange(startDate, endDate);

            for (int i = 0; i < dates.size(); i++) {
                int date = dates.get(i);
                if (DateUtil.isWeekend(date)) continue;
                AttendanceReport attendanceReport;
                Optional<AttendanceReport> attendanceReportOptional = attendanceReportRepository
                        .findByEmployeeIdAndDate(employee.getEmployeeId(), date);
                ObjectUtil.AttendanceRangeTime attendanceRangeTime =
                        determineAttendanceRangeTime(
                                i,
                                dates.size() - 1,
                                absence.getStartTime(),
                                absence.getEndTime(),
                                LocalDateTime.of(DateUtil.convertIntegerToLocalDate(date), startTime),
                                LocalDateTime.of(DateUtil.convertIntegerToLocalDate(date), endTime)
                        );
                LocalDateTime absenceStartTime = attendanceRangeTime.startTime();
                LocalDateTime absenceEndTime = attendanceRangeTime.endTime();
                double leaveTime = absenceType.getCode().equals(NO_ABSENCE)
                        ? 0.0
                        : syncAttendanceService.computeAttendanceTime(absenceStartTime.toLocalTime(), absenceEndTime.toLocalTime(), attendanceRange);
                attendanceReport = attendanceReportOptional.orElseGet(() -> AttendanceReport
                        .builder()
                        .employeeId(employee.getEmployeeId())
                        .attendanceRangeId(attendanceRange.getId())
                        .date(date)
                        .attendanceTime(0.0)
                        .build());
                attendanceReport.setLeaveTime(leaveTime);
                attendanceReport.setStatus(absence.getAbsenceType().getId().intValue() + 1000);
                attendanceReportRepository.save(attendanceReport);
            }
            // save absence
            absence.setStatus(AbsenceStatus.APPROVED.ordinal());
            absence.setUpdatedBy(SecurityUtil.getUserEmail());
            result.add(absenceRepository.save(absence));
        }
        return result.stream()
                .map(this::covertToAbsenceResponseForManager)
                .toList();
    }

    @Override
    public List<AbsenceResponse> rejectAbsences(ManagerAbsenceRequest dto) {
        List<Absence> result = new ArrayList<>();
        for (long id : dto.getIdList()) {
            Absence absence = absenceRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Absence not found"));
            absence.setStatus(AbsenceStatus.REJECTED.ordinal());
            absence.setUpdatedBy(SecurityUtil.getUserEmail());
            result.add(absenceRepository.save(absence));
        }
        return result
                .stream()
                .map(this::covertToAbsenceResponseForManager)
                .toList();
    }

    private AbsenceResponse convertToAbsenceResponse(Absence absence) {
        return AbsenceResponse.builder()
                .email(absence.getEmployee().getEmail())
                .startTime(amOrPm(absence, true))
                .startDate(absence.getStartTime().toLocalDate())
                .endTime(amOrPm(absence, false))
                .endDate(absence.getEndTime().toLocalDate())
                .length(absence.getLength())
                .type(absence.getType())
                .subType(absence.getAbsenceType().getDescription())
                .note(absence.getNote())
                .status(absence.getStatus())
                .emailStatus(absence.getEmailStatus())
                .build();
    }

    private AbsenceResponse covertToAbsenceResponseForManager(Absence absence) {
        return AbsenceResponse.builder()
                .id(absence.getId())
                .email(absence.getEmployee().getEmail())
                .startTime(amOrPm(absence, true))
                .startDate(absence.getStartTime().toLocalDate())
                .endTime(amOrPm(absence, false))
                .endDate(absence.getEndTime().toLocalDate())
                .type(absence.getType())
                .subType(absence.getAbsenceType().getDescription())
                .note(absence.getNote())
                .status(absence.getStatus())
                .emailStatus(absence.getEmailStatus())
                .build();
    }

    private double calculateAbsenceCount(LocalDateTime start, LocalDateTime end, AttendanceRange attendanceRange) {
        double absenceCount = 0;
        LocalTime startLunchRange;
        LocalTime endLunchRange;
        double breakTimeInHours;
        if (attendanceRange != null && attendanceRange.getStatus().equals(StatusEnum.ACTIVE.ordinal())) {
            startLunchRange = attendanceRange.getStartLunch();
            endLunchRange = attendanceRange.getEndLunch();
        } else {
            startLunchRange = START_LUNCH;
            endLunchRange = END_LUNCH;
        }
        breakTimeInHours = getDifferenceInHours(startLunchRange, endLunchRange);
        // Calculate date and time differences
        long dateDistance = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        // Calculate time difference considering lunch break
        boolean isTimeRangeIncludeLunch = startTime.isBefore(startLunchRange) && endTime.isAfter(endLunchRange);
        double timeDistance = getDifferenceInHours(startTime, endTime);
        timeDistance = isTimeRangeIncludeLunch ? timeDistance - breakTimeInHours : timeDistance;

        // Determine absence count
        absenceCount = dateDistance - countWeekendsBetween(start.toLocalDate(), end.toLocalDate()); // 1 day absence for each full day
        assert attendanceRange != null;
        if (timeDistance != attendanceRange.getFullHours()) {
            absenceCount += 0.5;
        } else {
            absenceCount += 1;
        }
        return Math.max(0, absenceCount);
    }

    private List<LocalDateTime> calculateStartTimeAndEndTimeForAbsence(AbsenceRequest dto, AttendanceRange attendanceRange) {
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (Objects.equals(dto.getStartTime(), dto.getEndTime())) {
            List<LocalTime> shift = calculateStartTimeAndEndTimeForShift(attendanceRange, dto.getStartTime());
            startTime = dto.getStartDate().atTime(shift.get(0));
            endTime = dto.getEndDate().atTime(shift.get(1));
        } else {
            List<LocalTime> shift1 = calculateStartTimeAndEndTimeForShift(attendanceRange, dto.getStartTime());
            List<LocalTime> shift2 = calculateStartTimeAndEndTimeForShift(attendanceRange, dto.getEndTime());
            startTime = dto.getStartDate().atTime(shift1.get(0));
            endTime = dto.getEndDate().atTime(shift2.get(1));
        }
        return Arrays.asList(startTime, endTime);
    }

    private List<LocalTime> calculateStartTimeAndEndTimeForShift(AttendanceRange attendanceRange, int type) {
        LocalTime startTimeRange;
        LocalTime endTimeRange;
        if (type == AbsenceTimeEnum.AM.ordinal()) {
            if (attendanceRange != null && attendanceRange.getStatus().equals(StatusEnum.ACTIVE.ordinal())) {
                startTimeRange = attendanceRange.getStartTime();
                endTimeRange = attendanceRange.getStartLunch();
            } else {
                startTimeRange = START_TIME;
                endTimeRange = START_LUNCH;
            }
        } else {
            if (attendanceRange != null && attendanceRange.getStatus().equals(StatusEnum.ACTIVE.ordinal())) {
                startTimeRange = attendanceRange.getEndLunch();
                endTimeRange = attendanceRange.getEndTime();
            } else {
                startTimeRange = END_LUNCH;
                endTimeRange = END_TIME;
            }
        }
        return Arrays.asList(startTimeRange, endTimeRange);
    }

    private String amOrPm(Absence absence, boolean isStart) {
        if (isStart) {
            if (absence.getStartTime().toLocalTime().equals(absence.getEmployee().getAttendanceRange().getStartTime())) {
                return "Sáng";
            } else {
                return "Chiều";
            }
        } else {
            if (absence.getEndTime().toLocalTime().equals(absence.getEmployee().getAttendanceRange().getEndTime())) {
                return "Chiều";
            } else {
                return "Sáng";
            }
        }
    }

    private ObjectUtil.AttendanceRangeTime determineAttendanceRangeTime(
            int i,
            int last,
            LocalDateTime absenceStartTime,
            LocalDateTime absenceEndTime,
            LocalDateTime rangeStartTime,
            LocalDateTime rangeEndTime) {
        if (0 == last) {
            return new ObjectUtil.AttendanceRangeTime(absenceStartTime, absenceEndTime);
        }
        if (i != 0 && i != last) {
            return new ObjectUtil.AttendanceRangeTime(rangeStartTime, rangeEndTime);
        }
        if (i == 0) {
            return new ObjectUtil.AttendanceRangeTime(absenceStartTime, rangeEndTime);
        }
        return new ObjectUtil.AttendanceRangeTime(rangeStartTime, absenceEndTime);
    }

    private void sendEmail(Absence absence, String leadEmail) {
        String subject = "[Chấm công] "
                + absence.getEmployee().getFullName()
                + "_"
                + absence.getEmployee().getPosition().getName()
                + "_"
                + absence.getAbsenceType().getDescription()
                + " "
                + absence.getStartTime().format(Constants.DATE_FORMATTER);

        Employee lead = employeeRepository.findByEmail(leadEmail).get();
        List<String> copiedCcList = new ArrayList<>(ccList);
        copiedCcList.addAll(absence.getCcList());
        copiedCcList = copiedCcList.stream().distinct().toList();

        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();
        variables.put("fullName", absence.getEmployee().getFullName());
        variables.put("absenceType", absence.getAbsenceType().getDescription());
        variables.put("leadName", lead.getFullName());
        variables.put("userEmail", absence.getEmployee().getEmail());
        variables.put("leadEmail", leadEmail);
        variables.put("title", absence.getEmployee().getPosition().getName());
        variables.put("note", absence.getNote());
        variables.put("startTime", amOrPm(absence, true));
        variables.put("startDate", absence.getStartTime().format(Constants.DATE_FORMATTER));
        variables.put("endTime", amOrPm(absence, false));
        variables.put("endDate", absence.getEndTime().format(Constants.DATE_FORMATTER));
        variables.put("approvalLink", approvalLink);
        context.setVariables(variables);
        final List<String> finalCopiedCcList = copiedCcList;
        Thread emailThread = new Thread(() -> {
            try {
                mailService.sendEmailWithHtmlTemplate(leadEmail, finalCopiedCcList, subject, "absence-mail-template", context);
                absence.setEmailStatus(EmailStatusEnum.SUCCESS.ordinal());
            } catch (Exception e) {
                absence.setEmailStatus(EmailStatusEnum.FAILED.ordinal());
            }
        });
        emailThread.start();
    }
}
