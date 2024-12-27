package openerp.openerpresourceserver.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.out.department.filter.IDepartmentFilter;
import openerp.openerpresourceserver.application.port.out.staff.filter.IStaffFilter;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.DepartmentEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class DepartmentSpecification implements Specification<DepartmentEntity> {
    private final IDepartmentFilter filter;

    @Override
    public Predicate toPredicate(Root<DepartmentEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if(filter.getDepartmentCode() != null){
            Predicate codePredicate = cb.like(cb.lower(root.get("department_code")),
                    "%" + filter.getDepartmentCode().toLowerCase() + "%");
            predicates.add(codePredicate);
        }
        if (filter.getDepartmentName() != null){
            Predicate namePredicate = cb.like(cb.lower(root.get("department_name")),
                    "%" + filter.getDepartmentName().toLowerCase() + "%");
            predicates.add(namePredicate);
        }
        if (filter.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }

        return cb.or(predicates.toArray(new Predicate[0]));
    }
}
