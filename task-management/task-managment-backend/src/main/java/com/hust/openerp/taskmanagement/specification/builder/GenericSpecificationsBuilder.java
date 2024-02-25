package com.hust.openerp.taskmanagement.specification.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import com.hust.openerp.taskmanagement.util.SearchCriteria;
import com.hust.openerp.taskmanagement.util.SearchOperation;

import jakarta.annotation.Nullable;

public class GenericSpecificationsBuilder<E> {

  private final List<SearchCriteria> params;

  public GenericSpecificationsBuilder() {
    this.params = new ArrayList<>();
  }

  public final GenericSpecificationsBuilder<E> with(final String key, final String operation, final Object value) {
    return with(null, key, operation, value, null, null);
  }

  public final GenericSpecificationsBuilder<E> with(final String key, final String operation, final Object value,
      final String prefix, final String suffix) {
    return with(null, key, operation, value, prefix, suffix);
  }

  public final GenericSpecificationsBuilder<E> with(final String precedenceIndicator, final String key,
      final String operation, final Object value, final String prefix, final String suffix) {
    SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
    if (op != null) {
      if (op == SearchOperation.EQUALITY) // the operation may be complex operation
      {
        final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
        final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

        if (startWithAsterisk && endWithAsterisk) {
          op = SearchOperation.CONTAINS;
        } else if (startWithAsterisk) {
          op = SearchOperation.ENDS_WITH;
        } else if (endWithAsterisk) {
          op = SearchOperation.STARTS_WITH;
        }
      }
      params.add(new SearchCriteria(precedenceIndicator, key, op, value));
    }
    return this;
  }

  @Nullable
  public Specification<E> build(Function<SearchCriteria, Specification<E>> converter) {

    if (params.isEmpty()) {
      return null;
    }

    final List<Specification<E>> specs = params.stream()
        .map(converter)
        .collect(Collectors.toCollection(ArrayList::new));

    Specification<E> result = specs.get(0);

    for (int idx = 1; idx < specs.size(); idx++) {
      result = params.get(idx)
          .isOrPredicate()
              ? Specification.where(result)
                  .or(specs.get(idx))
              : Specification.where(result)
                  .and(specs.get(idx));
    }

    return result;
  }

  public Specification<E> build(Deque<?> postFixedExprStack, Function<SearchCriteria, Specification<E>> converter) {

    Deque<Specification<E>> specStack = new LinkedList<>();
    Collections.reverse((List<?>) postFixedExprStack);

    while (!postFixedExprStack.isEmpty()) {
      Object mayBeOperand = postFixedExprStack.pop();

      if (mayBeOperand instanceof SearchCriteria criteria) {
        specStack.push(converter.apply(criteria));
      } else {
        Specification<E> operand1 = specStack.pop();
        Specification<E> operand2 = specStack.pop();
        if (mayBeOperand.equals(SearchOperation.AND_OPERATOR))
          specStack.push(Specification.where(operand1)
              .and(operand2));
        else if (mayBeOperand.equals(SearchOperation.OR_OPERATOR))
          specStack.push(Specification.where(operand1)
              .or(operand2));
      }

    }
    return specStack.pop();

  }
}