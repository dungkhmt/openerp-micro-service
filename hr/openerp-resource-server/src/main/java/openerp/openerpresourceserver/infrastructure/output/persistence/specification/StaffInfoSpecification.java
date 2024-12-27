package openerp.openerpresourceserver.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.out.checkinout.filter.ICheckinoutFilter;
import openerp.openerpresourceserver.application.port.out.staff.filter.IStaffFilter;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckinoutEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
public class StaffInfoSpecification implements Specification<StaffEntity> {
    private final IStaffFilter filter;

    @Override
    public Predicate toPredicate(Root<StaffEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if(filter.getStaffCode() != null){
            Predicate codePredicate = cb.like(cb.lower(root.get("staff_code")),
                    "%" + filter.getStaffCode().toLowerCase() + "%");
            predicates.add(codePredicate);
        }
        if (filter.getStaffName() != null){
            Predicate namePredicate = cb.like(cb.lower(root.get("fullname")),
                    "%" + filter.getStaffName().toLowerCase() + "%");
            predicates.add(namePredicate);
        }
        if (filter.getStaffEmail() != null) {
            var userJoin = root.join("user_login", JoinType.INNER);
            Predicate emailPredicate = cb.like(cb.lower(userJoin.get("email")),
                    "%" + filter.getStaffEmail().toLowerCase() + "%");
            predicates.add(emailPredicate);
        }

        return cb.or(predicates.toArray(new Predicate[0]));
    }
}
