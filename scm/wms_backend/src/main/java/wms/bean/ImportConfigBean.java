/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package wms.bean;

/**
 * Cau hinh import file Excel.
 *
 * @author HuyenNV
 * @version 1.0
 * @since 1.0
 */
public class ImportConfigBean {

    //Ten cot trong file Excel
    private String excelColumn;
    //0: NUMBER (long), 1: NUMBER (double), 2: VARCHAR2, 3: DATE
    private Long type;
    //Co the null hay khong
    private Boolean nullable;
    //Do dai du lieu
    private Long length;
    //Bo qua, khong day vao CSDL
    private Boolean ignore;
    //Kiem tra trung du lieu
    private Boolean checkDuplicate;
    //Gia tri nho nhat voi truong hop kieu du lieu la so thuc
    private Double minValue;
    //Gia tri lon nhat voi truong hop kieu du lieu la so thuc
    private Double maxValue;
    //Cot day vao CSDL
    private String databaseColumn;
    //Co the lay gia tri nho nhat
    private Boolean containsMinValue;
    //Co the lay gia tri lon nhat
    private Boolean containsMaxValue;

    /**
     * Constructor. Mac dinh la bo qua.
     */
//    public ImportConfigBean() {
//    }

    /**
     * Thiet lap gia tri mac dinh.
     */
    public void setValues() {
        this.ignore = true;
        this.databaseColumn = "";
    }

    /**
     * Set cac gia tri.
     *
     * @param databaseColumn
     */
    public void setValues(String databaseColumn) {
        this.ignore = true;
        this.databaseColumn = databaseColumn;
    }

    /**
     * Set cac gia tri.
     *
     * @param excelColumn
     * @param type
     * @param nullable
     * @param length
     * @param checkDuplicate
     * @param minValue
     * @param maxValue
     * @param databaseColumn
     */
    public void setValues(String excelColumn, Long type, Boolean nullable, Long length,
                          Boolean checkDuplicate, Double minValue, Double maxValue, String databaseColumn,
                          Boolean containsMinValue, Boolean containsMaxValue) {
        this.ignore = false;
        this.databaseColumn = databaseColumn;

        this.excelColumn = excelColumn;
        this.type = type;
        this.nullable = nullable;
        this.length = length;
        this.checkDuplicate = checkDuplicate;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.containsMinValue = containsMinValue;
        this.containsMaxValue = containsMaxValue;
    }

    public String getExcelColumn() {
        return excelColumn;
    }

    public void setExcelColumn(String excelColumn) {
        this.excelColumn = excelColumn;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    public Boolean getCheckDuplicate() {
        return checkDuplicate;
    }

    public void setCheckDuplicate(Boolean checkDuplicate) {
        this.checkDuplicate = checkDuplicate;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public String getDatabaseColumn() {
        return databaseColumn;
    }

    public void setDatabaseColumn(String databaseColumn) {
        this.databaseColumn = databaseColumn;
    }

    public Boolean getContainsMinValue() {
        return containsMinValue;
    }

    public void setContainsMinValue(Boolean containsMinValue) {
        this.containsMinValue = containsMinValue;
    }

    public Boolean getContainsMaxValue() {
        return containsMaxValue;
    }

    public void setContainsMaxValue(Boolean containsMaxValue) {
        this.containsMaxValue = containsMaxValue;
    }
}
