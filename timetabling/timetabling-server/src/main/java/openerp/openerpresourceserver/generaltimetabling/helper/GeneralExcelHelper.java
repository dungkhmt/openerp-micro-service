package openerp.openerpresourceserver.generaltimetabling.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
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
    static String[] HEADERS = { "SL thực", "Loại lớp", "Mã HP","Tên HP", "Tuần học", "Thời lượng", "SL max",
            "Lớp học", "Trạng thái", "Mã lớp", "Mã lớp tham chiếu", "Mã lớp tạm thời", "Mã lớp cha", "Kíp", "Đợt", "Khóa", "Giáo viên nước ngoài"};
    static String SHEET = "Sheet1";
    static String DEFAULT_SHEET = "Sheet1";

    /**
     * Start row in excel file to classes
     */
    private final static int START_ROW_TO_READ_CLASS = 2;
    /**
     * Start column in excel to read class information (Start with column A)
     */
    private final static int START_COL_TO_READ_CLASS_INFO = 0;
    /**
     * End column in excel to read class information (End with column Q)
     */
    private final static int END_COL_TO_READ_CLASS_INFO = 16;
    /**
     * Start column in excel to read class schedule (Start with column R)
     */
    private final static int START_COL_TO_READ_CLASS_SCHEDULE = 17;
    /**
     * End column in excel to read class information (End with column BA)
     */
    private final static int END_COL_TO_READ_CLASS_SCHEDULE = 53;

    public static final Integer NUMBER_PERIODS_PER_DAY = 6;

    public static final Integer DEFAULT_VALUE_CALCULATE_TIME = 12;

    /**
     * @param file
     * @return boolean
     * @functionality return true if file has excel format
     * @example example.xlsx return true, example.docx return false
     */
    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    /**
     * @param inputStream
     * @functionality this function is for get data from excel file and convert it
     *                to GCO.
     * @return
     */
    public static List<GeneralClass> convertFromExcelToGeneralClassOpened(InputStream inputStream,
                                                                          String semester) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(SHEET);
            List<GeneralClass> convertedList = new ArrayList<GeneralClass>();
            if (sheet == null) {
                sheet = workbook.getSheet(DEFAULT_SHEET);
            }
            int totalRowsNum = sheet.getLastRowNum();
            System.out.println(totalRowsNum);
            for (int i = totalRowsNum; i >= START_ROW_TO_READ_CLASS; i--) {
                Row classRow = sheet.getRow(i);
                // skip if not exist classs code
                if (classRow != null && classRow.getCell(9) != null) {
                    Cell classCodeCell = classRow.getCell(9);
                    switch (classCodeCell.getCellType()) {
                        case Cell.CELL_TYPE_BLANK:
                            System.out.println("Cell blank, skip!");
                            continue;
                    }
                } else {
                    continue;
                }
                GeneralClass generalClass = new GeneralClass();
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
                                if (cellValue != null && !cellValue.isEmpty()) {
                                    timeSlot = new RoomReservation();
                                    timeSlot.setGeneralClass(generalClass);
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
                                    generalClass.addTimeSlot(timeSlot);
                                    timeSlot = null;
                                    duration = 0;
                                }
                            }
                        } else {
                            switch (classInfoCell.getColumnIndex()) {
                                case 0:
                                    if (cellValue.isEmpty() || cellValue.trim().isEmpty()) {
                                        generalClass.setQuantity(null);
                                    } else {
                                        generalClass.setQuantity(Integer.valueOf(cellValue));
                                    }
                                    break;
                                case 1:
                                    generalClass.setClassType(cellValue);
                                    break;
                                case 2:
                                    generalClass.setModuleCode(cellValue);
                                    break;
                                case 3:
                                    generalClass.setModuleName(cellValue);
                                    break;
                                case 4:
                                    generalClass.setLearningWeeks(cellValue);
                                    break;
                                case 5:
                                    generalClass.setMass(cellValue);
                                    break;
                                case 6:
                                    generalClass.setQuantityMax(Integer.valueOf(cellValue));
                                    break;
                                case 7:
                                    generalClass.setStudyClass(cellValue);
                                    break;
                                case 8:
                                    generalClass.setState(cellValue);
                                    break;
                                case 9:
                                    generalClass.setClassCode(cellValue);
                                    break;
                                case 10:
                                    if (cellValue.isEmpty()) {
                                        generalClass.setRefClassId(null);
                                    } else {
                                        generalClass.setRefClassId(Long.valueOf(cellValue));
                                    }
                                    break;
                                case 12:
                                    if (cellValue.isEmpty()) {
                                        generalClass.setParentClassId(null);
                                    } else {
                                        generalClass.setParentClassId(Long.valueOf(cellValue));
                                    }
                                    break;
                                case 13:
                                    generalClass.setCrew(cellValue);
                                    break;
                                case 14:
                                    generalClass.setOpenBatch(cellValue);
                                    break;
                                case 15:
                                    generalClass.setCourse(cellValue);
                                    break;
                                case 16:
                                    generalClass.setForeignLecturer(cellValue);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                generalClass.setSemester(semester);
                convertedList.add(generalClass);
            }
            workbook.close();
            return convertedList;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex);
            return null;
        }
    }


    public static ByteArrayInputStream convertRoomOccupationToExcel(List<RoomOccupation> rooms) {
        /*Init the data to map*/
        HashMap<String, List<OccupationClassPeriod>> periodMap = new HashMap<>();
        HashMap<OccupationClassPeriod, List<OccupationClassPeriod>> conflictMap = new HashMap<>();
        for(RoomOccupation room : rooms) {
            String classRoom = room.getClassRoom();
            long crewPeriod = room.getCrew().equals("S") ? 0 : 6;
            long startPeriodIndex = room.getStartPeriod() + 12L *(room.getDayIndex()-2) + crewPeriod;
            long endPeriodIndex = room.getEndPeriod() + 12L *(room.getDayIndex()-2) + crewPeriod;
            OccupationClassPeriod period = new OccupationClassPeriod(startPeriodIndex, endPeriodIndex, room.getClassCode(), classRoom);
            if(periodMap.get(classRoom) == null) {
                List<OccupationClassPeriod> initList = new ArrayList<>();
                initList.add(period);
                periodMap.put(classRoom, initList);
            } else {
                periodMap.get(classRoom).add(period);
            }
            if(conflictMap.get(period) == null) {
                List<OccupationClassPeriod> initList = new ArrayList<>();
                conflictMap.put(period, initList);
            }
        }




        /*Handle Excel write*/
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            /* Init the cell style*/
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

            /*Header*/
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
                if(room != null && !room.isEmpty()) {
                    Row roomRow = sheet.createRow(rowIndex);
                    Cell roomNameCell = roomRow.createCell(0);
                    roomNameCell.setCellValue(room);
                    roomNameCell.setCellStyle(headerStyle);
                    for (int cellIndex = 1; cellIndex <= 84; cellIndex++) {
                        Cell c = roomRow.createCell(cellIndex);
                        c.setCellStyle(boldStyle);
                        for (OccupationClassPeriod roomPeriod : periodMap.get(room)) {
                            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, (int) roomPeriod.getStartPeriodIndex(), (int) roomPeriod.getEndPeriodIndex()));
                            if(cellIndex >=  roomPeriod.getStartPeriodIndex() && cellIndex <= roomPeriod.getEndPeriodIndex()) {
                                // If cell value is not empty, append class code with comma
                                if (c.getStringCellValue() != null && !c.getStringCellValue().isEmpty()) {
                                    Set<String> classCodeSet = new HashSet<>(Arrays.stream(c.getStringCellValue().split(",")).toList());
                                    classCodeSet.add(roomPeriod.getClassCode());
                                    c.setCellValue(String.join(",", classCodeSet));
                                    c.setCellStyle(errorStyle);
                                } else {
                                    c.setCellValue(roomPeriod.getClassCode());
                                    c.setCellStyle(roomStyle);
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



     public static ByteArrayInputStream convertGeneralClassToExcel(List<GeneralClass> classes) {
        /*Handle Excel write*/
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            /* Init the cell style*/
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

            /*Header*/
            /*Handle create header info*/
            Row weekIndexRow = sheet.createRow(rowIndex);
            for (int i = 0; i < HEADERS.length; i += 1) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,i,i));
                Cell c = weekIndexRow.createCell(i);
                String classInfoString = HEADERS[i];
                c.setCellValue(classInfoString);
            }
            /*Handle create header schedule info */

            Row periodIndexRow = sheet.createRow(rowIndex+1);
            for (int i = START_COL_TO_READ_CLASS_SCHEDULE; i < START_COL_TO_READ_CLASS_SCHEDULE+42; i += 6) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,i,i+5));
                Cell c = weekIndexRow.createCell(i);
                String weekString = "Thứ " + ((i-START_COL_TO_READ_CLASS_SCHEDULE)/6 + 2);
                c.setCellValue(weekString);
            }

            for (int i = START_COL_TO_READ_CLASS_SCHEDULE; i < START_COL_TO_READ_CLASS_SCHEDULE+42; i++) {
                Cell c = periodIndexRow.createCell(i);
                String periodString = "" + ((i-START_COL_TO_READ_CLASS_SCHEDULE)%6 + 1);
                c.setCellValue(periodString);
            }

            rowIndex+=2;
            /*Handle write class info and schedule*/
            for (GeneralClass generalClass : classes) {
                Row classRow = sheet.createRow(rowIndex);
                /*Write the class info*/
                for (int i = 0 ; i <= END_COL_TO_READ_CLASS_INFO; i++ ) {
                    Cell c = classRow.createCell(i);
                    switch (i) {
                        case 0:
                            if (generalClass.getQuantity() != null) {
                                c.setCellValue(generalClass.getQuantity());
                            }
                            break;
                        case 1:
                            c.setCellValue(generalClass.getClassType());
                            break;
                        case 2:
                            c.setCellValue(generalClass.getModuleCode());
                            break;
                        case 3:
                            c.setCellValue(generalClass.getModuleName());
                            break;
                        case 4:
                            c.setCellValue(generalClass.getLearningWeeks());
                            break;
                        case 5:
                            c.setCellValue(generalClass.getMass());
                            break;
                        case 6:
                            if (generalClass.getQuantityMax() != null) {
                                c.setCellValue(generalClass.getQuantityMax());
                            }
                            break;
                        case 7:
                            c.setCellValue(generalClass.getStudyClass());
                            break;
                        case 8:
                            c.setCellValue(generalClass.getState());
                            break;
                        case 9:
                            c.setCellValue(generalClass.getClassCode());
                            break;
                        case 10:
                            if (generalClass.getRefClassId() != null) {
                                c.setCellValue(generalClass.getRefClassId());
                            }
                            break;
                        case 11:
                            if (generalClass.getId()!= null) {
                                c.setCellValue(generalClass.getId());
                            }
                            break;
                        case 12:
                            if (generalClass.getParentClassId() != null) {
                                c.setCellValue(generalClass.getParentClassId());
                            }
                            break;
                        case 13:
                            c.setCellValue(generalClass.getCrew());
                            break;
                        case 14:
                            c.setCellValue(generalClass.getOpenBatch());
                            break;
                        case 15:
                            c.setCellValue(generalClass.getCourse());
                            break;
                        case 16:
                            c.setCellValue(generalClass.getForeignLecturer());
                            break;
                        default:
                            break;
                    }
                }
                /*Write the class schedule*/
                for (int j = START_COL_TO_READ_CLASS_SCHEDULE; j < START_COL_TO_READ_CLASS_SCHEDULE + 42; j++) {
                    Cell c = classRow.createCell(j);
                    for (RoomReservation rr : generalClass.getTimeSlots().stream().filter(RoomReservation::isScheduleNotNull).toList()) {
                        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, (rr.getWeekday()-2)*6 + rr.getStartTime() -1 + START_COL_TO_READ_CLASS_SCHEDULE, START_COL_TO_READ_CLASS_SCHEDULE + (rr.getWeekday()-2)*6 + rr.getEndTime() -1));
                        if (j - START_COL_TO_READ_CLASS_SCHEDULE >= (rr.getWeekday()-2)*6 + rr.getStartTime() -1 && j-START_COL_TO_READ_CLASS_SCHEDULE <= (rr.getWeekday()-2)*6 + rr.getEndTime() -1) {
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