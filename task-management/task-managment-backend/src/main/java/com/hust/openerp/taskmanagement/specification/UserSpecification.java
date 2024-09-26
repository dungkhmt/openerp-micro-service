package com.hust.openerp.taskmanagement.specification;

import org.springframework.lang.Nullable;

import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.entity.User_;
import com.hust.openerp.taskmanagement.util.SearchCriteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserSpecification extends BaseSpecification<User> {
  public UserSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  @Nullable
  public Predicate toPredicate(final Root<User> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
    switch (criteria.getKey()) {
      case User_.ID:
      case User_.FIRST_NAME:
      case User_.LAST_NAME:
        return this.parseStringField(root, builder);
      default:
        return null;
    }
  }
}
