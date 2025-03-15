package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.filter.ICheckpointConfigureFilter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.filter.ICheckpointPeriodFilter;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckpointConfigureEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckpointPeriodEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class CheckpointPeriodSpecification implements Specification<CheckpointPeriodEntity> {
    private final ICheckpointPeriodFilter filter;

    @Override
    public Predicate toPredicate(Root<CheckpointPeriodEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if (filter.getStatus() != null){
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }

        if(filter.getName() != null){
            Predicate namePredicate = cb.like(cb.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%");
            predicates.add(namePredicate);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
