package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.out.department.filter.IDepartmentFilter;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.DepartmentEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DepartmentSpecification implements Specification<DepartmentEntity> {
    private final IDepartmentFilter filter;

    @Override
    public Predicate toPredicate(Root<DepartmentEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        List<Predicate> orPredicates = new ArrayList<>();
        if (filter.getDepartmentCode() != null) {
            Predicate codePredicate = cb.like(cb.lower(root.get("departmentCode")),
                    "%" + filter.getDepartmentCode().toLowerCase() + "%");
            orPredicates.add(codePredicate);
        }
        if (filter.getDepartmentName() != null) {
            Predicate namePredicate = cb.like(cb.lower(root.get("departmentName")),
                    "%" + filter.getDepartmentName().toLowerCase() + "%");
            orPredicates.add(namePredicate);
        }

        if (!orPredicates.isEmpty()) {
            predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
        }

        if (filter.getStatus() != null) {
            Predicate statusPredicate = cb.equal(root.get("status"), filter.getStatus());
            predicates.add(statusPredicate);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

}
