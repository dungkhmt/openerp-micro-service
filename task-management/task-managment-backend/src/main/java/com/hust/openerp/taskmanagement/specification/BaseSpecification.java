package com.hust.openerp.taskmanagement.specification;

import org.springframework.data.jpa.domain.Specification;

import com.hust.openerp.taskmanagement.util.SearchCriteria;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;

public abstract class BaseSpecification<T> implements Specification<T> {
  @Getter
  protected transient SearchCriteria criteria;

  protected BaseSpecification(final SearchCriteria criteria) {
    super();
    this.criteria = criteria;
  }

  @Nullable
  protected Predicate parseStringField(final Root<T> root,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
      case LIKE -> builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
      case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
      case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
      case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
      default -> null;
    };
  }

  @Nullable
  protected Predicate parseDateField(final Root<T> root, final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
      case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
      case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
      default -> null;
    };
  }

  @Nullable
  protected Predicate parseNumberField(final Root<T> root,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
      case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
      case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
      default -> null;
    };
  }

  @Nullable
  protected Predicate parseBooleanField(final Root<T> root,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
      default -> null;
    };
  }

  @Nullable
  protected Predicate parseIdField(final Root<T> root,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
      default -> null;
    };
  }
}
