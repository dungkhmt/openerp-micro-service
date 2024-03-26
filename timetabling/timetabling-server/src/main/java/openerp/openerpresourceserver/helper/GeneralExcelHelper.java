package openerp.openerpresourceserver.helper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;

public class GeneralExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "ID", "Kỳ", "Nhóm", "SL thực", "Loại lớp", "Mã HP", "Tên HP", "Thời lượng", "SL Max",
            "Lớp học", "Trạng thái", "Mã lớp", "Kíp", "Đợt", "Khóa", "Tiết BĐ", "Thứ", "Phòng", "Tiết BĐ 2", "Thứ",
            "Phòng" };
    static String SHEET = "Schedules";
    static String DEFAULT_SHEET = "Sheet1";

    /**
     * Start row in excel file to classes
     */
    private final static int START_ROW_TO_READ_CLASS = 5;
    /**
     * Start column in excel to read class information (Start with column A)
     */
    private final static int START_COL_TO_READ_CLASS_INFO = 1;
    /**
     * End column in excel to read class information (End with column P)
     */
    private final static int END_COL_TO_READ_CLASS_INFO = 12;
    /**
     * Start column in excel to read class schedule (Start with column Q)
     */
    private final static int START_COL_TO_READ_CLASS_SCHEDULE = 13;
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

    public static List<GeneralClassOpened> convertFromExcelToGeneralClassOpened(InputStream inputStream) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(SHEET);
            List<GeneralClassOpened> convertedList = new ArrayList<GeneralClassOpened>();
            if (sheet == null) {
                sheet = workbook.getSheet(DEFAULT_SHEET);
            }
            int totalRowsNum = sheet.getLastRowNum() + 1;
            System.out.println(totalRowsNum+1);
            for (int i = START_ROW_TO_READ_CLASS; i < totalRowsNum + 1; i++) {
                Row classRow = sheet.getRow(i);
                GeneralClassOpened generalClassOpened = new GeneralClassOpened();
                for (int j = START_COL_TO_READ_CLASS_INFO; j < END_COL_TO_READ_CLASS_SCHEDULE; j++) {
                    Cell classInfoCell = classRow.getCell(j);
                    if (classInfoCell != null) {
                        String cellValue = classInfoCell.getStringCellValue();
                        if (classInfoCell.getColumnIndex() > START_COL_TO_READ_CLASS_SCHEDULE && cellValue != null) {
                            int weekday = (Integer.parseInt(cellValue) - END_COL_TO_READ_CLASS_INFO) / 6;
                            int startTime = (Integer.parseInt(cellValue) - END_COL_TO_READ_CLASS_INFO) % 6;
                            String room = cellValue;
                            generalClassOpened.addTimeSlot(new RoomReservation(startTime, weekday, room));
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
                                    generalClassOpened.setClassCode(cellValue);
                                    break;
                                case 9:
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
                    break;
                }
            }
            workbook.close();
            return convertedList;
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

}
