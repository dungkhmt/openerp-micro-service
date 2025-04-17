package openerp.openerpresourceserver.repo.specification;

import openerp.openerpresourceserver.entity.AttendanceRange;
import org.springframework.data.jpa.domain.Specification;

public class AttendanceRangeSpecification {
    public static Specification<AttendanceRange> hasKeyword(String keyword) {
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

    public static Specification<AttendanceRange> hasStatus(Integer status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("status"), status);
        };
    }
}
