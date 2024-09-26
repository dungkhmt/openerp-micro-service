package com.hust.baseweb.model.querydsl;


public class SortAndFiltersInput {

    private SearchCriteria[] filters;
    private SortCriteria[] sort;

    public SortAndFiltersInput(
        SearchCriteria[] filters,
        SortCriteria[] sort
    ) {
        super();
        this.filters = filters;
        this.sort = sort;
    }

    public SortAndFiltersInput() {
        super();

    }

    public SearchCriteria[] getFilters() {
        return filters;
    }

    public void setFilters(SearchCriteria[] filters) {
        this.filters = filters;
    }

    public SortCriteria[] getSort() {
        return sort;
    }

    public void setSort(SortCriteria[] sort) {
        this.sort = sort;
    }


}
