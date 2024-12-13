package openerp.openerpresourceserver.timetablefirstyearstandard.helper;


import openerp.openerpresourceserver.timetablefirstyearstandard.entity.ClassFirstYearStandardOpened;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
    static String[] HEADERs = {"ID", "Kỳ", "Nhóm", "SL thực", "Loại lớp", "Mã HP", "Tên HP", "Thời lượng", "SL Max",
    "Lớp học", "Trạng thái", "Mã lớp", "Kíp", "Đợt", "Khóa", "Tiết BĐ", "Thứ", "Phòng", "Tiết BĐ 2", "Thứ", "Phòng"};
    static String SHEET = "Schedules";
    static String DEFAULT_SHEET = "Sheet1";

    public static final Integer NUMBER_PERIODS_PER_DAY = 6;

    public static final Integer DEFAULT_VALUE_CALCULATE_TIME = 15;

    public static ByteArrayInputStream classOpenedToExcelExport(List<ClassFirstYearStandardOpened> classOpenedList) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);
            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }
            int rowIdx = 1;
            for (ClassFirstYearStandardOpened classOpened : classOpenedList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(classOpened.getId());
                row.createCell(1).setCellValue(classOpened.getSemester());
                row.createCell(2).setCellValue(classOpened.getGroupName());
                row.createCell(3).setCellValue(classOpened.getQuantity());
                row.createCell(4).setCellValue(classOpened.getClassType());
                row.createCell(5).setCellValue(classOpened.getModuleCode());
                row.createCell(6).setCellValue(classOpened.getModuleName());
                row.createCell(7).setCellValue(classOpened.getMass());
                row.createCell(8).setCellValue(classOpened.getQuantityMax());
                row.createCell(9).setCellValue(classOpened.getStudyClass());
                row.createCell(10).setCellValue(classOpened.getState());
                row.createCell(11).setCellValue(classOpened.getClassCode());
                row.createCell(12).setCellValue(classOpened.getCrew());
                row.createCell(13).setCellValue(classOpened.getOpenBatch());
                row.createCell(14).setCellValue(classOpened.getCourse());
                row.createCell(15).setCellValue(classOpened.getStartPeriod());
                row.createCell(16).setCellValue(classOpened.getWeekday());
                row.createCell(17).setCellValue(classOpened.getClassroom());
                row.createCell(18).setCellValue(classOpened.getSecondStartPeriod());
                row.createCell(19).setCellValue(classOpened.getSecondWeekday());
                row.createCell(20).setCellValue(classOpened.getSecondClassroom());
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

//    public static ByteArrayInputStream schedulesToExcel(List<Schedule> schedules) {
//        try (Workbook workbook = new XSSFWorkbook();
//             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
//            Sheet sheet = workbook.createSheet(SHEET);
//
//            // Add title row
//            Row titleRow = sheet.createRow(0);
//            Cell titleCell = titleRow.createCell(0);
//            titleCell.setCellValue("THỜI KHÓA BIỂU");
//            titleCell.setCellStyle(getCenteredStyle(workbook));
//            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERs.length - 1));
//
//            // Header
//            Row headerRow = sheet.createRow(2);
//            for (int col = 0; col < HEADERs.length; col++) {
//                Cell cell = headerRow.createCell(col);
//                cell.setCellValue(HEADERs[col]);
//            }
//            workbook.write(out);
//            return new ByteArrayInputStream(out.toByteArray());
//        } catch (IOException e) {
//            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
//        }
//    }

    public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

//    private static CellStyle getCenteredStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(CellStyle.ALIGN_CENTER);
//        return style;
//    }

//    public static List<Schedule> excelToSchedules(InputStream is) {
//        try {
//            Workbook workbook = new XSSFWorkbook(is);
//            Sheet sheet = workbook.getSheet(SHEET);
//            if (sheet == null) {
//                sheet = workbook.getSheet(DEFAULT_SHEET);
//            }
//            Iterator<Row> rows = sheet.iterator();
//            List<Schedule> schedules = new ArrayList<Schedule>();
//            int rowNumber = 0;
//            if (rows.hasNext()){
//                rows.next();
//            }
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//                // skip header
//                if (rowNumber == 0) {
//                    rowNumber += 2;
//                    continue;
//                }
//                Iterator<Cell> cellsInRow = currentRow.iterator();
//                Schedule schedule = new Schedule();
//                int cellIdx = 0;
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//                    switch (cellIdx) {
//                        case 0:
//                            schedule.setSemester(currentCell.getStringCellValue());
//                            break;
//                        case 1:
//                            schedule.setInstitute(currentCell.getStringCellValue());
//                            break;
//                        case 2:
//                            schedule.setClassCode(currentCell.getStringCellValue());
//                            break;
//                        case 3:
//                            schedule.setBundleClassCode(currentCell.getStringCellValue());
//                            break;
//                        case 4:
//                            schedule.setModuleCode(currentCell.getStringCellValue());
//                            break;
//                        case 5:
//                            schedule.setModuleName(currentCell.getStringCellValue());
//                            break;
//                        case 6:
//                            schedule.setModuleNameByEnglish(currentCell.getStringCellValue());
//                            break;
//                        case 7:
//                            schedule.setMass(currentCell.getStringCellValue());
//                            break;
//                        case 8:
//                            schedule.setNotes(currentCell.getStringCellValue());
//                            break;
//                        case 9:
//                            schedule.setSessionNo(currentCell.getStringCellValue());
//                            break;
//                        case 10:
//                            schedule.setWeekDay(currentCell.getStringCellValue());
//                            break;
//                        case 11:
//                            schedule.setStudyTime(currentCell.getStringCellValue());
//                            break;
//                        case 12:
//                            schedule.setStart(currentCell.getStringCellValue());
//                            break;
//                        case 13:
//                            schedule.setFinish(currentCell.getStringCellValue());
//                            break;
//                        case 14:
//                            schedule.setCrew(currentCell.getStringCellValue());
//                            break;
//                        case 15:
//                            schedule.setStudyWeek(currentCell.getStringCellValue());
//                            break;
//                        case 16:
//                            schedule.setClassRoom(currentCell.getStringCellValue());
//                            break;
//                        case 17:
//                            schedule.setIsNeedExperiment(currentCell.getStringCellValue());
//                            break;
//                        case 18:
//                            schedule.setNumberOfRegistrations(currentCell.getStringCellValue());
//                            break;
//                        case 19:
//                            schedule.setMaxQuantity(currentCell.getStringCellValue());
//                            break;
//                        case 20:
//                            schedule.setState(currentCell.getStringCellValue());
//                            break;
//                        case 21:
//                            schedule.setClassType(currentCell.getStringCellValue());
//                            break;
//                        case 22:
//                            schedule.setOpenBatch(currentCell.getStringCellValue());
//                            break;
//                        case 23:
//                            schedule.setManagementCode(currentCell.getStringCellValue());
//                            break;
//                        default:
//                            break;
//                    }
//                    cellIdx++;
//                }
//                schedules.add(schedule);
//            }
//            workbook.close();
//            return schedules;
//        } catch (IOException e) {
//            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
//        }
//    }

    public static List<ClassFirstYearStandardOpened> excelToClassOpened(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) {
                sheet = workbook.getSheet(DEFAULT_SHEET);
            }
            Iterator<Row> rows = sheet.iterator();
            List<ClassFirstYearStandardOpened> classOpeneds = new ArrayList<ClassFirstYearStandardOpened>();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber++ < 4) {
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                ClassFirstYearStandardOpened classOpened = new ClassFirstYearStandardOpened();
                int cellIdx = 0;

                String startPeriod;
                String weekday;
                String classroom;

                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    String cellValue = getCellValueAsString(currentCell);
                    //System.out.println("Helper read excel cellValue = " + cellValue);
                    //check and set study time
                    if (cellIdx > 15 && !cellValue.isEmpty()) {
                        weekday = (cellIdx - DEFAULT_VALUE_CALCULATE_TIME) / NUMBER_PERIODS_PER_DAY + 2 + "";
                        startPeriod = (cellIdx - DEFAULT_VALUE_CALCULATE_TIME) % NUMBER_PERIODS_PER_DAY + "";
                        classroom = cellValue;

                        if (classOpened.getStartPeriod() == null) {
                            classOpened.setWeekday(weekday);
                            classOpened.setStartPeriod(startPeriod);
                            classOpened.setClassroom(classroom);
                        } else {
                            classOpened.setSecondWeekday(weekday);
                            classOpened.setSecondStartPeriod(startPeriod);
                            classOpened.setSecondClassroom(classroom);
                        }
                    }
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
                classOpened.setIsSeparateClass(classOpened.getSecondStartPeriod() != null);
                //System.out.println("excelToClassOpened, read one more " + classOpeneds.size());
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


    public static String getBuildingFromClassroom(String classroom) {
        String[] classroomArray = classroom.split("-");
        if (classroomArray.length == 3) {
            return classroomArray[0] + "-" + classroomArray[1];
        } else return classroomArray[0];
    }
}
