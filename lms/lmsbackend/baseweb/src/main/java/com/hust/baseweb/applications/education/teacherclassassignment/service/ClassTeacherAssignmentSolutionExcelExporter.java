package com.hust.baseweb.applications.education.teacherclassassignment.service;

import com.hust.baseweb.applications.education.teacherclassassignment.model.ClassTeacherAssignmentSolutionModel;
import com.hust.baseweb.applications.education.teacherclassassignment.model.ClassesAssignedToATeacherModel;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * OK
 */
@Log4j2
public class ClassTeacherAssignmentSolutionExcelExporter {

    private final XSSFWorkbook workbook = new XSSFWorkbook();

    private XSSFSheet sheet;

    private final List<ClassesAssignedToATeacherModel> gridViewData;

    private final List<ClassTeacherAssignmentSolutionModel> solution;

    private final XSSFCellStyle validClassStyles;

    private final XSSFCellStyle invalidClassStyles;

    public ClassTeacherAssignmentSolutionExcelExporter(
        List<ClassesAssignedToATeacherModel> gridViewData,
        List<ClassTeacherAssignmentSolutionModel> solution
    ) {
        this.gridViewData = gridViewData;
        this.solution = solution;

        // Valid class styles.
        validClassStyles = createClassStyle("2196F3");

        // Invalid class styles.
        invalidClassStyles = createClassStyle("F44336");
    }

    /**
     * OK
     *
     * @param rgbS
     * @return
     */
    private XSSFCellStyle createClassStyle(String rgbS) {
        XSSFCellStyle classStyles = workbook.createCellStyle();
        classStyles.setAlignment(HorizontalAlignment.CENTER);
        classStyles.setVerticalAlignment(VerticalAlignment.CENTER);
        classStyles.setWrapText(true);


        byte[] rgbB = new byte[0]; // get byte array from hex string
        try {
            rgbB = Hex.decodeHex(rgbS);
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        XSSFColor color = new XSSFColor(rgbB, null); // IndexedColorMap has no usage until now. So it can be set null.
        classStyles.setFillForegroundColor(color);
        classStyles.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return classStyles;
    }

    /**
     * OK
     */
    private void writeHeaderLine() {
        sheet = workbook.createSheet("DS phân công");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
//        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Mã lớp", style);
        createCell(row, 1, "Mã học phần", style);
        createCell(row, 2, "Tên học phần", style);
        createCell(row, 3, "Giáo viên", style);
        createCell(row, 4, "Thời khoá biểu", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    /**
     * OK
     */
    private void writeDataLines() {
        for (int i = 0; i < solution.size(); i++) {
            ClassTeacherAssignmentSolutionModel s = solution.get(i);
            Row row = sheet.createRow(i + 1);

            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
//            font.setFontName("Arial");
            font.setBold(false);
            font.setFontHeight(16);
            style.setFont(font);

            createCell(row, 0, s.getClassCode(), style);
            createCell(row, 1, s.getCourseId(), style);
            createCell(row, 2, s.getCourseName(), style);
            createCell(row, 3, s.getTeacherName(), style);
            createCell(row, 4, s.getTimetable(), style);
        }
    }

//    public void export(HttpServletResponse response) throws IOException {
//        writeHeaderLine();
//        writeDataLines();
//
//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//        workbook.close();
//
//        outputStream.close();
//        System.out.println("export: FINISHED write to response");
//
//    }

    /**
     * OK
     *
     * @return
     */
    public ByteArrayInputStream toExcel() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeHeaderLine();
            writeDataLines();

            toTemplateExcel();
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a cell and style it a certain way.
     *
     * @param row    the row to create the cell in
     * @param column the column number to create the cell in
     */
    private static Cell createCell(
        Row row,
        int column,
        CellStyle cellStyle
    ) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    /**
     * Creates a merged cell and style it a certain way.
     *
     * @param sheet        the sheet
     * @param rangeAddress
     * @param value
     * @param cellStyle
     */
    private void createMergedCell(
        XSSFSheet sheet,
        CellRangeAddress rangeAddress,
        Object value,
        XSSFCellStyle cellStyle
    ) {
        Row row = sheet.getRow(rangeAddress.getFirstRow());

        if (row == null) {
            row = sheet.createRow(rangeAddress.getFirstRow());
        }

        int column = rangeAddress.getFirstColumn();
        Cell cell = row.createCell(column);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }

        try {
            sheet.addMergedRegion(rangeAddress);
        } catch (Exception e) {
            System.out.println("DETECTED INVALID MERGED REGION!");
        }

        setBorderStyles(cellStyle);
        cell = row.getCell(column);
        cell.setCellStyle(cellStyle);
    }

    /**
     * Set border styles for all merged cell in sheet.
     *
     * @param sheet the sheet
     */
    private void setBordersToMergedCells(Sheet sheet) {
        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
        for (CellRangeAddress rangeAddress : mergedRegions) {
            RegionUtil.setBorderTop(BorderStyle.THIN, rangeAddress, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, rangeAddress, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);
            RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet);
        }
    }

    /**
     * Set border styles for normal cell.
     *
     * @param style
     */
    private void setBorderStyles(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());

        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }

