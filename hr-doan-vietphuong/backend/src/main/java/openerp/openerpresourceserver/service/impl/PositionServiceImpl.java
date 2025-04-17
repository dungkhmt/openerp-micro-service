package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.position.PositionQueryRequest;
import openerp.openerpresourceserver.dto.request.position.PositionRequest;
import openerp.openerpresourceserver.dto.response.position.PositionResponse;
import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.entity.JobHistory;
import openerp.openerpresourceserver.entity.Position;
import openerp.openerpresourceserver.entity.Role;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.EmployeeRepository;
import openerp.openerpresourceserver.repo.PositionRepository;
import openerp.openerpresourceserver.repo.RoleRepository;
import openerp.openerpresourceserver.repo.specification.PositionSpecification;
import openerp.openerpresourceserver.service.PositionService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<PositionResponse> getPositionsByProperties(PositionQueryRequest dto, PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.by(Sort.Direction.DESC, "id"));
        Specification<Position> specs = Specification
                .where(PositionSpecification.hasKeyword(dto.getKeyword()))
                .and(PositionSpecification.hasStatus(dto.getStatus()));
        return positionRepository.findAll(specs, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public PositionResponse getPositionById(Long id) throws NotFoundException {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position not found with id: " + id));
        return mapToResponse(position);
    }

    @Override
    public Position addPosition(PositionRequest dto) throws BadRequestException {
        if (positionRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Position name already exists");
        }

        List<Role> roles = dto.getRoleIdList().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId)))
                .collect(Collectors.toList());

        Position position = Position.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .roles(roles)
                .isOfficial(dto.getIsOfficial())
                .isLead(dto.getIsLead())
                .updatedBy(SecurityUtil.getUserEmail())
                .build();

        return positionRepository.save(position);
    }

    @Override
    public Position updatePosition(PositionRequest dto) throws BadRequestException, NotFoundException {
        Position position = positionRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Position not found with id: " + dto.getId()));

        if (positionRepository.existsByIdNotAndName(dto.getId(), dto.getName())) {
            throw new BadRequestException("Position name already exists");
        }

        List<Role> roles = dto.getRoleIdList().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId)))
                .collect(Collectors.toList());

        position.setName(dto.getName());
        position.setDescription(dto.getDescription());
        position.setIsLead(dto.getIsLead());
        position.setIsOfficial(dto.getIsOfficial());
        position.setStatus(dto.getStatus());

        position.setRoles(roles);
        position.setUpdatedBy(SecurityUtil.getUserEmail());

        return positionRepository.save(position);
    }

    @Override
    public Position deletePosition(Long id) throws NotFoundException {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position not found with id: " + id));
        List<Employee> employees = employeeRepository.findAll();
        if (employees.stream().anyMatch(e -> e.getPosition() != null && e.getPosition().getId().equals(id))) {
            throw new BadRequestException("Cannot delete position that is currently assigned to employees");
        }
        position.setStatus(StatusEnum.INACTIVE.ordinal());
        return positionRepository.save(position);
    }

    @Override
    public Employee assignPositionToEmployee(Long employeeId, Long positionId) throws NotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + employeeId));
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new NotFoundException("Position not found with id: " + positionId));

        // Cập nhật lịch sử công việc
        List<JobHistory> jobHistories = employee.getJobHistories();
        if (jobHistories == null) {
            jobHistories = new ArrayList<>();
            employee.setJobHistories(jobHistories);
        }

        if (employee.getPosition() != null) {
            JobHistory history = JobHistory.builder()
                    .positionId(employee.getPosition().getId())
                    .positionName(employee.getPosition().getName())
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now())
                    .createdBy(SecurityUtil.getUserEmail())
                    .build();
            jobHistories.add(history);
        }

        employee.setPosition(position);
        return employeeRepository.save(employee);
    }

    private PositionResponse mapToResponse(Position position) {
        PositionResponse response = new PositionResponse();
        response.setId(position.getId());
        response.setName(position.getName());
        response.setDescription(position.getDescription());
        response.setStatus(position.getStatus());
        response.setCreatedAt(position.getCreatedAt());
        response.setUpdatedAt(position.getUpdatedAt());
        response.setUpdatedBy(position.getUpdatedBy());
        response.setIsLead(position.getIsLead());
        response.setIsOfficial(position.getIsOfficial());
        response.setRoleIdList(position.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList()));
        return response;
    }
}