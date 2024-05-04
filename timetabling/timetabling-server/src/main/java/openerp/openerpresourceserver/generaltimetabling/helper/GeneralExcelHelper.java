package openerp.openerpresourceserver.generaltimetabling.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.OccupationClassPeriod;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Component
public class GeneralExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERS = { "SL thực", "Loại lớp", "Mã HP", "Tuần học", "Thời lượng", "SL max",
            "Lớp học", "Thời lượng", "Trạng thái", "Mã lớp", "Kíp", "Đợt", "Khóa", "Giáo viên nước ngoài"};
    static String SHEET = "Sheet1";
    static String DEFAULT_SHEET = "Sheet1";

    /**
     * Start row in excel file to classes
     */
    private final static int START_ROW_TO_READ_CLASS = 4;
    /**
     * Start column in excel to read class information (Start with column A)
     */
    private final static int START_COL_TO_READ_CLASS_INFO = 0;
    /**
     * End column in excel to read class information (End with column P)
     */
    private final static int END_COL_TO_READ_CLASS_INFO = 13;
    /**
     * Start column in excel to read class schedule (Start with column Q)
     */
    private final static int START_COL_TO_READ_CLASS_SCHEDULE = 14;
    /**
     * End column in excel to read class information (End with column AZ)
     */
    private final static int END_COL_TO_READ_CLASS_SCHEDULE = 48;

    public static final Integer NUMBER_PERIODS_PER_DAY = 6;

    public static final Integer DEFAULT_VALUE_CALCULATE_TIME = 12;

    /**
     * @param file
     * @return boolean
     * @functionality return true if file has excel format
     * @example example.xlsx return true, example.docx return false
     */
    public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    /**
     * @param inputStream
     * @functionality this function is for get data from excel file and convert it
     *                to GCO.
     * @return
     */
    public static List<GeneralClassOpened> convertFromExcelToGeneralClassOpened(InputStream inputStream,
                                                                                String semester) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(SHEET);
            List<GeneralClassOpened> convertedList = new ArrayList<GeneralClassOpened>();
            if (sheet == null) {
                sheet = workbook.getSheet(DEFAULT_SHEET);
            }
            int totalRowsNum = sheet.getLastRowNum();
            for (int i = START_ROW_TO_READ_CLASS; i < totalRowsNum; i++) {
                Row classRow = sheet.getRow(i);
                // skip if not exist classs code
                if (classRow.getCell(8) == null)
                    continue;
                GeneralClassOpened generalClassOpened = new GeneralClassOpened();
                RoomReservation timeSlot = null;
                int duration = 0;
                for (int j = START_COL_TO_READ_CLASS_INFO; j <= END_COL_TO_READ_CLASS_SCHEDULE; j++) {
                    Cell classInfoCell = classRow.getCell(j);
                    if (classInfoCell != null) {
                        String cellValue = "";
                        switch (classInfoCell.getCellType()) {
                            case 1:
                                cellValue = classInfoCell.getStringCellValue();
                                break;
                            case 0:
                                cellValue = String.valueOf((int) classInfoCell.getNumericCellValue());
                                break;
                            default:
                                break;
                        }
                        if (classInfoCell.getColumnIndex() >= START_COL_TO_READ_CLASS_SCHEDULE
                                && classInfoCell.getCellStyle() != null) {
                            XSSFColor bgColor = (XSSFColor) classInfoCell.getCellStyle().getFillBackgroundColorColor();
                            if (bgColor != null && "FFFFC000".equals(bgColor.getARGBHex())) {
                                if (cellValue != null && !cellValue.equals("")) {
                                    timeSlot = new RoomReservation();
                                    timeSlot.setGeneralClassOpened(generalClassOpened);
                                    timeSlot.setStartTime(
                                            (classInfoCell.getColumnIndex() - END_COL_TO_READ_CLASS_INFO) % 6);
                                    timeSlot.setWeekday(
                                            (classInfoCell.getColumnIndex() - END_COL_TO_READ_CLASS_INFO) / 6 + 2);
                                    timeSlot.setRoom(cellValue);
                                }

                                duration++;
                            } else {
                                if (timeSlot != null) {
                                    timeSlot.setEndTime(timeSlot.getStartTime() + duration - 1);
                                    generalClassOpened.addTimeSlot(timeSlot);
                                    timeSlot = null;
                                    duration = 0;
                                }
                            }
                        } else {
                            switch (classInfoCell.getColumnIndex()) {
                                case 0:
                                    generalClassOpened.setQuantity(cellValue);
                                    break;
                                case 1:
                                    generalClassOpened.setClassType(cellValue);
                                    break;
                                case 2:
                                    generalClassOpened.setModuleCode(cellValue);
                                    break;
                                case 3:
                                    generalClassOpened.setModuleName(cellValue);
                                    break;
                                case 4:
                                    generalClassOpened.setLearningWeeks(cellValue);
                                    break;
                                case 5:
                                    generalClassOpened.setMass(cellValue);
                                    break;
                                case 6:
                                    generalClassOpened.setQuantityMax(cellValue);
                                    break;
                                case 7:
                                    generalClassOpened.setStudyClass(cellValue);
                                    break;
                                case 8:
                                    generalClassOpened.setState(cellValue);
                                    break;
                                case 9:
                                    if (cellValue.equals(""))
                                        continue;
                                    generalClassOpened.setClassCode(cellValue);
                                    break;
                                case 10:
                                    generalClassOpened.setCrew(cellValue);
                                    break;
                                case 11:
                                    generalClassOpened.setOpenBatch(cellValue);
                                    break;
                                case 12:
                                    generalClassOpened.setCourse(cellValue);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                generalClassOpened.setSemester(semester);
                convertedList.add(generalClassOpened);
            }
            workbook.close();
            return convertedList;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }


    public ByteArrayInputStream convertRoomOccupationToExcel(List<RoomOccupation> rooms) {
        /**Init the data to map*/
        HashMap<String, List<OccupationClassPeriod>> periodMap = new HashMap<>();
        HashMap<String, List<OccupationClassPeriod>> conflictMap = new HashMap<>();
        for(RoomOccupation room : rooms) {
            String classRoom = room.getClassRoom();
            long crewPeriod = room.getCrew().equals("S") ? 0 : 6;
            long startPeriodIndex = room.getStartPeriod() + 12*(room.getDayIndex()-2) + crewPeriod;
            long endPeriodIndex = room.getEndPeriod() + 12*(room.getDayIndex()-2) + crewPeriod;
            OccupationClassPeriod period = new OccupationClassPeriod(startPeriodIndex, endPeriodIndex, room.getClassCode());
            if(periodMap.get(classRoom) == null) {
                List<OccupationClassPeriod> initList = new ArrayList<>();
                initList.add(period);
                periodMap.put(classRoom, initList);
            } else {
                periodMap.get(classRoom).add(period);
            }
            if(/**Check conflict at here*/!ClassTimeComparator.isPeriodConflict(period, periodMap)) {
                if(conflictMap.get(classRoom) == null) {
                    List<OccupationClassPeriod> initList = new ArrayList<>();
                    initList.add(period);
                    conflictMap.put(classRoom, initList);
                } else {
                    conflictMap.get(classRoom).add(period);
                }
            }
        }


        /**Handle Excel write*/
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            /** Init the cell style*/
            /*Bold style*/
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            /*Error style*/
            CellStyle errorStyle=  workbook.createCellStyle();
            errorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            errorStyle.setFillPattern((short) 1);
            errorStyle.setFont(boldFont);
            /*Room style*/
            CellStyle roomStyle=  workbook.createCellStyle();
            roomStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            roomStyle.setFillPattern((short) 1);
            /*Week index style*/
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
            headerStyle.setBorderBottom((short) 1);
            headerStyle.setBorderLeft((short) 1);
            headerStyle.setBorderRight((short) 1);
            headerStyle.setBorderTop((short) 1);
            Sheet sheet = workbook.createSheet(SHEET);
            int rowIndex = 0;

            /**Header*/
            /*Week index row*/
            Row dayIndexRow = sheet.createRow(rowIndex);
            for (int i = 0; i < 84; i+=12) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,i+1,i+12));
                Cell c = dayIndexRow.createCell(i+1);
                String weekIndexString = "" + ((i%84)/12 +2);
                c.setCellValue(weekIndexString);
                c.setCellStyle(headerStyle);
            }
            rowIndex++;
            /*Period row*/
            Row periodRow = sheet.createRow(rowIndex);
            for (int i = 0; i < 84; i++) {
                Cell c = periodRow.createCell(i+1);
                String periodIndexString = "" + ((i%84)%12+1);
                c.setCellValue(periodIndexString);
                c.setCellStyle(headerStyle);
            }
            rowIndex++;


            /*Start write data*/
            for (String room : periodMap.keySet()) {
                if(!room.equals("")) {
                    Row roomRow = sheet.createRow(rowIndex);
                    Cell roomNameCell = roomRow.createCell(0);
                    roomNameCell.setCellValue(room);
                    roomNameCell.setCellStyle(roomStyle);
                    for (int cellIndex = 1; cellIndex <= 84; cellIndex++) {
                        Cell c = roomRow.createCell(cellIndex);
                        c.setCellStyle(boldStyle);
                        for (OccupationClassPeriod roomPeriod : periodMap.get(room)) {
                            if(cellIndex >=  roomPeriod.getStartPeriodIndex() && cellIndex <= roomPeriod.getEndPeriodIndex()) {
                                // If cell value is not empty, append class code with comma
                                if (c.getStringCellValue() != null && !c.getStringCellValue().isEmpty()) {
                                    Set<String> classCodeSet = new HashSet<>(Arrays.stream(c.getStringCellValue().split(",")).toList());
                                    classCodeSet.add(roomPeriod.getClassCode());
                                    c.setCellValue(String.join(",", classCodeSet));
                                    c.setCellStyle(errorStyle);
                                } else {
                                    c.setCellValue(roomPeriod.getClassCode());
                                }
                            }
                        }
                    }
                    rowIndex++;
                }
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public ByteArrayInputStream convertGeneralClassToExcel(List<GeneralClassOpened> classes) {
        /**Handle Excel write*/
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            /** Init the cell style*/
            /*Bold style*/
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            int rowIndex = START_ROW_TO_READ_CLASS;
            Sheet sheet = workbook.createSheet(SHEET);
            /*Room style*/
            CellStyle roomStyle=  workbook.createCellStyle();
            roomStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            roomStyle.setFillPattern((short) 1);

            /**Header*/
            /*Handle create header info*/
            Row weekIndexRow = sheet.createRow(rowIndex);
            for (int i = 0; i < HEADERS.length; i += 1) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,i,i));
                Cell c = weekIndexRow.createCell(i);
                String classInfoString = HEADERS[i];
                c.setCellValue(classInfoString);
            }
            /**Handle create header schedule info */

            Row periodIndexRow = sheet.createRow(rowIndex+1);
            for (int i = START_COL_TO_READ_CLASS_SCHEDULE; i < START_COL_TO_READ_CLASS_SCHEDULE+42; i += 6) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,i,i+6));
                Cell c = weekIndexRow.createCell(i);
                String weekString = "Thứ " + (i-START_COL_TO_READ_CLASS_SCHEDULE)/6;
                c.setCellValue(weekString);
            }

            for (int i = START_COL_TO_READ_CLASS_SCHEDULE; i < START_COL_TO_READ_CLASS_SCHEDULE+42; i++) {
                Cell c = periodIndexRow.createCell(i);
                String periodString = "" + (i-START_COL_TO_READ_CLASS_SCHEDULE)%6;
                c.setCellValue(periodString);
            }

            rowIndex+=2;
            /**Handle write class info and schedule*/
            for (GeneralClassOpened generalClassOpened : classes) {
                Row classRow = sheet.createRow(rowIndex);
                /*Write the class info*/
                for (int i = 0 ; i < END_COL_TO_READ_CLASS_INFO; i++ ) {
                    Cell c = classRow.createCell(i);
                    switch (i) {
                        case 0:
                            c.setCellValue(generalClassOpened.getQuantity());
                            break;
                        case 1:
                            c.setCellValue(generalClassOpened.getClassType());
                            break;
                        case 2:
                            c.setCellValue(generalClassOpened.getModuleCode());
                            break;
                        case 3:
                            c.setCellValue(generalClassOpened.getModuleName());
                            break;
                        case 4:
                            c.setCellValue(generalClassOpened.getLearningWeeks());
                            break;
                        case 5:
                            c.setCellValue(generalClassOpened.getMass());
                            break;
                        case 6:
                            c.setCellValue(generalClassOpened.getQuantityMax());
                            break;
                        case 7:
                            c.setCellValue(generalClassOpened.getStudyClass());
                            break;
                        case 8:
                            c.setCellValue(generalClassOpened.getState());
                            break;
                        case 9:
                            c.setCellValue(generalClassOpened.getClassCode());
                            break;
                        case 10:
                            c.setCellValue(generalClassOpened.getCrew());
                            break;
                        case 11:
                            c.setCellValue(generalClassOpened.getOpenBatch());
                            break;
                        case 12:
                            c.setCellValue(generalClassOpened.getCourse());
                            break;
                        default:
                            break;
                    }
                }
                /*Write the class schedule*/
                for (int j = START_COL_TO_READ_CLASS_SCHEDULE; j < START_COL_TO_READ_CLASS_SCHEDULE + 42; j++) {
                    Cell c = classRow.createCell(j);
                    for (RoomReservation rr : generalClassOpened.getTimeSlots().stream().filter(RoomReservation::isNotNull).toList()) {
                        if (j - START_COL_TO_READ_CLASS_SCHEDULE > (rr.getWeekday()-2)*6 + rr.getStartTime() && j-START_COL_TO_READ_CLASS_SCHEDULE < (rr.getWeekday()-2)*6 + rr.getEndTime()) {
                            c.setCellValue(rr.getRoom());
                            c.setCellStyle(roomStyle);
                        }
                    }
                }
                rowIndex++;
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}
