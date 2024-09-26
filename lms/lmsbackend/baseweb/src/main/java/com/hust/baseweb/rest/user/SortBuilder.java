package com.hust.baseweb.rest.user;

import com.hust.baseweb.model.querydsl.SortCriteria;
import com.hust.baseweb.utils.CommonUtils;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;


public class SortBuilder {

    private final String fullNamePath = "wrappedPerson.fullName";
    private final String firstNamePath = "wrappedPerson.firstName";
    private final String middleNamePath = "wrappedPerson.middleName";
    private final String lastNamePath = "wrappedPerson.lastName";
    List<SortCriteria> sort;

    public SortBuilder() {
        super();
        this.sort = new ArrayList<SortCriteria>();
    }

    public void add(String key, boolean isAsc) {
        if (key.equals(fullNamePath)) {
            sort.add(new SortCriteria(lastNamePath, isAsc));
            sort.add(new SortCriteria(middleNamePath, isAsc));
            sort.add(new SortCriteria(firstNamePath, isAsc));
        } else {
            sort.add(new SortCriteria(key, isAsc));
        }
    }

    public Sort build() {
        return CommonUtils.buildSortBySortCriteria(sort);
    }
}