    /**
     * OK
     *
     * @param currentRowIndex
     * @param startColumnIndex
     * @param endColumnIndex
     * @param classCode
     * @param classStyles
     */
    private void drawClass(
        int currentRowIndex,
        int startColumnIndex,
        int endColumnIndex,
        Object classCode,
        XSSFCellStyle classStyles
    ) {
        createMergedCell(
            sheet,
            new CellRangeAddress(
                currentRowIndex,
                currentRowIndex,
                startColumnIndex,
                endColumnIndex),
            classCode,
            classStyles
        );
    }

    /**
     * OK
     *
     * @param classes
     * @param fromIndex
     * @param toIndex
     * @param currentRowIndex
     */
    private void drawConflictClasses(
        List<ClassTeacherAssignmentSolutionModel> classes,
        int fromIndex,
        int toIndex,
        int currentRowIndex
    ) {
        ClassTeacherAssignmentSolutionModel firstClass = classes.get(fromIndex);
        StringBuilder classCode = new StringBuilder(firstClass.getClassCode() + ", ");
        int duration = firstClass.getDuration();
        int startColumnIndex = firstClass.getStartSlot();

        for (int i = fromIndex + 1; i <= toIndex; i++) {
            ClassTeacherAssignmentSolutionModel c = classes.get(i);

            classCode.append(c.getClassCode()).append(i == toIndex ? "" : ", ");
            duration += c.getStartIndexFromPrevious() + c.getDuration();
        }

        drawClass(
            currentRowIndex,
            startColumnIndex,
            startColumnIndex + duration - 1,
            classCode.toString(),
            invalidClassStyles);
    }

    /**
     * OK
     *
     * @param classes
     * @param currentRowIndex
     */
    private void drawClasses(List<ClassTeacherAssignmentSolutionModel> classes, int currentRowIndex) {
        int n = classes.size();

        for (int i = 0; i < n; i++) {
            ClassTeacherAssignmentSolutionModel ci = classes.get(i);

            for (int j = i + 1; j < n; j++) {
                ClassTeacherAssignmentSolutionModel cj = classes.get(j);

                if (cj.getStartIndexFromPrevious() > -1) {
                    if (j == i + 1) {
                        drawClass(
                            currentRowIndex,
                            ci.getStartSlot(),
                            ci.getEndSlot(),
                            Integer.parseInt(ci.getClassCode()),
                            validClassStyles);
                    } else {
                        drawConflictClasses(classes, i, j - 1, currentRowIndex);
                    }

                    i = j - 1;
                    break;
                } else {
                    if (j == n - 1) {
                        drawConflictClasses(classes, i, j, currentRowIndex);
                        i = j + 1;
                    }
                }
            }

            if (i == n - 1) {
                drawClass(
                    currentRowIndex,
                    ci.getStartSlot(),
                    ci.getEndSlot(),
                    Integer.parseInt(ci.getClassCode()),
                    validClassStyles);
            }
        }
    }

