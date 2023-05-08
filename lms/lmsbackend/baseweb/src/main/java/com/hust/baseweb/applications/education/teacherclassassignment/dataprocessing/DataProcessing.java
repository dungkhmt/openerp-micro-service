package com.hust.baseweb.applications.education.teacherclassassignment.dataprocessing;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class DataProcessing {

    public static void main(String[] args) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(
            "D:/Downloads/waiting-for-classification/Xử lý dữ liệu đồ án/final/TKB20201-1909.xlsx"));
        XSSFSheet sheet = wb.getSheetAt(0);

        HashSet<String> classIds = new HashSet<>();
        HashMap<String, String> mCourseId2CreditInfo = new HashMap<>();
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            Cell c = row.getCell(2);
            classIds.add(String.valueOf(Double.valueOf(c.getNumericCellValue()).intValue()));

            c = row.getCell(4);
            String courseId = c.getStringCellValue();
            c = row.getCell(7);
            String creditInfo = c.getStringCellValue();

            mCourseId2CreditInfo.put(courseId, creditInfo);
        }

        XSSFWorkbook wb2 = new XSSFWorkbook(new FileInputStream(
            "D:/Downloads/waiting-for-classification/Xử lý dữ liệu đồ án/CNTT_20201.xlsx"));
        XSSFSheet sheet2 = wb2.getSheetAt(0);

        lastRowNum = sheet2.getLastRowNum();
        int destinationRowNum = lastRowNum + 20;
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet2.getRow(i);
            Cell c = row.getCell(2);
            String classId = String.valueOf(Double.valueOf(c.getNumericCellValue()).intValue());

            c = row.getCell(4);
            String courseId = c.getStringCellValue();
            c = row.getCell(7);
            c.setCellValue(mCourseId2CreditInfo.get(courseId));

            if (!classIds.contains(classId)) {
                System.out.println("CNTT_20201 has class with id = " + classId +
                                   " that not in 20201-1909");
                copyRow(wb2, sheet2, i, destinationRowNum++);
            }
        }

        FileOutputStream out = new FileOutputStream("D:/output.xlsx");
        wb2.write(out);
        out.close();
        System.out.println("OK");
    }

    private static void copyRow(XSSFWorkbook workbook, XSSFSheet worksheet, int sourceRowNum, int destinationRowNum) {
        // Get the source / new row
        XSSFRow newRow = worksheet.getRow(destinationRowNum);
        XSSFRow sourceRow = worksheet.getRow(sourceRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
        } else {
            newRow = worksheet.createRow(destinationRowNum);
        }

        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            XSSFCell oldCell = sourceRow.getCell(i);
            XSSFCell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                continue;
            }

            // Copy style from old cell and apply to new cell
            XSSFCellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            // If there is a cell comment, copy
            if (oldCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            if (oldCell.getCellType().equals(CellType.FORMULA)) {
                newCell.setCellFormula(oldCell.getCellFormula());
            } else {
                newCell.setCellType(oldCell.getCellType());

                // Set the cell data value
                switch (oldCell.getCellType()) {
                    case BLANK:
                        newCell.setCellValue(oldCell.getStringCellValue());
                        break;
                    case BOOLEAN:
                        newCell.setCellValue(oldCell.getBooleanCellValue());
                        break;
                    case ERROR:
                        newCell.setCellErrorValue(oldCell.getErrorCellValue());
                        break;
//                    case FORMULA:
//                        newCell.setCellFormula(oldCell.getCellFormula());
//                        break;
                    case NUMERIC:
                        newCell.setCellValue(oldCell.getNumericCellValue());
                        break;
                    case STRING:
                        newCell.setCellValue(oldCell.getRichStringCellValue());
                        break;
                }
            }
        }

        // If there are are any merged regions in the source row, copy to new row
        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(
                    newRow.getRowNum(),
                    (newRow.getRowNum() +
                     (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()
                     )),
                    cellRangeAddress.getFirstColumn(),
                    cellRangeAddress.getLastColumn());
                worksheet.addMergedRegion(newCellRangeAddress);
            }
        }
    }
}
