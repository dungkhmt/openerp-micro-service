package openerp.openerpresourceserver.repo.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.Attendance;
import openerp.openerpresourceserver.entity.AttendanceReport;
import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.repo.projection.AttendanceDTO;
import openerp.openerpresourceserver.util.DateUtil;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class SyncAttendanceRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<AttendanceDTO> findAttendancesWithFilters(Integer startDate,
                                                          Integer endDate,
                                                          List<Integer> employeeIds) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AttendanceDTO> query = criteriaBuilder.createQuery(AttendanceDTO.class);
        Root<Attendance> root = query.from(Attendance.class);
        Join<Attendance, Employee> attendanceEmployeeJoin = root.join("employee");

        List< Predicate> predicates = new ArrayList<>();
        if (endDate != null) {
            predicates.add(criteriaBuilder.between(root.get("date"), startDate, endDate));
        } else {
            predicates.add(criteriaBuilder.between(root.get("date"), startDate, DateUtil.convertLocalDateToInteger(LocalDate.now())));
        }
        if (employeeIds != null && !employeeIds.isEmpty()) {
            predicates.add(root.get("id").in(employeeIds));
        }
        query.groupBy(root.get("id"), root.get("date"), attendanceEmployeeJoin.get("attendanceRange").get("id"));
        final Expression<Number> minTime = criteriaBuilder.min(root.get("time"));
        final Expression<Number> maxTime = criteriaBuilder.max(root.get("time"));
        query.multiselect(
                root.get("id").alias("id"),
                root.get("date").alias("date"),
                minTime.alias("clockIn"),
                maxTime.alias("clockOut"),
                attendanceEmployeeJoin.get("attendanceRange").get("id").alias("attendanceRangeId")
        );
        query.where(predicates.toArray(new Predicate[0]));
        TypedQuery<AttendanceDTO> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    @Transactional
    public List<AttendanceReport> findAttendanceReportsWithFilters(Integer startDate,
                                                                   Integer endDate,
                                                                   List<Integer> employeeIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AttendanceReport> query = cb.createQuery(AttendanceReport.class);
        Root<AttendanceReport> root = query.from(AttendanceReport.class);
        List<Predicate> predicates = new ArrayList<>();
        if (endDate != null) {
            predicates.add(cb.between(root.get("date"), startDate, endDate));
        } else {
            predicates.add(cb.between(root.get("date"), startDate, DateUtil.convertLocalDateToInteger(LocalDate.now())));
        }
        if (employeeIds != null && !employeeIds.isEmpty()) {
            predicates.add(root.get("employeeId").in(employeeIds));
        }
        query.where(predicates.toArray(new Predicate[0]));
        query.multiselect(
                root.get("id").alias("id"),
                root.get("employeeId").alias("employeeId"),
                root.get("attendanceRangeId").alias("attendanceRangeId"),
                root.get("date").alias("date"),
                root.get("startTime").alias("startTime"),
                root.get("endTime").alias("endTime"),
                root.get("attendanceTime").alias("attendanceTime"),
                root.get("rawStartTime").alias("rawStartTime"),
                root.get("rawEndTime").alias("rawEndTime"),
                root.get("rawAttendanceTime").alias("rawAttendanceTime"),
                root.get("leaveTime").alias("leaveTime"),
                root.get("status").alias("status"));
        TypedQuery<AttendanceReport> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}
