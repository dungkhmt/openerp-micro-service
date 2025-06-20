package com.hust.openerp.taskmanagement.specification;

import com.hust.openerp.taskmanagement.entity.MeetingPlan;
import com.hust.openerp.taskmanagement.entity.MeetingPlanUser;
import com.hust.openerp.taskmanagement.entity.MeetingPlanUser_;
import com.hust.openerp.taskmanagement.entity.MeetingPlan_;
import com.hust.openerp.taskmanagement.util.SearchCriteria;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;

public class MeetingPlanSpecification extends BaseSpecification<MeetingPlan> {

    public MeetingPlanSpecification(final SearchCriteria criteria) {
        super(criteria);
    }

    @Override
    public Predicate toPredicate(Root<MeetingPlan> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        switch (criteria.getKey()) {
            case MeetingPlan_.NAME:
            case MeetingPlan_.DESCRIPTION:
            case MeetingPlan_.LOCATION:
                return this.parseStringField(root, builder);
            case MeetingPlan_.STATUS_ID:
            case MeetingPlan_.CREATED_BY:
                return this.parseIdField(root, builder);
            case MeetingPlan_.ID:
                return this.parseUUIDField(root, builder);
            case MeetingPlan_.CREATED_STAMP:
            case MeetingPlan_.REGISTRATION_DEADLINE:
                return this.parseDateField(root, builder);
            case "memberId":
                Subquery<MeetingPlanUser> subquery = query.subquery(MeetingPlanUser.class);
                Root<MeetingPlanUser> subRoot = subquery.from(MeetingPlanUser.class);
                subquery.select(subRoot);
                subquery.where(
                    builder.equal(subRoot.get(MeetingPlanUser_.meetingPlan), root),
                    builder.equal(subRoot.get(MeetingPlanUser_.USER_ID), criteria.getValue())
                );
                return builder.exists(subquery);
            default:
                return null;
        }
    }

    @Override
    protected <X> Predicate parseIdField(final Path<X> path, final CriteriaBuilder builder) {
        // * May be null
        if (criteria.getValue().toString().equalsIgnoreCase("null")) {
            return switch (criteria.getOperation()) {
                case EQUALITY -> builder.isNull(path.get(criteria.getKey()));
                case NEGATION -> builder.isNotNull(path.get(criteria.getKey()));
                default -> null;
            };
        }

        // * May be check in the list
        var values = criteria.getValue().toString().split(",");
        if (values.length > 1) {
            return switch (criteria.getOperation()) {
                case EQUALITY -> path.get(criteria.getKey()).in((Object[]) values);
                case NEGATION -> builder.not(path.get(criteria.getKey()).in((Object[]) values));
                default -> null;
            };
        }

        return super.parseIdField(path, builder);
    }

    @Override
    @Nullable
    protected final <X> Predicate parseDateField(final Path<X> path, final CriteriaBuilder builder) {
        // * May be null
        if (criteria.getValue().toString().equalsIgnoreCase("null")) {
            return switch (criteria.getOperation()) {
                case EQUALITY -> builder.isNull(path.get(criteria.getKey()));
                case NEGATION -> builder.isNotNull(path.get(criteria.getKey()));
                default -> null;
            };
        }

        return super.parseDateField(path, builder);
    }
}