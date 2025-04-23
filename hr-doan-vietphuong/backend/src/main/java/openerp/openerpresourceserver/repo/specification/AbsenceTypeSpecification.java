package openerp.openerpresourceserver.repo.specification;

import openerp.openerpresourceserver.entity.AbsenceType;
import org.springframework.data.jpa.domain.Specification;

public class AbsenceTypeSpecification {
    public static Specification<AbsenceType> hasKeyword(String keyword) {
        return (root, query, builder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return builder.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("code")), likePattern),
                    builder.like(builder.lower(root.get("description")), likePattern)
            );
        };
    }

    public static Specification<AbsenceType> hasStatus(Integer status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<AbsenceType> hasType(Integer type) {
        return (root, query, builder) -> {
            if (type == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("type"), type);
        };
    }
}
