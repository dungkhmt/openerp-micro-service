package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.filter.ICheckpointPeriodConfigureFilter;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckpointPeriodConfigureEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class CheckpointPeriodConfigureSpecification implements Specification<CheckpointPeriodConfigureEntity> {
    private final ICheckpointPeriodConfigureFilter filter;

    @Override
    public Predicate toPredicate(Root<CheckpointPeriodConfigureEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if (filter.getPeriodId() != null) {
            predicates.add(cb.equal(root.get("id").get("checkpointPeriodId"), filter.getPeriodId()));
        }

        if (filter.getStatus() != null){
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
