package openerp.openerpresourceserver.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.out.staff.filter.IStaffFilter;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffDepartmentEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
public class StaffInfoSpecification implements Specification<StaffEntity> {
    private final IStaffFilter filter;

    @Override
    public Predicate toPredicate(Root<StaffEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var orPredicates = new ArrayList<Predicate>();
        var predicates = new ArrayList<Predicate>();

        // Filter by Staff Code
        if (filter.getStaffCode() != null) {
            Predicate codePredicate = cb.like(cb.lower(root.get("staffCode")),
                    "%" + filter.getStaffCode().toLowerCase() + "%");
            orPredicates.add(codePredicate);
        }

        // Filter by Staff Name
        if (filter.getStaffName() != null) {
            Predicate namePredicate = cb.like(cb.lower(root.get("fullname")),
                    "%" + filter.getStaffName().toLowerCase() + "%");
            orPredicates.add(namePredicate);
        }

        // Filter by Staff Email
        if (filter.getStaffEmail() != null) {
            var userJoin = root.join("user", JoinType.INNER); // Join with User table
            Predicate emailPredicate = cb.like(cb.lower(userJoin.get("email")),
                    "%" + filter.getStaffEmail().toLowerCase() + "%");
            orPredicates.add(emailPredicate);
        }

        // Combine OR predicates
        if (!orPredicates.isEmpty()) {
            predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
        }

        // Filter by Staff Status
        if (filter.getStatus() != null) {
            Predicate statusPredicate = cb.equal(root.get("status"), filter.getStatus());
            predicates.add(statusPredicate);
        }

        // Join and filter by Department
        if (filter.getDepartmentCode() != null) {
            predicates.add(getDepartmentPredicate(root, query, cb));
        }

        // Join and filter by Job Position
        if (filter.getJobPositionCode() != null) {
            predicates.add(getJobPositionPredicate(root, query, cb));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate getDepartmentPredicate(Root<StaffEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // Subquery to fetch the latest fromDate for StaffDepartmentEntity
        Subquery<LocalDate> latestDepartmentSubquery = query.subquery(LocalDate.class);
        Root<StaffDepartmentEntity> departmentSubRoot = latestDepartmentSubquery.from(StaffDepartmentEntity.class);

        latestDepartmentSubquery.select(cb.greatest(departmentSubRoot.get("id").get("fromDate").as(LocalDate.class)))
                .where(cb.equal(departmentSubRoot.get("id").get("userId"), root.get("user").get("id")));

        // Join StaffDepartmentEntity
        Root<StaffDepartmentEntity> departmentJoin = query.from(StaffDepartmentEntity.class);
        return cb.and(
                cb.equal(departmentJoin.get("id").get("departmentCode"), filter.getDepartmentCode()),
                cb.equal(departmentJoin.get("id").get("fromDate"), latestDepartmentSubquery),
                cb.equal(departmentJoin.get("id").get("userId"), root.get("user").get("id"))
        );
    }

    private Predicate getJobPositionPredicate(Root<StaffEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // Subquery to fetch the latest fromDate for StaffJobPositionEntity
        Subquery<LocalDate> latestJobPositionSubquery = query.subquery(LocalDate.class);
        Root<StaffJobPositionEntity> jobPositionSubRoot = latestJobPositionSubquery.from(StaffJobPositionEntity.class);

        latestJobPositionSubquery.select(cb.greatest(jobPositionSubRoot.get("id").get("fromDate").as(LocalDate.class)))
                .where(cb.equal(jobPositionSubRoot.get("id").get("userId"), root.get("user").get("id")));

        // Join StaffJobPositionEntity
        Root<StaffJobPositionEntity> jobPositionJoin = query.from(StaffJobPositionEntity.class);
        return cb.and(
                cb.equal(jobPositionJoin.get("id").get("positionCode"), filter.getJobPositionCode()),
                cb.equal(jobPositionJoin.get("id").get("fromDate"), latestJobPositionSubquery),
                cb.equal(jobPositionJoin.get("id").get("userId"), root.get("user").get("id"))
        );
    }

}
