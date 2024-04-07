package openerp.openerpresourceserver.helper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;

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
    public static List<GeneralClassOpened> convertFromExcelToGeneralClassOpened(InputStream inputStream, String semester) {
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
                        if (classInfoCell.getColumnIndex() >= START_COL_TO_READ_CLASS_SCHEDULE && classInfoCell.getCellStyle() != null) {
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
                                    generalClassOpened.setMass(cellValue);
                                    break;
                                case 5:
                                    generalClassOpened.setQuantityMax(cellValue);
                                    break;
                                case 6:
                                    generalClassOpened.setStudyClass(cellValue);
                                    break;
                                case 7:
                                    generalClassOpened.setState(cellValue);
                                    break;
                                case 8:
                                    if(cellValue.equals("")) continue;
                                    generalClassOpened.setClassCode(cellValue);
                                    break;
                                case 9:
                                    generalClassOpened.setCrew(cellValue);
                                    break;
                                case 11:
                                    generalClassOpened.setOpenBatch(cellValue);
                                    generalClassOpened.setLearningWeeks(
                                        Converter.convertOpenBatchToLearningWeeks(generalClassOpened.getOpenBatch())
                                    );
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
                // Log for classes imported
                // System.out.println(generalClassOpened);
                convertedList.add(generalClassOpened);
            }
            workbook.close();
            return convertedList;
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

}
