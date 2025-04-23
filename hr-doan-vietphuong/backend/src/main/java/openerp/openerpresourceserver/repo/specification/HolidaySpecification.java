package openerp.openerpresourceserver.repo.specification;

import openerp.openerpresourceserver.entity.Holiday;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class HolidaySpecification {

    public static Specification<Holiday> hasFromDate(LocalDate from) {
        return (root, query, builder) -> {
            if (from == null) {
                return builder.conjunction();
            }
            return builder.greaterThanOrEqualTo(root.get("date"), from);
        };
    }

    public static Specification<Holiday> hasToDate(LocalDate to) {
        return (root, query, builder) -> {
            if (to == null) {
                return builder.conjunction();
            }
            return builder.lessThanOrEqualTo(root.get("date"), to);
        };
    }

    public static Specification<Holiday> hasType(Integer type) {
        return (root, query, builder) -> {
            if (type == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("type"), type);
        };
    }

    public static Specification<Holiday> hasStatus(Integer status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("status"), status);
        };
    }
}
