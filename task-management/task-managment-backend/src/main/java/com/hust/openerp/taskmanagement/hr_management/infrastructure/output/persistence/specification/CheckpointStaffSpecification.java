package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointStaffEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class CheckpointStaffSpecification implements Specification<CheckpointStaffEntity> {
    private final ICheckpointStaffFilter filter;

    @Override
    public Predicate toPredicate(Root<CheckpointStaffEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if (filter.getUserId() != null) {
            predicates.add(cb.equal(root.get("id").get("userId"), filter.getUserId()));
        }
        else if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) {
            predicates.add(root.get("id").get("userId").in(filter.getUserIds()));
        }

        if (filter.getPeriodId() != null) {
            predicates.add(cb.equal(root.get("id").get("checkpointPeriodId"), filter.getPeriodId()));
        }

        /*if (filter.getConfigureIds() != null && !filter.getConfigureIds().isEmpty()) {
            predicates.add(root.get("id").get("checkpointCode").in(filter.getConfigureIds()));
        }*/

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
