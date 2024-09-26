package com.hust.openerp.taskmanagement.specification;

import com.hust.openerp.taskmanagement.entity.Project_;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.Task_;
import com.hust.openerp.taskmanagement.util.SearchCriteria;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class TaskSpecification extends BaseSpecification<Task> {

  public TaskSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    switch (criteria.getKey()) {
      case Task_.NAME:
      case Task_.DESCRIPTION:
        return this.parseStringField(root, builder);
      case Task_.ASSIGNEE_ID:
      case Task_.CREATOR_ID:
      case Task_.PRIORITY_ID:
      case Task_.STATUS_ID:
      case Task_.CATEGORY_ID:
        return this.parseIdField(root, builder);
      case Task_.PROJECT_ID:
        return this.parseUUIDField(root, builder);
      case Task_.CREATED_DATE:
      case Task_.CREATED_STAMP:
      case Task_.LAST_UPDATED_STAMP:
      case Task_.DUE_DATE:
      case Task_.FROM_DATE:
        return this.parseDateField(root, builder);
      case Task_.ESTIMATED_TIME:
      case Task_.PROGRESS:
        return this.parseNumberField(root, builder);
      case "projectName":
        var projectJoin = root.join(Task_.project);
        criteria.setKey(Project_.NAME);
        return this.parseStringField(projectJoin, builder);
      default:
        return null;
    }
  }

  @Override
  @Nullable
  protected final <X> Predicate parseIdField(final Path<X> path, final CriteriaBuilder builder) {
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

  @Override
  @Nullable
  protected final <X> Predicate parseNumberField(final Path<X> path, final CriteriaBuilder builder) {
    // * May be null
    if (criteria.getValue().toString().equalsIgnoreCase("null")) {
      return switch (criteria.getOperation()) {
        case EQUALITY -> builder.isNull(path.get(criteria.getKey()));
        case NEGATION -> builder.isNotNull(path.get(criteria.getKey()));
        default -> null;
      };
    }

    return super.parseNumberField(path, builder);
  }
}
