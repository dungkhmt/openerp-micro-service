package openerp.openerpresourceserver.repo.specification;

import jakarta.persistence.criteria.Predicate;
import openerp.openerpresourceserver.entity.Absence;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AbsenceSpecification {

    public static Specification<Absence> hasUserEmail(String email) {
        return (root, query, builder) -> {
            if (email == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("user").get("email"), email);
        };
    }

    public static Specification<Absence> hasStatus(Integer status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Absence> hasType(Integer type) {
        return (root, query, builder) -> {
            if (type == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("type"), type);
        };
    }

    public static Specification<Absence> hasCreatedAtBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (fromDate != null && toDate != null) {
                LocalDateTime from = fromDate.atStartOfDay();
                LocalDateTime to = toDate.plusDays(1).atStartOfDay().minusSeconds(1);
                predicate = builder.between(root.get("createdAt"), from, to);
            } else if (fromDate != null) {
                LocalDateTime from = fromDate.atStartOfDay();
                predicate = builder.greaterThanOrEqualTo(root.get("createdAt"), from);
            } else if (toDate != null) {
                LocalDateTime to = toDate.plusDays(1).atStartOfDay().minusSeconds(1);
                predicate = builder.lessThanOrEqualTo(root.get("createdAt"), to);
            }

            return predicate;
        };
    }

    public static Specification<Absence> hasNotUserEmail(String email) {
        return (root, query, builder) -> builder.notEqual(root.get("user").get("email"), email);
    }

    public static Specification<Absence> hasLeadId(Long leadId) {
        return (root, query, builder) -> builder.equal(root.get("leadId"), leadId);
    }
}
