package openerp.openerpresourceserver.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import openerp.openerpresourceserver.model.entity.occupation.OccupationClassPeriod;
import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;

@Component
public class GeneralExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "ID", "Kỳ", "Nhóm", "SL thực", "Loại lớp", "Mã HP", "Tên HP", "Thời lượng", "SL Max",
            "Lớp học", "Trạng thái", "Mã lớp", "Kíp", "Đợt", "Khóa", "Tiết BĐ", "Thứ", "Phòng", "Tiết BĐ 2", "Thứ",
            "Phòng" };
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
                                case 12:
                                    generalClassOpened.setOpenBatch(cellValue);
                                    break;
                                case 13:
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
            System.err.println(ex);
            return null;
        }
    }


    public ByteArrayInputStream convertToExcel(List<RoomOccupation> rooms, int weekLength) {
        /**Init the data to map*/
        HashMap<String, List<OccupationClassPeriod>> periodMap = new HashMap<>();
        HashMap<String, List<OccupationClassPeriod>> conflictMap = new HashMap<>();
        for(RoomOccupation room : rooms) {
            String classRoom = room.getClassRoom();
            long crewPeriod = room.getCrew().equals("S") ? 0 : 6;
            long startPeriodIndex = room.getStartPeriod() + 12*(room.getDayIndex()-2) + 7*12*(room.getWeekIndex()-1) + crewPeriod;
            long endPeriodIndex = room.getEndPeriod() + 12*(room.getDayIndex()-2) + 7*12*(room.getWeekIndex()-1) + crewPeriod;
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
            Sheet sheet = workbook.createSheet(SHEET);
            /*Header*/
            Row headerRow = sheet.createRow(0);
            /*Header Cell*/
            for (int i = 0; i < weekLength*84; i++) {
                Cell c = headerRow.createCell(i+1);
                String periodIndexString = ((i%84)%12+1) + "/" + ((i%84)/12 +2) + "/W" + (i/84 + 1) ;
                c.setCellValue(periodIndexString);
            }
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            int rowIndex = 1;
            for (String room : periodMap.keySet()) {
                if(!room.equals("")) {
                    Row roomRow = sheet.createRow(rowIndex);
                    Cell roomNameCell = roomRow.createCell(0);
                    roomNameCell.setCellValue(room);
                    for (int cellIndex = 1; cellIndex <= weekLength*7*12; cellIndex++) {
                        Cell c = roomRow.createCell(cellIndex);
                        c.setCellStyle(boldStyle);
                        for (OccupationClassPeriod roomPeriod : periodMap.get(room)) {
                            if(cellIndex >=  roomPeriod.getStartPeriodIndex() && cellIndex <= roomPeriod.getEndPeriodIndex()) {
                                // If cell value is not empty, append class code with comma
                                if (c.getStringCellValue() != null && !c.getStringCellValue().isEmpty()) {
                                    Set<String> classCodeSet = new HashSet<>(Arrays.stream(c.getStringCellValue().split(",")).toList());
                                    classCodeSet.add(roomPeriod.getClassCode());
                                    c.setCellValue(String.join(",", classCodeSet));
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
}
