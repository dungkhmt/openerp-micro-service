package openerp.openerpresourceserver.repo.specification;

import openerp.openerpresourceserver.entity.Position;
import org.springframework.data.jpa.domain.Specification;

public class PositionSpecification {
    public static Specification<Position> hasKeyword(String keyword) {
        return (root, query, builder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return builder.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("name")), likePattern),
                    builder.like(builder.lower(root.get("description")), likePattern)
            );
        };
    }
    public static Specification<Position> hasStatus(Integer status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("status"), status);
        };
    }
}