    /**
     * OK
     */
    private void toTemplateExcel() {
        sheet = workbook.createSheet("Biểu đồ lịch giảng dạy theo tuần");

        // Styling.
        sheet.setColumnWidth(0, 30 * 256);
        for (short i = 1; i < 73; i++) {
            sheet.setColumnWidth(i, 4 * 256);
        }

        // Header styles.
        XSSFCellStyle headerStyles = workbook.createCellStyle();
        headerStyles.setAlignment(HorizontalAlignment.CENTER);
        headerStyles.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderStyles(headerStyles);

        // First column styles.
        CellStyle firstColumnStyles = workbook.createCellStyle();
        firstColumnStyles.setAlignment(HorizontalAlignment.LEFT);
        firstColumnStyles.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorderStyles(firstColumnStyles);

        // Start construct header.
        createMergedCell(
            sheet,
            new CellRangeAddress(1, 3, 0, 0),
            "Giảng viên",
            headerStyles);

        // Create column for weekdays.
        Row sessionRow = sheet.createRow(3);
        for (short i = 0; i < 6; i++) {
            createMergedCell(
                sheet,
                new CellRangeAddress(1, 1, 1 + i * 12, 12 + i * 12),
                "Thứ " + (i + 2),
                headerStyles);

            createMergedCell(
                sheet,
                new CellRangeAddress(2, 2, 1 + i * 12, 6 + i * 12),
                "Sáng",
                headerStyles);

            createMergedCell(
                sheet,
                new CellRangeAddress(2, 2, 7 + i * 12, 12 + i * 12),
                "Chiều",
                headerStyles);

            for (short j = 0; j < 12; j++) {
                Cell cell = createCell(
                    sessionRow,
                    1 + i * 12 + j,
                    headerStyles);

                cell.setCellValue(j + 1);
            }
        }

        // Write data.
        int currentRowIndex = 3;
        Row row;
        for (ClassesAssignedToATeacherModel c : gridViewData) {
            currentRowIndex++;
            row = sheet.createRow(currentRowIndex);

            Cell teacherNameCell = createCell(row, 0, firstColumnStyles);
            teacherNameCell.setCellValue(c.getTeacherName());
            drawClasses(c.getClassList(), currentRowIndex);
        }

        // Draw necessary border
        CellRangeAddress rangeAddress = new CellRangeAddress(currentRowIndex, currentRowIndex, 0, 72);
        RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet);

        for (short i = 0; i < 6; i++) {
            rangeAddress = new CellRangeAddress(3, currentRowIndex, 1 + i * 12, 6 + i * 12);
            RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);

            rangeAddress = new CellRangeAddress(3, currentRowIndex, 1 + i * 12, 12 + i * 12);
            RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);
        }

        // The following is old code, consider remove it in the future
//        solution.sort((firstClass, secondClass) -> firstClass.getTeacherId()
//                                                             .compareToIgnoreCase(secondClass.getTeacherId()));
//
//        String previousTeacher = "";
//        Row row;
//        int currentRowIndex = 3;
//
//        for (ClassesAssignedToATeacherModel s : solution) {
//            if (s.getTeacherId().compareToIgnoreCase(previousTeacher) != 0) {
//                previousTeacher = s.getTeacherId();
//                currentRowIndex++;
//                row = sheet.createRow(currentRowIndex);
//
//                Cell teacherNameCell = createCell(row, 0, firstColumnStyles);
//                teacherNameCell.setCellValue(s.getTeacherName());
//            }
//
//            String periodStr = TimetableConflictChecker.extractPeriod(s.getTimetable());
//
//            if (periodStr != null) { // Ignore all classes which timetable is null.
//                // Extract period to define position.
//                String[] period = periodStr.split(",");
//
//                String start = period[0];
//                String end = period[1];
//                int weekDay = Integer.parseInt(start.substring(0, 1));
//                int startSession = Integer.parseInt(start.substring(1, 2)) - 1;
//                int startPeriod = Integer.parseInt(start.substring(2));
//                int endSession = Integer.parseInt(end.substring(1, 2)) - 1;
//                int endPeriod = Integer.parseInt(end.substring(2));
//
//                createMergedCell(
//                    sheet,
//                    new CellRangeAddress(
//                        currentRowIndex,
//                        currentRowIndex,
//                        1 + (weekDay - 2) * 12 + 6 * startSession + startPeriod - 1,
//                        1 + (weekDay - 2) * 12 + 6 * endSession + endPeriod - 1),
//                    Integer.parseInt(s.getClassCode()),
//                    classStyles
//                );
//            } else {
//                log.info("TIMETABLE IS NULL WITH CLASSCODE " + s.getClassCode());
//            }
//        }

        setBordersToMergedCells(sheet);
    }
}
