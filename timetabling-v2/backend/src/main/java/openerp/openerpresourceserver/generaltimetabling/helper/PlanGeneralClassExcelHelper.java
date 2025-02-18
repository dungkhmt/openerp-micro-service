package openerp.openerpresourceserver.generaltimetabling.helper;

import lombok.extern.log4j.Log4j2;
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
import java.util.Date;
import java.util.List;

@Log4j2
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
    private final static int END_COL_TO_READ_CLASS_INFO = 13;
    public static boolean isNumeric(String str) {
        if(str == null || str.equals("")) return false;
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
                planGeneralClass.setMass(value); // khoi luong 3(2-1-0-6)
                break;
            case 4:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setNumberOfClasses(intValue);
                }else{
                    planGeneralClass.setNumberOfClasses(0);
                }
                break;
            case 5:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setLectureMaxQuantity(intValue);
                }else{
                    planGeneralClass.setNumberOfClasses(0);
                }
                break;


            case 6:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setExerciseMaxQuantity(intValue);
                }else{
                    planGeneralClass.setNumberOfClasses(0);
                }
                break;
            case 7:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setLectureExerciseMaxQuantity(intValue);
                }else{
                    planGeneralClass.setNumberOfClasses(0);
                }
                break;

            case 8:
                planGeneralClass.setProgramName(value);
                break;
            case 9:
                planGeneralClass.setLearningWeeks(value);
                break;
            case 10:
                planGeneralClass.setWeekType(value);
                break;
            case 11:
                planGeneralClass.setCrew(value);
                break;
            case 12:
                if (isNumeric(value)) {
                    double decimalNumber = Double.parseDouble(value);
                    Integer intValue = (int) decimalNumber;
                    planGeneralClass.setDuration(intValue);
                }else{
                    planGeneralClass.setDuration(0);
                }
                break;
            case 13:
                planGeneralClass.setPromotion(value);
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
                planGeneralClass.setCreatedStamp(new Date());
                for (int colIndex = START_COL_TO_READ_CLASS_INFO; colIndex<=END_COL_TO_READ_CLASS_INFO ; colIndex++) {
                    if(sheet.getRow(rowIndex).getCell(colIndex) == null) {
                        setPlanGeneralClassInfo(
                                colIndex,
                                null,
                                planGeneralClass);
                    } else if (sheet.getRow(rowIndex).getCell(colIndex).getCellType() == Cell.CELL_TYPE_NUMERIC) {
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
                log.info("convertExcelToPlanGeneralClasses, extract row " + rowIndex + " course " + planGeneralClass.getModuleCode() + " name " + planGeneralClass.getModuleName() + " semester " + planGeneralClass.getSemester() + " quantity " + planGeneralClass.getNumberOfClasses());

                rowIndex++;
            }
        } catch (Exception e ) {
            //System.out.println(e);
            e.printStackTrace();
        }
        return planGeneralClasses;
    }
}
