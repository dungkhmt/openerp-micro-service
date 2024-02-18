package com.hust.openerp.taskmanagement.dto.dao;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.util.SearchCriteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;

public class ProjectSpecification implements Specification<Project> {
  @Getter
  private SearchCriteria criteria;

  public ProjectSpecification(final SearchCriteria criteria) {
    super();
    this.criteria = criteria;
  }

  @Override
  @Nullable
  public Predicate toPredicate(final Root<Project> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {

    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
      case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
      case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
      case LIKE -> builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
      case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
      case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
      case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
      default -> null;
    };
  }
}
