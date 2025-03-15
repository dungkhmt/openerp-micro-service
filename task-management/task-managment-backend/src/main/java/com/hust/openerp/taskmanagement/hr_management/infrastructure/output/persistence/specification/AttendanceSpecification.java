package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.IAttendancesFilter;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckinoutEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class AttendanceSpecification implements Specification<CheckinoutEntity> {
    private final IAttendancesFilter filter;

    @Override
    public Predicate toPredicate(Root<CheckinoutEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        // Filter by user IDs
        if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) {
            predicates.add(root.get("userId").in(filter.getUserIds()));
        }

        // Filter range
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            predicates.add(cb.between(
                    root.get("timePoint"),
                    cb.literal(filter.getStartDate()),
                    cb.literal(filter.getEndDate())
            ));
        }

        // Add order by timepoint
        if(filter.sortByDesc()){
            query.orderBy(cb.desc(root.get("timePoint")));
        }
        else query.orderBy(cb.asc(root.get("timePoint")));

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
