package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.filter.ICheckinoutFilter;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckinoutEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
public class CheckinoutSpecification implements Specification<CheckinoutEntity> {
    private final ICheckinoutFilter filter;

    @Override
    public Predicate toPredicate(Root<CheckinoutEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();
        if(filter.getUserId() != null){
            predicates.add(cb.equal(root.get("userId"), filter.getUserId()));
        }
        if (filter.getType() != null){
            predicates.add(cb.equal(root.get("checkinout"), String.valueOf(filter.getType().ordinal())));
        }
        if(filter.getDate() != null){
            predicates.add(cb.equal(
                    cb.function("DATE", LocalDate.class, root.get("timePoint")),
                    filter.getDate()
            ));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
