/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package wms.bean;

/**
 * Doi tuong loi khi thuc hien nghiep vu Import Excel.
 *
 * @author HuyenNV1@viettel.com.vn
 * @version 1.0
 * @since 1.0
 */
public class ImportErrorBean {

    /**
     * Dong
     */
    private int row;
    /**
     * Cot
     */
    private int column;
    /**
     * Nhan cua cot
     */
    private String columnLabel;
    /**
     * Mo ta
     */
    private String description;
    /**
     * Noi dung file Excel
     */
    private String content;

    /**
     * Ham tao mac dinh.
     */
    public ImportErrorBean() {
    }

    /**
     * Ham tao.
     *
     * @param row         Hang
     * @param icolumn     Cot
     * @param description Mo ta
     * @param content     Noi dung
     */
    public ImportErrorBean(int row, int icolumn, String description, String content) {
        final int ALPHABET_NUMBER = 26; // so chu cai
        this.row = row + 1;
        this.column = icolumn + 1;
        this.description = description;
        this.content = content;

        //this.columnLabel = Character.forDigit(Character.digit('A', 10), 10);
        if (icolumn < ALPHABET_NUMBER) {
            this.columnLabel = String.valueOf((char) ('A' + icolumn));
        } else {
            int temp = icolumn / ALPHABET_NUMBER;
            icolumn -= ALPHABET_NUMBER * temp;
            String result = String.valueOf((char) ('A' + temp - 1));
            this.columnLabel = result + (char) ('A' + icolumn);
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public void setColumnLabel(String columnLabel) {
        this.columnLabel = columnLabel;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
