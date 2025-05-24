package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.filter.IPayrollFilter;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.PayrollEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class PayrollSpecification implements Specification<PayrollEntity> {
    private final IPayrollFilter filter;

    @Override
    public Predicate toPredicate(Root<PayrollEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();
        if(filter.getSearchName() != null){
            Predicate namePredicate = cb.like(cb.lower(root.get("name")),
                "%" + filter.getSearchName().toLowerCase() + "%");
            predicates.add(namePredicate);
        }
        if(filter.getStatus() != null){
            Predicate statusPredicate = cb.equal(root.get("status"), filter.getStatus());
            predicates.add(statusPredicate);
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
