package openerp.openerpresourceserver.helper;

import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Schedule;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Kỳ", "Trường_Viện_Khoa", "Mã_lớp", "Mã_lớp_kèm", "Mã_HP", "Tên_HP", "Tên_HP_Tiếng_Anh",
            "Khối_lượng", "Ghi_chú", "Buổi_số", "Thứ", "Thời_gian", "BĐ", "KT", "Kíp", "Tuần", "Phòng",
            "Cần_TN", "SLĐK", "SL_Max", "Trạng_thái", "Loại_lớp", "Đợt_mở", "Mã_QL"};
    static String SHEET = "Schedules";
    static String DEFAULT_SHEET = "Sheet1";

    public static ByteArrayInputStream schedulesToExcelExport(List<Schedule> schedules) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            int rowIdx = 1;
            for (Schedule schedule : schedules) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(schedule.getSemester());
                row.createCell(1).setCellValue(schedule.getInstitute() );
                row.createCell(2).setCellValue(schedule.getClassCode());
                row.createCell(3).setCellValue(schedule.getBundleClassCode());
                row.createCell(4).setCellValue(schedule.getModuleCode());
                row.createCell(5).setCellValue(schedule.getModuleName());
                row.createCell(6).setCellValue(schedule.getModuleNameByEnglish());
                row.createCell(7).setCellValue(schedule.getMass());
                row.createCell(8).setCellValue(schedule.getNotes());
                row.createCell(9).setCellValue(schedule.getSessionNo());
                row.createCell(10).setCellValue(schedule.getWeekDay());
                row.createCell(11).setCellValue(schedule.getStudyTime());
                row.createCell(12).setCellValue(schedule.getStart());
                row.createCell(13).setCellValue(schedule.getFinish());
                row.createCell(14).setCellValue(schedule.getCrew());
                row.createCell(15).setCellValue(schedule.getStudyWeek());
                row.createCell(16).setCellValue(schedule.getClassRoom());
                row.createCell(17).setCellValue(schedule.getIsNeedExperiment());
                row.createCell(18).setCellValue(schedule.getNumberOfRegistrations());
                row.createCell(19).setCellValue(schedule.getMaxQuantity());
                row.createCell(20).setCellValue(schedule.getState());
                row.createCell(21).setCellValue(schedule.getClassType());
                row.createCell(22).setCellValue(schedule.getOpenBatch());
                row.createCell(23).setCellValue(schedule.getManagementCode());
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream schedulesToExcel(List<Schedule> schedules) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Add title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("THỜI KHÓA BIỂU");
            titleCell.setCellStyle(getCenteredStyle(workbook));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERs.length - 1));

            // Header
            Row headerRow = sheet.createRow(2);
            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    private static CellStyle getCenteredStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.ALIGN_CENTER);
        return style;
    }

    public static List<Schedule> excelToSchedules(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) {
                sheet = workbook.getSheet(DEFAULT_SHEET);
            }
            Iterator<Row> rows = sheet.iterator();
            List<Schedule> schedules = new ArrayList<Schedule>();
            int rowNumber = 0;
            if (rows.hasNext()){
                rows.next();
            }
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber += 2;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                Schedule schedule = new Schedule();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            schedule.setSemester(currentCell.getStringCellValue());
                            break;
                        case 1:
                            schedule.setInstitute(currentCell.getStringCellValue());
                            break;
                        case 2:
                            schedule.setClassCode(currentCell.getStringCellValue());
                            break;
                        case 3:
                            schedule.setBundleClassCode(currentCell.getStringCellValue());
                            break;
                        case 4:
                            schedule.setModuleCode(currentCell.getStringCellValue());
                            break;
                        case 5:
                            schedule.setModuleName(currentCell.getStringCellValue());
                            break;
                        case 6:
                            schedule.setModuleNameByEnglish(currentCell.getStringCellValue());
                            break;
                        case 7:
                            schedule.setMass(currentCell.getStringCellValue());
                            break;
                        case 8:
                            schedule.setNotes(currentCell.getStringCellValue());
                            break;
                        case 9:
                            schedule.setSessionNo(currentCell.getStringCellValue());
                            break;
                        case 10:
                            schedule.setWeekDay(currentCell.getStringCellValue());
                            break;
                        case 11:
                            schedule.setStudyTime(currentCell.getStringCellValue());
                            break;
                        case 12:
                            schedule.setStart(currentCell.getStringCellValue());
                            break;
                        case 13:
                            schedule.setFinish(currentCell.getStringCellValue());
                            break;
                        case 14:
                            schedule.setCrew(currentCell.getStringCellValue());
                            break;
                        case 15:
                            schedule.setStudyWeek(currentCell.getStringCellValue());
                            break;
                        case 16:
                            schedule.setClassRoom(currentCell.getStringCellValue());
                            break;
                        case 17:
                            schedule.setIsNeedExperiment(currentCell.getStringCellValue());
                            break;
                        case 18:
                            schedule.setNumberOfRegistrations(currentCell.getStringCellValue());
                            break;
                        case 19:
                            schedule.setMaxQuantity(currentCell.getStringCellValue());
                            break;
                        case 20:
                            schedule.setState(currentCell.getStringCellValue());
                            break;
                        case 21:
                            schedule.setClassType(currentCell.getStringCellValue());
                            break;
                        case 22:
                            schedule.setOpenBatch(currentCell.getStringCellValue());
                            break;
                        case 23:
                            schedule.setManagementCode(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                schedules.add(schedule);
            }
            workbook.close();
            return schedules;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    public static List<ClassOpened> excelToClassOpened(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) {
                sheet = workbook.getSheet(DEFAULT_SHEET);
            }
            Iterator<Row> rows = sheet.iterator();
            List<ClassOpened> classOpeneds = new ArrayList<ClassOpened>();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber++ < 4) {
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                ClassOpened classOpened = new ClassOpened();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    String cellValue = getCellValueAsString(currentCell);
                    switch (cellIdx) {
                        case 0:
                            classOpened.setQuantity(cellValue);
                            break;
                        case 1:
                            classOpened.setClassType(cellValue);
                            break;
                        case 2:
                            classOpened.setModuleCode(cellValue);
                            break;
                        case 3:
                            classOpened.setModuleName(cellValue);
                            break;
                        case 4:
                            classOpened.setMass(cellValue);
                            break;
                        case 5:
                            classOpened.setQuantityMax(cellValue);
                            break;
                        case 7:
                            classOpened.setStudyClass(cellValue);
                            break;
                        case 10:
                            classOpened.setState(cellValue);
                            break;
                        case 11:
                            classOpened.setClassCode(cellValue);
                            break;
                        case 12:
                            classOpened.setCrew(cellValue);
                            break;
                        case 14:
                            classOpened.setOpenBatch(cellValue);
                            break;
                        case 15:
                            classOpened.setCourse(cellValue);
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                classOpened.setIsSeparateClass(false);
                classOpeneds.add(classOpened);
            }
            workbook.close();
            return classOpeneds;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return "";
        }
    }
}
