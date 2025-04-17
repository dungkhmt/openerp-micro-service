package openerp.openerpresourceserver.repo.specification;

import openerp.openerpresourceserver.entity.Organization;
import org.springframework.data.jpa.domain.Specification;

public class OrganizationSpecification {
    public static Specification<Organization> hasKeyword(String keyword) {
        return (root, query, builder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return builder.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("name")), likePattern)
            );
        };
    }

    public static Specification<Organization> hasType(Integer type) {
        return (root, query, builder) -> {
            if (type == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("type"), type);
        };
    }

    public static Specification<Organization> hasStatus(Integer status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Organization> hasNoParent() {
        return (root, query, builder) -> builder.isNull(root.get("parent"));
    }
}
