package openerp.openerpresourceserver.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.AdditionalCheckInQueryRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.AdditionalCheckInRequest;
import openerp.openerpresourceserver.dto.request.additionalCheckIn.ManagerAdditionalCheckInRequest;
import openerp.openerpresourceserver.dto.response.additionalCheckIn.AdditionalCheckInResponse;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.enums.AbsenceStatus;
import openerp.openerpresourceserver.enums.AbsenceTypeEnum;
import openerp.openerpresourceserver.enums.EmailStatusEnum;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.AbsenceRepository;
import openerp.openerpresourceserver.repo.AbsenceTypeRepository;
import openerp.openerpresourceserver.repo.AttendanceReportRepository;
import openerp.openerpresourceserver.repo.EmployeeRepository;
import openerp.openerpresourceserver.repo.specification.AbsenceSpecification;
import openerp.openerpresourceserver.service.AdditionalCheckInService;
import openerp.openerpresourceserver.service.MailService;
import openerp.openerpresourceserver.service.SyncAttendanceService;
import openerp.openerpresourceserver.util.Constants;
import openerp.openerpresourceserver.util.DateUtil;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdditionalCheckInServiceImpl implements AdditionalCheckInService {
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
    public Page<AdditionalCheckInResponse> getAdditionalCheckInByUserLogin(final AdditionalCheckInQueryRequest dto, final PagingRequest pagingRequest) {
        String email = SecurityUtil.getUserEmail();
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Absence> specification = Specification
                .where(AbsenceSpecification.hasUserEmail(email))
                .and(AbsenceSpecification.hasStatus(dto.getStatus()))
                .and(AbsenceSpecification.hasType(AbsenceTypeEnum.ATTENDANCE.ordinal()))
                .and(AbsenceSpecification.hasCreatedAtBetween(dto.getFrom(), dto.getTo()));
        Page<Absence> page = absenceRepository.findAll(specification, pageable);
        return page.map(this::convertToAdditionalCheckInResponse);
    }

    @Override
    public List<AbsenceType> getAdditionalCheckInTypes() {
        return absenceTypeRepository.findByTypeAndStatusOrderById(AbsenceTypeEnum.ATTENDANCE.ordinal(), StatusEnum.ACTIVE.ordinal());
    }

    @Override
    public AdditionalCheckInResponse addAdditionalCheckIn(final AdditionalCheckInRequest dto) throws MessagingException, BadRequestException {
        AbsenceType absenceType = absenceTypeRepository.findById(dto.getAbsenceTypeId())
                .orElseThrow(() -> new NotFoundException("Absence type not found"));
        Employee employee = employeeRepository.findByEmail(SecurityUtil.getUserEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
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

        // Check if any additional check-in exists
        List<Absence> absenceList = absenceRepository.getAbsenceInDateRange(
                SecurityUtil.getUserEmail(),
                AbsenceTypeEnum.ATTENDANCE.ordinal(),
                List.of(AbsenceStatus.PENDING.ordinal(), AbsenceStatus.APPROVED.ordinal()),
                dto.getDate().atTime(LocalTime.MIN),
                dto.getDate().atTime(LocalTime.MAX)
        );
        if (absenceList.size() > 0) {
            throw new BadRequestException("You have created additional check-in request for this date already");
        }

        // Calculate attendance length
        int morningForget = dto.getCheckIn() != null ? 1 : 0;
        int afternoonForget = dto.getCheckOut() != null ? 1 : 0;
        double attendanceForgetLength = 1.0 * (morningForget + afternoonForget);

        // Create cc list
        List<String> optionalCcList = new ArrayList<>();
        if (dto.getOrganizationIdList() != null) {
            List<Employee> employees = employeeRepository.findByOrganizations(dto.getOrganizationIdList());
            optionalCcList.addAll(employees.stream()
                    .map(Employee::getEmail)
                    .toList()
            );
        } else if (dto.getCcList() != null) {
            optionalCcList.addAll(dto.getCcList());
        }
        optionalCcList.add(employee.getEmail());
        optionalCcList.remove(lead.getEmail());
        optionalCcList = optionalCcList.stream().distinct().toList();
        Absence absence = Absence.builder()
                .employee(employee)
                .absenceType(absenceType)
                .startTime(dto.getCheckIn() != null ? dto.getDate().atTime(dto.getCheckIn()) : null)
                .endTime(dto.getCheckOut() != null ? dto.getDate().atTime(dto.getCheckOut()) : null)
                .length(attendanceForgetLength)
                .type(AbsenceTypeEnum.ATTENDANCE.ordinal())
                .note(dto.getNote())
                .updatedBy(SecurityUtil.getUserEmail())
                .status(AbsenceStatus.PENDING.ordinal())
                .leadId(lead.getId())
                .ccList(optionalCcList)
                .build();
        absence = absenceRepository.save(absence);
        sendEmail(absence, dto, lead.getEmail());
        return convertToAdditionalCheckInResponse(absence);
    }

    @Override
    public Page<AdditionalCheckInResponse> getAdditionalCheckInByProperties(AdditionalCheckInQueryRequest dto, PagingRequest pagingRequest) {
        String email = SecurityUtil.getUserEmail();
        Employee lead = employeeRepository.findByEmailAndPositionIsLead(email, true)
                .orElseThrow(() -> new NotFoundException("Lead not found or you are not lead"));
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize());
        Specification<Absence> specification = Specification
                .where(AbsenceSpecification.hasStatus(dto.getStatus()))
                .and(AbsenceSpecification.hasUserEmail(dto.getEmail()))
                .and(AbsenceSpecification.hasType(AbsenceTypeEnum.ATTENDANCE.ordinal()))
                .and(AbsenceSpecification.hasCreatedAtBetween(dto.getFrom(), dto.getTo()))
                .and(AbsenceSpecification.hasLeadId(lead.getId()))
                .and(AbsenceSpecification.hasNotUserEmail(lead.getEmail()));
        Page<Absence> page = absenceRepository.findAll(specification, pageable);
        return page.map(this::convertToAdditionalCheckInResponseForManager);
    }

    @Override
    public List<AdditionalCheckInResponse> approveAdditionalCheckins(ManagerAdditionalCheckInRequest dto) {
        List<Absence> result = new ArrayList<>();
        for (long id : dto.getIdList()) {
            Absence absence = absenceRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Additional check-in not found"));
            Employee employee = absence.getEmployee();
            AttendanceRange attendanceRange = employee.getAttendanceRange();
            if (absence.getStatus() != AbsenceStatus.PENDING.ordinal()) continue;

            int dateInteger = DateUtil.convertLocalDateToInteger(
                    absence.getStartTime() != null ?
                            absence.getStartTime().toLocalDate() : absence.getEndTime().toLocalDate());
            Optional<AttendanceReport> attendanceReportOptional = attendanceReportRepository
                    .findByEmployeeIdAndDate(employee.getEmployeeId(), dateInteger);
            AttendanceReport attendanceReport;
            LocalDateTime absenceStartTime = absence.getStartTime();
            LocalDateTime absenceEndTime = absence.getEndTime();

            // If attendance report already exist
            if (attendanceReportOptional.isPresent()) {
                attendanceReport = attendanceReportOptional.get();
                if (absenceStartTime != null)
                    attendanceReport.setStartTime(
                            absenceStartTime.isBefore(attendanceReport.getRawStartTime())
                                    ? absenceStartTime
                                    : attendanceReport.getRawStartTime());
                if (absenceEndTime != null)
                    attendanceReport.setEndTime(
                            absenceEndTime.isAfter(attendanceReport.getRawEndTime())
                                    ? absenceEndTime
                                    : attendanceReport.getRawEndTime());
                double attendanceTime = syncAttendanceService.computeAttendanceTime(
                        attendanceReport.getStartTime().toLocalTime(),
                        attendanceReport.getEndTime().toLocalTime(),
                        attendanceRange
                );
                attendanceReport.setAttendanceTime(attendanceTime);
            } else {
                // Else create new one
                attendanceReport = AttendanceReport
                        .builder()
                        .employeeId(employee.getEmployeeId())
                        .attendanceRangeId(attendanceRange.getId())
                        .startTime(absenceStartTime)
                        .endTime(absenceEndTime)
                        .attendanceTime(syncAttendanceService.computeAttendanceTime(
                                absenceStartTime.toLocalTime(), absenceEndTime.toLocalTime(), attendanceRange
                        ))
                        .date(dateInteger)
                        .leaveTime(0.0)
                        .build();
            }
            attendanceReport.setStatus(absence.getAbsenceType().getId().intValue() + 1000);
            attendanceReportRepository.save(attendanceReport);

            // Save form status
            absence.setStatus(AbsenceStatus.APPROVED.ordinal());
            absence.setUpdatedBy(SecurityUtil.getUserEmail());
            result.add(absenceRepository.save(absence));
        }
        return result.stream()
                .map(this::convertToAdditionalCheckInResponseForManager)
                .toList();
    }

    @Override
    public List<AdditionalCheckInResponse> rejectAdditionalCheckins(final ManagerAdditionalCheckInRequest dto) {
        List<Absence> result = new ArrayList<>();
        for (long id : dto.getIdList()) {
            Absence absence = absenceRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Additional check-in not found"));
            absence.setStatus(AbsenceStatus.REJECTED.ordinal());
            absence.setUpdatedBy(SecurityUtil.getUserEmail());
            result.add(absenceRepository.save(absence));
        }
        return result
                .stream()
                .map(this::convertToAdditionalCheckInResponseForManager)
                .toList();
    }

    private void sendEmail(Absence absence, AdditionalCheckInRequest dto, String leadEmail) {
        String subject = "[Chấm công] "
                + absence.getEmployee().getFullName()
                + "_"
                + absence.getEmployee().getPosition().getName()
                + "_"
                + "Bổ sung chấm công"
                + " "
                + (absence.getStartTime() != null ? absence.getStartTime().toLocalDate() : absence.getEndTime().toLocalDate()).format(Constants.DATE_FORMATTER);

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
        variables.put("date", (absence.getStartTime() != null ? absence.getStartTime().toLocalDate() : absence.getEndTime().toLocalDate()).format(Constants.DATE_FORMATTER));
        variables.put("checkIn", dto.getCheckIn() != null ? dto.getCheckIn().format(Constants.TIME_FORMATTER) : "");
        variables.put("checkOut", dto.getCheckOut() != null ? dto.getCheckOut().format(Constants.TIME_FORMATTER) : "");
        variables.put("approvalLink", approvalLink);
        context.setVariables(variables);
        final List<String> finalCopiedCcList = copiedCcList;
        Thread emailThread = new Thread(() -> {
            try {
                mailService.sendEmailWithHtmlTemplate(leadEmail, finalCopiedCcList, subject, "additional-check-in-mail-template", context);
                absence.setEmailStatus(EmailStatusEnum.SUCCESS.ordinal());
            } catch (Exception e) {
                absence.setEmailStatus(EmailStatusEnum.FAILED.ordinal());
            }
            absenceRepository.save(absence);
        });
        emailThread.start();
    }

    private AdditionalCheckInResponse convertToAdditionalCheckInResponse(Absence absence) {
        return AdditionalCheckInResponse.builder()
                .date(absence.getStartTime() != null ? absence.getStartTime().toLocalDate() : absence.getEndTime().toLocalDate())
                .checkIn(absence.getStartTime() != null ? absence.getStartTime().toLocalTime() : null)
                .checkOut(absence.getEndTime() != null ? absence.getEndTime().toLocalTime() : null)
                .note(absence.getNote())
                .type(absence.getType())
                .subType(absence.getAbsenceType().getDescription())
                .status(absence.getStatus())
                .emailStatus(absence.getEmailStatus())
                .build();
    }

    private AdditionalCheckInResponse convertToAdditionalCheckInResponseForManager(Absence absence) {
        return AdditionalCheckInResponse.builder()
                .id(absence.getId())
                .email(absence.getEmployee().getEmail())
                .date(absence.getStartTime() != null ? absence.getStartTime().toLocalDate() : absence.getEndTime().toLocalDate())
                .checkIn(absence.getStartTime() != null ? absence.getStartTime().toLocalTime() : null)
                .checkOut(absence.getEndTime() != null ? absence.getEndTime().toLocalTime() : null)
                .note(absence.getNote())
                .type(absence.getType())
                .subType(absence.getAbsenceType().getDescription())
                .status(absence.getStatus())
                .emailStatus(absence.getEmailStatus())
                .build();
    }
}
