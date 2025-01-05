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
import java.util.List;

@RequiredArgsConstructor
public class JobPositionSpecification implements Specification<JobPositionEntity> {
    private final IJobPositionFilter filter;

    @Override
    public Predicate toPredicate(Root<JobPositionEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var orPredicates = new ArrayList<Predicate>();
        List<Predicate> predicates = new ArrayList<>();
        if(filter.getCode() != null){
            Predicate codePredicate = cb.like(cb.lower(root.get("positionCode")),
                    "%" + filter.getCode().toLowerCase() + "%");
            orPredicates.add(codePredicate);
        }
        if (filter.getName() != null){
            Predicate namePredicate = cb.like(cb.lower(root.get("positionName")),
                    "%" + filter.getName().toLowerCase() + "%");
            orPredicates.add(namePredicate);
        }
        if (!orPredicates.isEmpty()) {
            predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
        }

        if (filter.getStatus() != null) {
            Predicate statusPredicate = cb.equal(root.get("status"), filter.getStatus());
            predicates.add(statusPredicate);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
