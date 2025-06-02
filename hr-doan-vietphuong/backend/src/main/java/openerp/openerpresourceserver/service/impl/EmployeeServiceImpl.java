package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.employee.EmployeeQueryRequest;
import openerp.openerpresourceserver.dto.request.employee.EmployeeRequest;
import openerp.openerpresourceserver.dto.response.employee.EmployeeResponse;
import openerp.openerpresourceserver.dto.response.employee.SimpleEmployeeResponse;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.repo.projection.EmployeeResponseProjection;
import openerp.openerpresourceserver.service.EmployeeService;
import openerp.openerpresourceserver.service.UploadService;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;


@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final UploadService uploadService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRangeRepository attendanceRangeRepository;
    private final OrganizationRepository organizationRepository;
    private final PositionRepository positionRepository;
    @Override
    public Employee getEmployeeInfo() {
        String email = SecurityUtil.getUserEmail();
        return employeeRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Employee not found"));
    }

    @Override
    public Employee updateProfile(final EmployeeRequest dto) throws NotFoundException {
        String email = SecurityUtil.getUserEmail();
        Optional<Employee> employeeOptional = employeeRepository.findByEmail(email);
        if (employeeOptional.isEmpty()) {
            throw new NotFoundException("Employee not found");
        }
        Employee employee = employeeOptional.get();
        employee.setFullName(dto.getFullName());
        employee.setPhone(dto.getPhone());
        return employeeRepository.save(employee);
    }

    @Override
    public Page<EmployeeResponse> getEmployeesByProperties(final EmployeeQueryRequest dto, final PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.by(Sort.Direction.DESC, "id"));
        return employeeRepository.findEmployeesByProperties(dto, pageable);
    }

    @Override
    public EmployeeResponseProjection getEmployeeById(Long id) throws NotFoundException {
        EmployeeResponseProjection result = employeeRepository.findEmployeeResponseById(id);
        if (result == null) {
            throw new NotFoundException("Employee not found");
        }

        return result;
    }

    public List<Map<String, String>> getEmployeeFacesById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));

        Integer employeeId = employee.getEmployeeId();
        File faceDir = new File("faces");

        if (!faceDir.exists() || !faceDir.isDirectory()) {
            throw new NotFoundException("Faces directory not found");
        }

        File[] faceFiles = faceDir.listFiles((dir, name) -> name.startsWith(employeeId + "-") &&
                (name.endsWith(".jpg") || name.endsWith(".png")));

        List<Map<String, String>> images = new ArrayList<>();
        for (File file : faceFiles) {
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String base64 = Base64.getEncoder().encodeToString(bytes);

                Map<String, String> map = new HashMap<>();
                map.put("filename", file.getName());
                map.put("base64", base64);
                images.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    @Override
    public Employee addEmployee(final EmployeeRequest dto) throws BadRequestException {
        if (!userRepository.existsById(dto.getUserLoginId())) {
            throw new BadRequestException("Email not exists on keycloak!");
        }
        if (employeeRepository.existsByUserId(dto.getUserLoginId())) {
            throw new BadRequestException("User Login ID used on another employee");
        }
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (dto.getEmployeeId() != null && employeeRepository.existsByEmployeeId(dto.getEmployeeId())) {
            throw new BadRequestException("Employee ID already exists");
        }
        User user = userRepository.findById(dto.getUserLoginId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        AttendanceRange attendanceRange = attendanceRangeRepository.findById(dto.getAttendanceRangeId())
                .orElseThrow(() -> new NotFoundException("Attendance range not found"));
        Organization organization = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found"));
        Position position = positionRepository.findById(dto.getPositionId())
                .orElseThrow(() -> new NotFoundException("Position not found"));
        Employee employee = Employee.builder()
                .user(user)
                .email(dto.getEmail())
                .employeeId(dto.getEmployeeId())
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .annualLeave(dto.getAnnualLeave())
                .status(dto.getStatus())
                .updatedBy(SecurityUtil.getUserEmail())
                .organization(organization)
                .attendanceRange(attendanceRange)
                .position(position)
                .jobHistories(List.of(
                        JobHistory.builder()
                                .positionId(position.getId())
                                .positionName(position.getName())
                                .organizationId(organization.getId())
                                .organizationName(organization.getName())
                                .startDate(LocalDate.now())
                                .endDate(null)
                                .createdBy(SecurityUtil.getUserEmail())
                                .build()
                ))
                .build();
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(final EmployeeRequest dto) throws BadRequestException, NotFoundException {
        Optional<Employee> EmployeeOptional = employeeRepository.findById(dto.getId());
        if (EmployeeOptional.isEmpty()) {
            throw new NotFoundException("Employee not found");
        }
        if (!userRepository.existsById(dto.getUserLoginId())) {
            throw new BadRequestException("User Login ID not exists on keycloak!");
        }
        if (employeeRepository.existsByIdNotAndUserId(dto.getId(), dto.getUserLoginId())) {
            throw new BadRequestException("User Login ID used on another employee");
        }
        if (employeeRepository.existsByIdNotAndEmail(dto.getId(), dto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (dto.getEmployeeId() != null && employeeRepository.existsByIdNotAndEmployeeId(dto.getId(), dto.getEmployeeId())) {
            throw new BadRequestException("Employee ID already exists");
        }
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Organization organization = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found"));
        AttendanceRange attendanceRange = attendanceRangeRepository.findById(dto.getAttendanceRangeId())
                .orElseThrow(() -> new NotFoundException("Attendance range not found"));
        Position position = positionRepository.findById(dto.getPositionId())
                .orElseThrow(() -> new NotFoundException("Position not found"));
        Employee employee = EmployeeOptional.get();
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setEmail(dto.getEmail());
        employee.setUser(user);
        employee.setFullName(dto.getFullName());
        employee.setPhone(dto.getPhone());
        employee.setAnnualLeave(dto.getAnnualLeave());
        employee.setAttendanceRange(attendanceRange);

        List<JobHistory> jobHistories = employee.getJobHistories();
        if (jobHistories == null) {
            jobHistories = new ArrayList<>();
            employee.setJobHistories(jobHistories);
        }

        if (employee.getPosition() != position || employee.getOrganization() != organization) {
            if (!jobHistories.isEmpty()) {
                jobHistories.get(jobHistories.size() - 1).setEndDate(LocalDate.now());
            }
            JobHistory history = JobHistory.builder()
                    .positionId(position.getId())
                    .positionName(position.getName())
                    .organizationId(organization.getId())
                    .organizationName(organization.getName())
                    .startDate(LocalDate.now())
                    .endDate(null)
                    .createdBy(SecurityUtil.getUserEmail())
                    .build();
            jobHistories.add(history);
        }

        employee.setOrganization(organization);
        employee.setPosition(position);
        employee.setStatus(dto.getStatus());
        employee.setUpdatedBy(SecurityUtil.getUserEmail());
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> deleteEmployee(final List<Long> idList) {
        List<Employee> result = new ArrayList<>();
        for (long id : idList) {
            Optional<Employee> employeeOptional = employeeRepository.findById(id);
            if (employeeOptional.isPresent()) {
                Employee employee = employeeOptional.get();
                employee.setStatus(StatusEnum.DELETED.ordinal());
                employee.setOrganization(null);
                employee.setUpdatedBy(SecurityUtil.getUserEmail());
                result.add(employeeRepository.save(employee));
            } else {
                throw new NotFoundException("Employee not found");
            }
        }
        return result;
    }

    @Override
    public List<SimpleEmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAllByStatus(StatusEnum.ACTIVE.ordinal());
        return employees.stream()
                .map(this::mapToSimpleEmployeeResponse)
                .toList();
    }

    @Override
    public Employee updateAvatar(MultipartFile image) throws IOException {
        Employee employee = employeeRepository.findByEmail(SecurityUtil.getUserEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        String imageUrl = uploadService.upload(image.getBytes(), image.getOriginalFilename(), "avatars");
        employee.setImageUrl(imageUrl);
        return employeeRepository.save(employee);
    }

    @Override
    public String deleteFaces(String filename) {
        Path facePath = Paths.get("./faces", filename);

        if (!Files.exists(facePath)) {
            throw new NotFoundException("Ảnh không tồn tại hoặc không hợp lệ.");
        }
        try {
            Files.delete(facePath);
            return filename;
        } catch (IOException e) {
            throw new BadRequestException("Không thể xoá ảnh");
        }
    }

    private SimpleEmployeeResponse mapToSimpleEmployeeResponse(Employee employee) {
        return SimpleEmployeeResponse.builder()
                .email(employee.getEmail())
                .fullName(employee.getFullName())
                .build();
    }
}
