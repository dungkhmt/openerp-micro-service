package com.hust.baseweb.rest.user;

import com.hust.baseweb.model.querydsl.SearchCriteria;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.ArrayList;
import java.util.List;

public class PredicateBuilder {

    private final List<SearchCriteria> params;

    public PredicateBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public PredicateBuilder with(final String key, final String operation, final Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression build() {
        if (params.size() == 0) {
            return null;
        }

        final List<BooleanExpression> predicates = new ArrayList<>();
        Predicate predicate;
        for (final SearchCriteria param : params) {
            predicate = new Predicate(param);
            final BooleanExpression exp = predicate.getPredicate();
            if (exp != null) {
                predicates.add(exp);
            }
        }

        BooleanExpression result = predicates.get(0);
        for (int i = 1; i < predicates.size(); i++) {
            result = result.and(predicates.get(i));
        }
        return result;
    }
}
