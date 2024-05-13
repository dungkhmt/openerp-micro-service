package openerp.openerpresourceserver.generaltimetabling.helper;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
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
    private final static int START_ROW_TO_READ_CLASS = 5;
    /**
     * Start column in excel to read class information (Start with column A)
     */
    private final static int START_COL_TO_READ_CLASS_INFO = 1;
    /**
     * End column in excel to read class information (End with column Q)
     */
    private final static int END_COL_TO_READ_CLASS_INFO = 4;
    public static final Integer NUMBER_PERIODS_PER_DAY = 6;

    public static final Integer DEFAULT_VALUE_CALCULATE_TIME = 12;

    private static PlanGeneralClass setPlanGeneralClassInfo(int field, String value, PlanGeneralClass planGeneralClass) {
        switch (field) {
            case 1:
                planGeneralClass.setProgramName(value);
                break;
            case 2:
                planGeneralClass.setModuleCode(value);
                break;
            case 3:
                planGeneralClass.setModuleName(value);
                break;
            case 4:
                planGeneralClass.setMass(value);
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
            while (sheet.getRow(rowIndex)!= null && sheet.getRow(rowIndex).getCell(START_COL_TO_READ_CLASS_INFO).getCellType() != Cell.CELL_TYPE_BLANK) {
                System.out.println("row: " + rowIndex);
                PlanGeneralClass planGeneralClass = new PlanGeneralClass();
                for (int colIndex = START_COL_TO_READ_CLASS_INFO; colIndex<=END_COL_TO_READ_CLASS_INFO ; colIndex++) {
                    System.out.println("col: " + colIndex);
                    if (sheet.getRow(rowIndex).getCell(colIndex).getCellType() == Cell.CELL_TYPE_STRING) {
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
