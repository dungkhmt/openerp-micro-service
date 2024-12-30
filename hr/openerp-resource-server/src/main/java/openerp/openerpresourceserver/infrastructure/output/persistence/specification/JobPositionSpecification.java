package openerp.openerpresourceserver.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.out.job_position.filter.IJobPositionFilter;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.JobPositionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class JobPositionSpecification implements Specification<JobPositionEntity> {
    private final IJobPositionFilter filter;

    @Override
    public Predicate toPredicate(Root<JobPositionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if(filter.getCode() != null){
            Predicate codePredicate = cb.like(cb.lower(root.get("positionCode")),
                    "%" + filter.getCode().toLowerCase() + "%");
            predicates.add(codePredicate);
        }
        if (filter.getName() != null){
            Predicate namePredicate = cb.like(cb.lower(root.get("positionName")),
                    "%" + filter.getName().toLowerCase() + "%");
            predicates.add(namePredicate);
        }

        return cb.or(predicates.toArray(new Predicate[0]));
    }
}
