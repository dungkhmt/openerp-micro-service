package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.attendanceRange.AttendanceRangeQueryRequest;
import openerp.openerpresourceserver.dto.request.attendanceRange.AttendanceRangeRequest;
import openerp.openerpresourceserver.entity.AttendanceRange;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.InvalidRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.AttendanceRangeRepository;
import openerp.openerpresourceserver.repo.specification.AttendanceRangeSpecification;
import openerp.openerpresourceserver.service.AttendanceRangeService;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceRangeServiceImpl implements AttendanceRangeService {
    private final AttendanceRangeRepository attendanceRangeRepository;

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
    public AttendanceRange createAttendanceRange(AttendanceRangeRequest request) {
        if (attendanceRangeRepository.existsByCode(request.getCode()))
            throw new InvalidRequestException("Code existed");
        AttendanceRange attendanceRangeEntity = AttendanceRange
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
        return attendanceRangeRepository.save(attendanceRangeEntity);
    }

    @Override
    public AttendanceRange updateAttendanceRange(AttendanceRangeRequest request) {
        AttendanceRange attendanceRangeEntity = attendanceRangeRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Attendance Range Not Found"));
        if (attendanceRangeRepository.existsByIdNotAndCode(request.getId(), request.getCode())) {
            throw new InvalidRequestException("Code Existed");
        }
        attendanceRangeEntity.setCode(request.getCode());
        attendanceRangeEntity.setDescription(request.getDescription());
        attendanceRangeEntity.setStartTime(request.getStartTime());
        attendanceRangeEntity.setEndTime(request.getEndTime());
        attendanceRangeEntity.setStartLunch(request.getStartLunch());
        attendanceRangeEntity.setEndLunch(request.getEndLunch());
        attendanceRangeEntity.setFullHours(request.getFullHours());
        attendanceRangeEntity.setBonusTime(request.getBonusTime());
        attendanceRangeEntity.setStatus(request.getStatus());
        attendanceRangeEntity.setUpdatedBy(SecurityUtil.getUserEmail());
        return attendanceRangeRepository.save(attendanceRangeEntity);
    }

    @Override
    public AttendanceRange deleteAttendanceRange(long id) {
        AttendanceRange attendanceRangeEntity = attendanceRangeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attendance Range Not Found"));
        attendanceRangeEntity.setStatus(StatusEnum.INACTIVE.ordinal());
        return attendanceRangeRepository.save(attendanceRangeEntity);
    }
}
