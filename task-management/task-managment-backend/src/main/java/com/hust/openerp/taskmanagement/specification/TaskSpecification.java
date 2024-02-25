package com.hust.openerp.taskmanagement.specification;

import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.TaskAssignment_;
import com.hust.openerp.taskmanagement.entity.Task_;
import com.hust.openerp.taskmanagement.util.SearchCriteria;
import com.hust.openerp.taskmanagement.util.SearchOperation;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
      case Task_.CREATED_BY_USER_ID:
      case Task_.PRIORITY_ID:
      case Task_.STATUS_ID:
        return this.parseIdField(root, builder);
      case Task_.CREATED_DATE:
      case Task_.DUE_DATE:
      case Task_.LAST_UPDATED_STAMP:
      case Task_.FROM_DATE:
        return this.parseDateField(root, builder);
      case "assignee":
        return builder.equal(root.join(Task_.assignment).get(TaskAssignment_.assigneeId), criteria.getValue());
      default:
        return null;
    }
  }

  @Override
  @Nullable
  protected final Predicate parseIdField(final Root<Task> root, final CriteriaBuilder builder) {
    if (criteria.getOperation() == SearchOperation.EQUALITY) {
      return builder.equal(root.get(criteria.getKey()), criteria.getValue());
    }

    return null;
  }

}
