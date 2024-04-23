package com.hust.openerp.taskmanagement.specification;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.hust.openerp.taskmanagement.util.SearchCriteria;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;

public abstract class BaseSpecification<T> implements Specification<T> {
  @Getter
  protected transient SearchCriteria criteria;

  protected BaseSpecification(final SearchCriteria criteria) {
    super();
    this.criteria = criteria;
  }

  @Nullable
  protected <X> Predicate parseStringField(final Path<X> path,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(path.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(path.get(criteria.getKey()), criteria.getValue());
      case LIKE ->
        builder.like(builder.upper(path.get(criteria.getKey())), criteria.getValue().toString().toUpperCase());
      case STARTS_WITH ->
        builder.like(builder.upper(path.get(criteria.getKey())), criteria.getValue().toString().toUpperCase() + "%");
      case ENDS_WITH ->
        builder.like(builder.upper(path.get(criteria.getKey())), "%" + criteria.getValue().toString().toUpperCase());
      case CONTAINS -> builder.like(builder.upper(path.get(criteria.getKey())),
          "%" + criteria.getValue().toString().toUpperCase() + "%");
      default -> null;
    };
  }

  @Nullable
  protected <X> Predicate parseDateField(final Path<X> path, final CriteriaBuilder builder) {
    long unixTime = Long.parseLong(criteria.getValue().toString());
    Date date = new Date(unixTime * 1000L); // Unix time is in seconds, Date constructor expects milliseconds

    return switch (criteria.getOperation()) {
      case GREATER_THAN -> builder.greaterThan(path.<Date>get(criteria.getKey()), date);
      case LESS_THAN -> builder.lessThan(path.<Date>get(criteria.getKey()), date);
      case GREATER_THAN_OR_EQUAL ->
        builder.greaterThanOrEqualTo(path.<Date>get(criteria.getKey()), date);
      case LESS_THAN_OR_EQUAL -> builder.lessThanOrEqualTo(path.<Date>get(criteria.getKey()), date);
      default -> null;
    };
  }

  @Nullable
  protected <X> Predicate parseNumberField(final Path<X> path,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(path.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(path.get(criteria.getKey()), criteria.getValue());
      case GREATER_THAN -> builder.greaterThan(path.get(criteria.getKey()), criteria.getValue().toString());
      case LESS_THAN -> builder.lessThan(path.get(criteria.getKey()), criteria.getValue().toString());
      case GREATER_THAN_OR_EQUAL ->
        builder.greaterThanOrEqualTo(path.get(criteria.getKey()), criteria.getValue().toString());
      case LESS_THAN_OR_EQUAL ->
        builder.lessThanOrEqualTo(path.get(criteria.getKey()), criteria.getValue().toString());
      default -> null;
    };
  }

  @Nullable
  protected <X> Predicate parseBooleanField(final Path<X> path,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(path.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(path.get(criteria.getKey()), criteria.getValue());
      default -> null;
    };
  }

  @Nullable
  protected <X> Predicate parseIdField(final Path<X> path,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(path.get(criteria.getKey()), criteria.getValue());
      case NEGATION -> builder.notEqual(path.get(criteria.getKey()), criteria.getValue());
      default -> null;
    };
  }

  @Nullable
  protected <X> Predicate parseUUIDField(final Path<X> path,
      final CriteriaBuilder builder) {
    return switch (criteria.getOperation()) {
      case EQUALITY -> builder.equal(path.get(criteria.getKey()), UUID.fromString(criteria.getValue().toString()));
      case NEGATION -> builder.notEqual(path.get(criteria.getKey()), UUID.fromString(criteria.getValue().toString()));
      default -> null;
    };
  }
}
