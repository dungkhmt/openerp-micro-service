package openerp.openerpresourceserver.util;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@UtilityClass
public class ExcelUtil {
    public Cell getCell(Row row, int index) {
        return row.getCell(index) == null ? row.createCell(index) : row.getCell(index);
    }

    public Cell getCell(Row row, int index, CellStyle cellStyle) {
        Cell cell = getCell(row, index);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public String getCellSumFormula(Cell fromCell, Cell toCell) {
        return "SUM($" + fromCell.getAddress() + ":$" + toCell.getAddress() + ")";
    }

    public String getCellRoundFormula(Cell fromCell, double denominator) {
        return "ROUND($" + fromCell.getAddress() + "/" + denominator + ",1)";
    }

    public String getCellRoundSumFormula(Cell fromCell, Cell toCell, double denominator) {
        return "ROUND(" + getCellSumFormula(fromCell, toCell) + "," + denominator + ")";
    }

    public Cell setCellValue(int rowIdx, int cellIdx, Object value, CellStyle cellStyle, Sheet sheet) {
        Row r1 = sheet.getRow(rowIdx);
        Cell c1 = getCell(r1, cellIdx);
        c1.setCellStyle(cellStyle);
        switch (value) {
            case String val -> c1.setCellValue(val);
            case Integer val -> c1.setCellValue(val);
            case Double val -> c1.setCellValue(val);
            case Boolean val -> c1.setCellValue(val);
            case null -> c1.setCellValue("");
            default -> throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
        }
        return c1;
    }

    public CellStyle createAlignedCellStyle(Font font,
                                            HorizontalAlignment horizontalAlignment,
                                            VerticalAlignment verticalAlignment,
                                            Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(horizontalAlignment);
        style.setVerticalAlignment(verticalAlignment);
        return style;
    }

    public Font createBasicFont(int size, boolean isBold, IndexedColors color, Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) size);
        font.setFontName("Times New Roman");
        font.setColor(color.getIndex());
        font.setBold(isBold);
        return font;
    }

    public CellStyle createBorderAlignedCellStyle(
            Font font,
            IndexedColors borderColor,
            HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment,
            Workbook workbook) {
        CellStyle style = createBorderCellStyle(borderColor, workbook);
        style.setFont(font);
        style.setAlignment(horizontalAlignment);
        style.setVerticalAlignment(verticalAlignment);
        return style;
    }

    public CellStyle createBorderCenterBackgroundStyle(
            Font font,
            IndexedColors borderColor,
            Color bgColor,
            HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment,
            Workbook workbook) {
        CellStyle style = createBorderAlignedCellStyle(font, borderColor, horizontalAlignment, verticalAlignment, workbook);

        // Set red background color
        style.setFillForegroundColor(bgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    public CellStyle createBorderCellStyle(IndexedColors borderColor, Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(borderColor.getIndex());
        style.setBottomBorderColor(borderColor.getIndex());
        style.setLeftBorderColor(borderColor.getIndex());
        style.setRightBorderColor(borderColor.getIndex());
        return style;
    }

    public Color getWeekDayBackgroundColor(Workbook workbook) {
        byte[] rgb = new byte[]{(byte) 214, (byte) 227, (byte) 188};
        return new XSSFColor(rgb, ((XSSFWorkbook) workbook).getStylesSource().getIndexedColors());
    }

    public Color getWeekendBackgroundColor(Workbook workbook) {
        byte[] rgb = new byte[]{(byte) 234, (byte) 153, (byte) 153};
        return new XSSFColor(rgb, ((XSSFWorkbook) workbook).getStylesSource().getIndexedColors());
    }

    public Color getSubtitleBackgroundColor(Workbook workbook) {
        byte[] rgb = new byte[]{(byte) 201, (byte) 218, (byte) 248};
        return new XSSFColor(rgb, ((XSSFWorkbook) workbook).getStylesSource().getIndexedColors());
    }

    public Color getHolidayBackgroundColor(Workbook workbook) {
        byte[] rgb = new byte[]{(byte) 250, (byte) 191, (byte) 143};
        return new XSSFColor(rgb, ((XSSFWorkbook) workbook).getStylesSource().getIndexedColors());
    }

    public Color getBackgroundColor(int r, int g, int b, Workbook workbook) {
        byte[] rgb = new byte[]{(byte) r, (byte) g, (byte) b};
        return new XSSFColor(rgb, ((XSSFWorkbook) workbook).getStylesSource().getIndexedColors());
    }

    public void mergeColumn(int firstRow, int lastRow, int firstCol, int lastCol, Sheet sheet) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }
}
