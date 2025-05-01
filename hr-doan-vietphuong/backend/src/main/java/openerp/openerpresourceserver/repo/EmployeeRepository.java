package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.dto.request.employee.EmployeeQueryRequest;
import openerp.openerpresourceserver.dto.response.employee.EmployeeResponse;
import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.repo.projection.EmployeeResponseProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findByEmail(String username);

    boolean existsByIdNotAndEmail(Long id, String email);

    boolean existsByIdNotAndEmployeeId(Long id, Integer employeeId);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(Integer employeeId);


    Optional<Employee> findByEmailAndStatus(String email, Integer status);

    @Query("""
            SELECT new openerp.openerpresourceserver.dto.response.employee.EmployeeResponse(
            e.id,
            e.employeeId,
            e.fullName,
            e.email,
            e.phone,
            e.profileUrl,
            e.position.id,
            e.status)
            FROM Employee e
            WHERE
            (
                :#{#dto.keyword == null ? 'true' : 'false'} = 'true'
                OR LOWER(e.email) LIKE %:#{#dto.keyword == null ? '' : #dto.keyword.toLowerCase()}%
                OR LOWER(e.fullName) LIKE %:#{#dto.keyword == null ? '' : #dto.keyword.toLowerCase()}%
            )
            AND (:#{#dto.status} IS NULL OR e.status = :#{#dto.status})
            """)
    Page<EmployeeResponse> findEmployeesByProperties(@Param("dto") EmployeeQueryRequest dto, Pageable pageable);

    @Query(value = """
            SELECT
            e.id,
            e.employee_id AS employeeId,
            e.user_login_id AS userLoginId,
            e.full_name AS fullName,
            e.email,
            e.phone,
            e.profile_url AS profileUrl,
            e.annual_leave AS annualLeave,
            e.status,
            e.organization_id AS organizationId,
            e.attendance_range_id AS attendanceRangeId,
            e.position_id AS positionId,
            e.job_histories AS jobHistories
            FROM phuongvv_employees e
            WHERE e.id = :id
            """,
            nativeQuery = true)
    EmployeeResponseProjection findEmployeeResponseById(@Param("id") Long id);

    boolean existsByUserId(String userLoginId);

    boolean existsByIdNotAndUserId(Long id, String userLoginId);

    Optional<Employee> findByUserIdAndStatus(String email, int status);

    Employee findByEmployeeIdAndStatus(Integer emloyeeId, int status);

    @Query("""
        SELECT e FROM Employee e
        WHERE e.organization.id IN (:organizationIdList)
        AND e.status = 1
        ORDER BY e.email ASC
    """)
    List<Employee> findByOrganizations(@Param("organizationIdList") List<Long> organizationIdList);

    List<Employee> findAllByStatus(int status);

    Optional<Employee> findByEmailAndPositionIsLead(String email, boolean isLead);
}
