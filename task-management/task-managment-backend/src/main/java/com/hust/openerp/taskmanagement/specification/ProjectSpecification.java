package com.hust.openerp.taskmanagement.specification;

import org.springframework.lang.Nullable;

import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.Project_;
import com.hust.openerp.taskmanagement.util.SearchCriteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProjectSpecification extends BaseSpecification<Project> {

  public ProjectSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  @Nullable
  public Predicate toPredicate(final Root<Project> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
    switch (criteria.getKey()) {
      case Project_.CODE:
      case Project_.NAME:
      case Project_.DESCRIPTION:
        return this.parseStringField(root, builder);
      case Project_.CREATED_STAMP:
      case Project_.LAST_UPDATED_STAMP:
        return this.parseDateField(root, builder);
      default:
        return null;
    }
  }
}
