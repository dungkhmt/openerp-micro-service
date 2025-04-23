package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.holiday.HolidayQueryRequest;
import openerp.openerpresourceserver.dto.request.holiday.HolidayRequest;
import openerp.openerpresourceserver.entity.Holiday;
import openerp.openerpresourceserver.enums.HolidayTypeEnum;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.HolidayRepository;
import openerp.openerpresourceserver.repo.specification.HolidaySpecification;
import openerp.openerpresourceserver.service.HolidayService;
import openerp.openerpresourceserver.util.DateUtil;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {
    private final HolidayRepository holidayRepository;

    @Override
    public Page<Holiday> getHolidaysByProperties(final HolidayQueryRequest dto, final PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.Direction.DESC, "date");
        Specification<Holiday> specs = Specification
                .where(HolidaySpecification.hasFromDate(dto.getFrom()))
                .and(HolidaySpecification.hasToDate(dto.getTo()))
                .and(HolidaySpecification.hasType(dto.getType()))
                .and(HolidaySpecification.hasStatus(dto.getStatus()));
        return holidayRepository.findAll(specs, pageable);
    }

    @Override
    public Holiday getHolidayById(final Long id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Holiday not found"));
    }

    @Override
    public Holiday createHoliday(final HolidayRequest dto) throws BadRequestException {
        if (holidayRepository.existsByDateAndStatus(dto.getDate(), dto.getStatus())) {
            throw new BadRequestException("Holiday on " + dto.getDate() + " already exists");
        }
        Holiday holiday = Holiday.builder()
                .date(dto.getDate())
                .type(dto.getType())
                .note(dto.getNote())
                .bonusTime(dto.getBonusTime())
                .updatedBy(SecurityUtil.getUserEmail())
                .status(dto.getStatus())
                .build();
        return holidayRepository.save(holiday);
    }

    @Override
    public Holiday editHoliday(final HolidayRequest dto) throws BadRequestException {
        Holiday holiday = holidayRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Holiday not found"));
        if (holidayRepository.existsByIdNotAndDateAndStatus(dto.getId(), dto.getDate(), dto.getStatus())) {
            throw new BadRequestException("Holiday on " + dto.getDate() + " already exists");
        }
        holiday.setDate(dto.getDate());
        holiday.setType(dto.getType());
        holiday.setBonusTime(dto.getBonusTime());
        holiday.setNote(dto.getNote());
        holiday.setStatus(dto.getStatus());
        holiday.setUpdatedBy(SecurityUtil.getUserEmail());
        return holidayRepository.save(holiday);
    }

    @Override
    public List<Holiday> deleteHolidays(final List<Long> idList) {
        List<Holiday> result = new ArrayList<>();
        for (Long id : idList) {
            Holiday holiday = holidayRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Holiday not found"));
            holiday.setStatus(StatusEnum.INACTIVE.ordinal());
            result.add(holidayRepository.save(holiday));
        }
        return result;
    }

    @Override
    public List<Integer> getAttendanceHoliday(final LocalDate startDate, final LocalDate endDate) {
        List<Holiday> holidayList = holidayRepository.findByDateBetweenAndTypeAndStatus(startDate, endDate, HolidayTypeEnum.HOLIDAY.ordinal(), StatusEnum.ACTIVE.ordinal());
        return holidayList.stream()
                .map(holiday -> DateUtil.convertLocalDateToInteger(holiday.getDate()))
                .toList();
    }

    @Override
    public double getBonusTime(int date) {
        Optional<Holiday> holiday = holidayRepository.findByDateAndTypeAndStatus(DateUtil.convertIntegerToLocalDate(date), HolidayTypeEnum.MCC.ordinal(), StatusEnum.ACTIVE.ordinal());
        if (holiday.isEmpty()) return 0.0;
        return holiday.get().getBonusTime();
    }

    @Override
    public Holiday getHolidayByDate(final int type, final int date) {
        Optional<Holiday> holiday = holidayRepository.findByDateAndTypeAndStatus(DateUtil.convertIntegerToLocalDate(date), type, StatusEnum.ACTIVE.ordinal());
        return holiday.orElse(null);
    }
}

