package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.attendanceRange.AttendanceRangeQueryRequest;
import openerp.openerpresourceserver.dto.request.attendanceRange.AttendanceRangeRequest;
import openerp.openerpresourceserver.entity.AttendanceRange;
import openerp.openerpresourceserver.enums.AbsenceTimeEnum;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.InvalidRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.AttendanceRangeRepository;
import openerp.openerpresourceserver.repo.specification.AttendanceRangeSpecification;
import openerp.openerpresourceserver.service.AttendanceRangeService;
import openerp.openerpresourceserver.service.SyncAttendanceService;
import openerp.openerpresourceserver.util.DateUtil;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

import static openerp.openerpresourceserver.util.DateUtil.START_LUNCH;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceRangeServiceImpl implements AttendanceRangeService {
    private final AttendanceRangeRepository attendanceRangeRepository;
    private final SyncAttendanceService syncAttendanceService;

    @Override
    public Page<AttendanceRange> getAttendanceRanges(AttendanceRangeQueryRequest request,
                                                     PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.by(Sort.Direction.DESC, "id"));
        Specification<AttendanceRange> specs = Specification
                .where(AttendanceRangeSpecification.hasKeyword(request.getKeyword()))
                .and(AttendanceRangeSpecification.hasStatus(request.getStatus()));
        return attendanceRangeRepository.findAll(specs, pageable);
    }

    @Override
    public AttendanceRange getAttendanceRangeById(final Long id) {
        return attendanceRangeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attendance Range Not Found"));
    }

    @Override
    public double getAttendanceTime(
            AttendanceRange attendanceRange,
            double leaveTime,
            LocalTime startTime,
            LocalTime endTime) {
        int shift = attendanceRange.getShift(leaveTime);
        double duration;
        double value;
        if (shift == AbsenceTimeEnum.AM.ordinal()) {
            LocalTime rangeEndLunch = attendanceRange.getEndLunch();
            LocalTime rangeEndTime = attendanceRange.getEndTime();
            startTime = startTime.isAfter(rangeEndLunch) ? startTime : rangeEndLunch;
            endTime = endTime.isAfter(rangeEndTime) ? rangeEndTime : endTime;
            duration = syncAttendanceService.computeAttendanceTime(startTime, endTime, attendanceRange);
        } else {
            LocalTime rangeStartTime = attendanceRange.getStartTime();
            LocalTime rangeStartLunch = START_LUNCH.minusMinutes(15);
            boolean isMorningShiftFull =
                    startTime.isBefore(rangeStartTime) && endTime.isAfter(rangeStartLunch);
            startTime = startTime.isBefore(rangeStartLunch) ? startTime : rangeStartLunch;
            endTime = endTime.isAfter(rangeStartLunch) ? rangeStartLunch : endTime;
            if (isMorningShiftFull) duration = attendanceRange.getFullHours() - leaveTime;
            else duration = DateUtil.getDifferenceInHours(startTime, endTime);
        }
        // duration can only have 3 range of value:
        // + morning absence -> [end lunch, end time]
        // + afternoon absence -> [start time, start lunch]
        // + negative value if work at wrong shift
        // work at wrong shift
        if (duration <= 0) value = 0.0;
            // if work time = full time of the other shift (the other shift of absence shift)
            // or work time = half-time of full-hour
        else if (duration == attendanceRange.getFullHours() - leaveTime || duration >= attendanceRange.getFullHours() / 2) value = 4.0;
        else value = duration;

        return value;
    }

    @Override
    public AttendanceRange createAttendanceRange(AttendanceRangeRequest request) {
        if (attendanceRangeRepository.existsByCode(request.getCode()))
            throw new InvalidRequestException("Code existed");
        AttendanceRange attendanceRange = AttendanceRange
                .builder()
                .code(request.getCode())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .startLunch(request.getStartLunch())
                .endLunch(request.getEndLunch())
                .fullHours(request.getFullHours())
                .bonusTime(request.getBonusTime())
                .status(request.getStatus())
                .updatedBy(SecurityUtil.getUserEmail())
                .build();
        return attendanceRangeRepository.save(attendanceRange);
    }

    @Override
    public AttendanceRange updateAttendanceRange(AttendanceRangeRequest request) {
        AttendanceRange attendanceRange = attendanceRangeRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Attendance Range Not Found"));
        if (attendanceRangeRepository.existsByIdNotAndCode(request.getId(), request.getCode())) {
            throw new InvalidRequestException("Code Existed");
        }
        attendanceRange.setCode(request.getCode());
        attendanceRange.setDescription(request.getDescription());
        attendanceRange.setStartTime(request.getStartTime());
        attendanceRange.setEndTime(request.getEndTime());
        attendanceRange.setStartLunch(request.getStartLunch());
        attendanceRange.setEndLunch(request.getEndLunch());
        attendanceRange.setFullHours(request.getFullHours());
        attendanceRange.setBonusTime(request.getBonusTime());
        attendanceRange.setStatus(request.getStatus());
        attendanceRange.setUpdatedBy(SecurityUtil.getUserEmail());
        return attendanceRangeRepository.save(attendanceRange);
    }

    @Override
    public AttendanceRange deleteAttendanceRange(long id) {
        AttendanceRange attendanceRange = attendanceRangeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attendance Range Not Found"));
        attendanceRange.setStatus(StatusEnum.INACTIVE.ordinal());
        return attendanceRangeRepository.save(attendanceRange);
    }
}
