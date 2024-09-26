package com.hust.baseweb.rest.user;

import com.hust.baseweb.model.querydsl.SearchCriteria;
import com.querydsl.core.types.dsl.*;

public class Predicate {

    private final String fullNamePath = "person.fullName";
    private final String firstNamePath = "person.firstName";
    private final String middleNamePath = "person.middleName";
    private final String lastNamePath = "person.lastName";

    private SearchCriteria criteria;

    public Predicate(final SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public static boolean isNumeric(final Object str) {
        return str instanceof Number;
    }

    public BooleanExpression getPredicate() {
        PathBuilder<DPerson> entityPath = new PathBuilder<>(
            DPerson.class,
            "dPerson");
        if (criteria.getValue() instanceof Boolean) {
            BooleanPath path = entityPath.getBoolean(criteria.getKey());
            return path.eq((Boolean) criteria.getValue());
        }
        if (isNumeric(criteria.getValue())) {
            NumberPath<Integer> path = entityPath.getNumber(
                criteria.getKey(),
                Integer.class);
            int value = Integer.parseInt(criteria.getValue().toString());
            switch (criteria.getOperation()) {
                case ":":
                    return path.eq(value);
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
            }
        } else if (criteria.getKey().equals(fullNamePath)) {
            StringPath pathFirstName = entityPath.getString(firstNamePath);
            StringPath pathMiddleName = entityPath.getString(middleNamePath);
            StringPath pathLastName = entityPath.getString(lastNamePath);
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                // return
                // path.containsIgnoreCase(criteria.getValue().toString());
                return emptyIfNull(pathFirstName).concat(
                    emptyIfNull(pathMiddleName).concat(
                        emptyIfNull(pathLastName))).containsIgnoreCase(
                    criteria.getValue().toString());
            }
        } else {
            StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.contains(criteria.getValue().toString());
            }
        }
        return null;
    }

    public SearchCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(final SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public StringExpression emptyIfNull(StringExpression stringExpression) {
        return stringExpression.coalesce("").asString();
    }
}
