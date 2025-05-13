package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.filter.IPayrollDetailFilter;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.PayrollDetailEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class PayrollDetailSpecification implements Specification<PayrollDetailEntity> {
    private final IPayrollDetailFilter filter;

    @Override
    public Predicate toPredicate(Root<PayrollDetailEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();
        if(filter.getUserLoginIds() != null){
            Predicate predicate = root.get("userId").in(filter.getUserLoginIds());
            predicates.add(predicate);
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
