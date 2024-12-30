package openerp.openerpresourceserver.infrastructure.output.persistence.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.filter.ICheckpointConfigureFilter;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointConfigureEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

@RequiredArgsConstructor
public class CheckpointConfigureSpecification implements Specification<CheckpointConfigureEntity> {
    private final ICheckpointConfigureFilter filter;

    @Override
    public Predicate toPredicate(Root<CheckpointConfigureEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        if(filter.getName() != null){
            Predicate namePredicate = cb.like(cb.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%");
            predicates.add(namePredicate);
        }
        if (filter.getStatus() != null){
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
