package openerp.openerpresourceserver.generaltimetabling.helper;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PlanGeneralClassExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Sheet1";
    static String DEFAULT_SHEET = "Sheet1";

    /**
     * Start row in excel file to classes
     */
    private final static int START_ROW_TO_READ_CLASS = 1;
    /**
     * Start column in excel to read class information (Start with column A)
     */
    private final static int START_COL_TO_READ_CLASS_INFO = 1;
    /**
     * End column in excel to read class information (End with column M)
     */
    private final static int END_COL_TO_READ_CLASS_INFO = 12;
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private static PlanGeneralClass setPlanGeneralClassInfo(int field, String value, PlanGeneralClass planGeneralClass) {
        switch (field) {
            case 1:
                planGeneralClass.setModuleCode(value);
                break;
            case 2:
                planGeneralClass.setModuleName(value);
                break;
            case 3:
                planGeneralClass.setMass(value);
                break;
            case 4:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setNumberOfClasses(intValue);
                }
                break;
            case 5:
                planGeneralClass.setLearningWeeks(value);
                break;
            case 6:
                planGeneralClass.setWeekType(value);
                break;
            case 7:
                planGeneralClass.setCrew(value);
                break;
            case 8:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setLectureMaxQuantity(intValue);
                }
                break;
            case 9:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setExerciseMaxQuantity(intValue);
                }
                break;
            case 10:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setLectureExerciseMaxQuantity(intValue);
                }
                break;
            case 11:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setQuantityMax(intValue);
                }
                break;
            case 12:
                planGeneralClass.setProgramName(value);
                break;
        }
        return planGeneralClass;
    }

    public static List<PlanGeneralClass> convertExcelToPlanGeneralClasses(
            InputStream inputStream,
            String semester)  {
        List<PlanGeneralClass> planGeneralClasses = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) {
                sheet = workbook.getSheet(DEFAULT_SHEET);
            }
            int rowIndex = START_ROW_TO_READ_CLASS;
            while (sheet.getRow(rowIndex)!= null && sheet.getRow(rowIndex).getCell(START_ROW_TO_READ_CLASS).getCellType() != Cell.CELL_TYPE_BLANK) {

                PlanGeneralClass planGeneralClass = new PlanGeneralClass();
                for (int colIndex = START_COL_TO_READ_CLASS_INFO; colIndex<=END_COL_TO_READ_CLASS_INFO ; colIndex++) {
                    if (sheet.getRow(rowIndex).getCell(colIndex).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        setPlanGeneralClassInfo(
                                colIndex,
                                String.valueOf(sheet.getRow(rowIndex).getCell(colIndex).getNumericCellValue()),
                                planGeneralClass);
                    } else if (sheet.getRow(rowIndex).getCell(colIndex).getCellType() == Cell.CELL_TYPE_STRING) {
                        setPlanGeneralClassInfo(
                                colIndex,
                                sheet.getRow(rowIndex).getCell(colIndex).getStringCellValue(),
                                planGeneralClass);
                    }
                }
                planGeneralClass.setSemester(semester);
                planGeneralClasses.add(planGeneralClass);
                rowIndex++;
            }
        } catch (Exception e ) {
            System.out.println(e);
        }
        return planGeneralClasses;
    }
}
