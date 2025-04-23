package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.absenceType.AbsenceTypeQueryRequest;
import openerp.openerpresourceserver.dto.request.absenceType.AbsenceTypeRequest;
import openerp.openerpresourceserver.entity.AbsenceType;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.AbsenceTypeRepository;
import openerp.openerpresourceserver.repo.specification.AbsenceTypeSpecification;
import openerp.openerpresourceserver.service.AbsenceTypeService;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AbsenceTypeServiceImpl implements AbsenceTypeService {
    private final AbsenceTypeRepository absenceTypeRepository;

    @Override
    public Page<AbsenceType> getAbsenceTypeByProperties(final AbsenceTypeQueryRequest dto, final PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.by(Sort.Direction.DESC, "id"));
        Specification<AbsenceType> specification = Specification
                .where(AbsenceTypeSpecification.hasType(dto.getType()))
                .and(AbsenceTypeSpecification.hasStatus(dto.getStatus()))
                .and(AbsenceTypeSpecification.hasKeyword(dto.getKeyword()));
        return absenceTypeRepository.findAll(specification, pageable);
    }

    @Override
    public AbsenceType getAbsenceTypeById(final Long id) {
        return absenceTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Absence type not found"));
    }

    @Override
    public AbsenceType addAbsenceType(final AbsenceTypeRequest dto) throws BadRequestException {
        if (absenceTypeRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new BadRequestException("Absence type code already exists");
        }
        AbsenceType absenceType = AbsenceType.builder()
                .type(dto.getType())
                .code(dto.getCode().toUpperCase())
                .description(dto.getDescription())
                .hasValue(dto.getHasValue())
                .status(dto.getStatus())
                .updatedBy(SecurityUtil.getUserEmail())
                .build();
        return absenceTypeRepository.save(absenceType);
    }

    @Override
    public AbsenceType updateAbsenceType(final AbsenceTypeRequest dto) throws BadRequestException {
        if (absenceTypeRepository.existsByIdNotAndCodeIgnoreCase(dto.getId(), dto.getCode())) {
            throw new BadRequestException("Absence type code already exists");
        }
        AbsenceType absenceType =  absenceTypeRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Absence type not found"));
        absenceType.setCode(dto.getCode().toUpperCase());
        absenceType.setDescription(dto.getDescription());
        absenceType.setType(dto.getType());
        absenceType.setHasValue(dto.getHasValue());
        absenceType.setStatus(dto.getStatus());
        absenceType.setUpdatedBy(SecurityUtil.getUserEmail());
        return absenceTypeRepository.save(absenceType);
    }

    @Override
    public List<AbsenceType> deleteAbsenceTypes(final List<Long> absenceTypeIdList) {
        List<AbsenceType> result = new ArrayList<>();
        for (long id : absenceTypeIdList) {
            Optional<AbsenceType> absenceTypeOptional = absenceTypeRepository.findById(id);
            if (absenceTypeOptional.isPresent()) {
                AbsenceType absenceType = absenceTypeOptional.get();
                absenceType.setStatus(StatusEnum.INACTIVE.ordinal());
                absenceType.setUpdatedBy(SecurityUtil.getUserEmail());
                result.add(absenceTypeRepository.save(absenceType));
            } else {
                throw new NotFoundException("Absence type not found");
            }
        }
        return result;
    }
}