package com.hust.baseweb.model.querydsl;

public class SortCriteria {

    private String field;
    private boolean isAsc;

    public SortCriteria(String field, boolean isAsc) {
        super();
        this.field = field;
        this.isAsc = isAsc;
    }

    public SortCriteria() {
        super();

    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isAsc() {
        return isAsc;
    }

    public void setAsc(boolean isAsc) {
        this.isAsc = isAsc;
    }

}
