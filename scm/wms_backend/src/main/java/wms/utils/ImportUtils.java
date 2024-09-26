package wms.utils;
import jxl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import wms.bean.ImportConfigBean;
import wms.bean.ImportErrorBean;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Cau hinh import file Excel.
 *
 * @author HuyenNV
 * @version 1.0
 * @since 1.0
 */
public class ImportUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImportUtils.class);
    /**
     *
     */
    public static final int DATA_CONTENT_ERROR = 3;
    //Khong co loi
    private static final int NO_ERROR = 0;
    //So dong cua file nho hon dong bat dau
    private static final int FIRST_DATA_ROW_ERROR = 1;
    //Khong co du lieu
    private static final int NO_DATA_ERROR = 2;
    //Khong dung dinh dang Excel
    private static final int FORMAT_ERROR = 4;
    //Khong du bo nho
    private static final int OUT_OF_MEMORY_ERROR = 5;
    //Loi so dong vuot qua
    private static final int EXCEED_MAX_NUMBER_OF_RECORD_ERROR = 8;
    //Bieu thuc chinh quy so thuc
    private static final String DOUBLE_REGEX = "(-)?(\\d+|(\\d|\\d\\d|\\d\\d\\d)(,\\d\\d\\d)*)(\\.\\d+)?";
    //Bieu thuc chinh quy so nguyen
    private static final String LONG_REGEX = "(-)?(\\d+|(\\d|\\d\\d|\\d\\d\\d)(,\\d\\d\\d)*)(\\.0+)?";
    //Cac loai du lieu
    private static final String[] TYPE_NAMES = new String[]{
            "interger",
            "double",
            "string",
            "date"
    };
    //Kieu so nguyen
    private static final Long LONG = 0L;
    //Kieu so thuc
    private static final Long DOUBLE = 1L;
    //Kieu xau
    private static final Long STRING = 2L;
    //Kieu ngay thang
    private static final Long DATE = 3L;
    //Kieu so nguyen
    private static final String STR_LONG = "long";
    //Kieu so thuc
    private static final String STR_DOUBLE = "double";
    //Kieu xau
    private static final String STR_STRING = "string";
    //Kieu ngay thang
    private static final String STR_DATE = "date";
    /**
     *
     */
    protected ImportValidator validator = new ImportValidator();
    //#095 Start
    private boolean isIgnoreMergedRow = false;
    private Integer ignoreValidateRow = null;
    private Map<Integer, Boolean> mapMergedRow = new HashMap<Integer, Boolean>();
    //Dong du lieu dau tien
    private int firstDataRow;
    //So cot
    private int numberOfColumns;
    private String tableName;
    //Cau hinh
    private ImportConfigBean[] columnConfig;
    //So dong toi da, neu khong gioi han thi nho hon 0
    private int maxNumberOfRecord;
    //Danh sach loi
    private List<ImportErrorBean> errorList = new LinkedList<ImportErrorBean>();
    private List[] lstDupplicates;
    private List[] lstRowDupplicates;
    //Danh sach cac dong co du lieu
    private List<Integer> rowList = new LinkedList<Integer>();
    //Format so
    private DecimalFormat df = new DecimalFormat("###,###.###");
    //Dữ liệu đọc được từ file import
    private List<Object[]> data;

    /**
     * Check string is null.
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.trim().isEmpty());
    }
    /**
     * Lay xau gia tri tu file ApplicationResources.properties.
     *
     * @param key Khoa
     * @return Gia tri
     */
    public static String getApplicationResource(String key) {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("ApplicationResources", new Locale("vi"));
            return rb.getString(key);
        } catch (Exception ex) {
            LOGGER.error("getApplicationResource:", ex);
            return "";
        }

    }
    /**
     * Doc cau hinh tu file.
     *
     * @param filePath Duong dan cua file
     * @throws Exception Exception
     */
    public ImportUtils(String filePath) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        initImportUtils(fileInputStream);
    }

    /**
     * Doc cau hinh tu file.
     *
     * @param filePath Duong dan cua file
     * @throws Exception Exception
     */
    public ImportUtils(InputStream fileInputStream) throws Exception {
        initImportUtils(fileInputStream);
    }

    //Du lieu co loi

    /**
     * @param tableName
     * @param firstDataRowIndex
     * @param numberOfColumns
     * @param maxRecords
     */
    public ImportUtils(String tableName, int firstDataRowIndex, int numberOfColumns, int maxRecords) {

        this.tableName = tableName;
        this.firstDataRow = firstDataRowIndex;
        this.numberOfColumns = numberOfColumns;
        this.maxNumberOfRecord = maxRecords;
        this.columnConfig = new ImportConfigBean[numberOfColumns];
        this.lstDupplicates = new List[numberOfColumns];
        this.lstRowDupplicates = new List[numberOfColumns];
        for (int i = 0; i < numberOfColumns; i++) {
            lstDupplicates[i] = new ArrayList();
            lstRowDupplicates[i] = new ArrayList();
        }
    }

    /**
     *
     */
    public ImportUtils() {
    }

    /**
     * @return the isIgnoreMergedRow
     */
    public boolean isIgnoreMergedRow() {
        return isIgnoreMergedRow;
    }

    /**
     * @param isIgnoreMergedRow the isIgnoreMergedRow to set
     */
    public void setIgnoreMergedRow(boolean isIgnoreMergedRow) {
        this.isIgnoreMergedRow = isIgnoreMergedRow;
    }

    /**
     * @return the ignoreValidateRow
     */
    public Integer getIgnoreValidateRow() {
        return ignoreValidateRow;
    }

    /**
     * @param ignoreValidateRow the ignoreValidateRow to set
     */
    public void setIgnoreValidateRow(Integer ignoreValidateRow) {
        this.ignoreValidateRow = ignoreValidateRow;
    }

    /**
     * @return the columnConfig
     */
    public ImportConfigBean[] getColumnConfig() {
        return columnConfig;
    }

    /**
     * @param columnConfig the columnConfig to set
     */
    public void setColumnConfig(ImportConfigBean[] columnConfig) {
        this.columnConfig = columnConfig;
    }

    /**
     * @return the numberOfColumns
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * @param numberOfColumns
     */
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    //#095 End
    public void initImportUtils(InputStream fileInputStream) throws Exception {
        BufferedReader configReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(fileInputStream, "UTF8");
            configReader = new BufferedReader(inputStreamReader);
            tableName = configReader.readLine();
            firstDataRow = Integer.parseInt(configReader.readLine()) - 1; // Do la index = stt - 1
            numberOfColumns = Integer.parseInt(configReader.readLine());
            maxNumberOfRecord = Integer.parseInt(configReader.readLine());
            columnConfig = new ImportConfigBean[numberOfColumns];
            lstDupplicates = new List[numberOfColumns];
            lstRowDupplicates = new List[numberOfColumns];
            for (int i = 0; i < numberOfColumns; i++) {
                lstDupplicates[i] = new ArrayList();
                lstRowDupplicates[i] = new ArrayList();
            }
            String s;
            int colCount = 0;
            String temp;
            while ((s = configReader.readLine()) != null) {
                try {
                    if (isNullOrEmpty(s)) {
                        continue;
                    }
                    columnConfig[colCount] = new ImportConfigBean();
                    String[] a = s.split("#");
                    if (a.length < 2) {
                        columnConfig[colCount].setValues();
                    } else if (a.length == 2) {
                        String databaseColumn = a[1].trim();
                        columnConfig[colCount].setValues(databaseColumn);
                    } else {
                        String excelColumn = a[0].trim(); // ten cot trong file Excel
                        Long dataType = null; // 0: NUMBER (integer), 1: NUMBER (double), 2: VARCHAR2, 3: DATE
                        String databaseColumn = a[1].trim();
                        a[2] = a[2].trim();
                        if (a[2].equals(STR_LONG)) {
                            dataType = LONG;
                        } else if (a[2].equals(STR_DOUBLE)) {
                            dataType = DOUBLE;
                        } else if (a[2].equals(STR_STRING)) {
                            dataType = STRING;
                        } else if (a[2].equals(STR_DATE)) {
                            dataType = DATE;
                        }
                        Long length = Long.parseLong(a[3].trim()); // do dai du lieu
                        Boolean nullable = "null".equals(a[4].trim()); // co the null hay khong
                        Boolean checkDuplicate = "x".equals(a[5].trim());
                        Double max = null; //Gia Tri max
                        Double min = null; // Gia tri min
                        Boolean containsMinValue = false;
                        Boolean containsMaxValue = false;
                        if (a.length > 6) {
                            temp = a[6].trim();
                            if (!isNullOrEmpty(temp)) {
                                if (!temp.contains("+")) {
                                    min = Double.parseDouble(temp);
                                } else {
                                    containsMinValue = true;
                                    min = Double.parseDouble(temp.substring(0, temp.length() - 1));
                                }
                            }
                            if (a.length > 7) {
                                temp = a[7].trim();
                                if (!isNullOrEmpty(temp)) {
                                    if (!temp.contains("+")) {
                                        max = Double.parseDouble(temp);
                                    } else {
                                        containsMaxValue = true;
                                        max = Double.parseDouble(temp.substring(0, temp.length() - 1));
                                    }
                                }
                            }
                        }
                        columnConfig[colCount].setValues(excelColumn, dataType, nullable, length, checkDuplicate, min, max, databaseColumn, containsMinValue, containsMaxValue);
                    }
                    colCount++;
                } catch (Exception ex) {
                    LOGGER.debug("Dong loi: " + s);
                    throw ex;
                }
            }
        } catch (Exception e) {
            LOGGER.error("IMPORT UTILS", e);
        } finally {
            if (configReader != null) {
                configReader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (configReader != null) {
                fileInputStream.close();
            }

        }
    }

    /**
     * @param importValidator
     * @param req
     * @param uploadFile
     * @param fileName
     * @param dataList
     * @return
     * @throws Exception
     */
    public boolean validateCommon(ImportValidator importValidator, HttpServletRequest req, MultipartFile uploadFile, String fileName, List<Object[]> dataList)
            throws Exception {

        this.validator = importValidator;
        return validateCommon(req, uploadFile, fileName, dataList);
    }

    /**
     * Validate nhung loi chung sau: Khong du bo nho Khong dung dinh dang Khong
     * co du lieu Du lieu loi (kieu du lieu, do dai, min, max, bat buoc hay
     * khong, trung lap,...)
     *
     * @param req        HttpServletRequest
     * @param uploadFile Doi tuong upload file tu form
     * @param fileName   Ten file se luu tren server, thuong la userId... Chu y
     *                   phai luon tao thu muc import/
     * @param dataList   Danh sach du lieu
     * @return
     * @throws Exception
     */
    public boolean validateCommon(HttpServletRequest req, MultipartFile uploadFile, String fileName, List<Object[]> dataList)
            throws Exception {

        // Luu file upload, lay sheet dau tien
        Sheet sheet = null;
        boolean outOfMemory = false;
        Workbook workbook = null;
        try {
            workbook = CommonUtil.saveImportExcelFile(uploadFile, fileName, CommonUtil.getConfig("uploadFolder") + "import/");
            sheet = workbook.getSheet(0);
            if (isIgnoreMergedRow) {
                Range[] ranges = sheet.getMergedCells().clone();
                if (ranges != null && ranges.length > 0) {
                    for (Range range : ranges) {
                        mapMergedRow.put(range.getTopLeft().getRow(), true);
                    }
                }
            }
        } catch (OutOfMemoryError ex) {
            LOGGER.debug("debug", ex);
            outOfMemory = true;
        } catch (Exception ex) {
            LOGGER.error("Loi doc file import", ex);
        }
        int importResult = NO_ERROR;
        if (outOfMemory) {
            importResult = OUT_OF_MEMORY_ERROR;
        } else if (sheet == null) {
            importResult = FORMAT_ERROR;
        } else {
            int rowNum = sheet.getRows();
            if (rowNum < firstDataRow) {
                importResult = FIRST_DATA_ROW_ERROR;
                req.setAttribute("firstDataRow", firstDataRow);
            } else {
                // Doc tung dong du lieu, cho vao danh sach
                for (int row = firstDataRow; row < rowNum; row++) {
                    //#095 Start
                    if (isIgnoreMergedRow && mapMergedRow.get(row) != null) {
                        continue;
                    }
                    //#095 End
                    Cell[] cells = sheet.getRow(row);
                    if (cells.length >= 1) { //&& cells[0].getContents().trim().length() > 0
                        boolean emptyRow = true;
                        for (int col = 0; col < numberOfColumns; col++) {
                            if (col < cells.length) {
                                String content = cells[col].getContents();
                                if (!content.isEmpty()) {
                                    emptyRow = false;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (!emptyRow) {
                            Object[] a = new Object[numberOfColumns];
                            for (int col = 0; col < numberOfColumns; col++) {
                                if (!columnConfig[col].getIgnore()) {
                                    if (col < cells.length) {
                                        //tunglt17 : sua lai loi doc file voi kieu date
                                        String content;
//                                        String content = cells[col].getContents().trim();
                                        if (cells[col].getType() == CellType.LABEL) {
                                            LabelCell lc = (LabelCell) cells[col];
                                            content = lc.getString().trim();
                                        } else if (cells[col].getType() == CellType.NUMBER) {
                                            NumberCell nc = (NumberCell) cells[col];
                                            content = CommonUtil.formatNumber(nc.getValue());
                                        } else if (cells[col].getType() == CellType.DATE) {
                                            // Dang bi loi giam di 1 ngay
                                            DateCell dc = (DateCell) cells[col];
                                            content = CommonUtil.convertDateToString(dc.getDate());
                                            //content = cells[col].getContents().trim();
                                        } else if (cells[col].getType() == CellType.BOOLEAN) {
                                            BooleanCell dc = (BooleanCell) cells[col];
                                            content = String.valueOf(dc.getValue());
                                        } else {
                                            content = cells[col].getContents().trim();
                                        }
                                        //#095 Start
                                        //them bien ignoreValidateRow
                                        //neu row hien tai == ignoreValidateRow thi khong can xu ly validate
                                        if (ignoreValidateRow != null && ignoreValidateRow.equals(row)) {
                                            // thi khong can xu ly validate
                                            a[col] = content == null ? null : CharsetConverter.convertCp1258ToUTF8(content.trim());
                                        } else {
                                            //tunglt17
                                            if (content.isEmpty()) {
                                                // Kiem tra NULL
                                                if (!columnConfig[col].getNullable()) {
                                                    errorList.add(new ImportErrorBean(row, col, columnConfig[col].getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.empty"), null));
                                                } else {
                                                    String error = validator.validateNull(columnConfig[col], content, row, col, errorList);
                                                    if (error != null) {
                                                        errorList.add(new ImportErrorBean(row, col, error, content));
                                                    }
                                                }
                                                if (columnConfig[col].getCheckDuplicate()) {
                                                    lstDupplicates[col].add(null);
                                                    lstRowDupplicates[col].add(null);
                                                }
                                            } else {
                                                if (columnConfig[col].getCheckDuplicate()) {
                                                    if (lstDupplicates[col].contains(content.toLowerCase())) {
//                                                        int rowError = lstDupplicates[col].indexOf(content.toLowerCase()) + firstDataRow + 1;
                                                        int rowError = Integer.valueOf(String.valueOf(lstRowDupplicates[col].get(lstDupplicates[col].indexOf(content.toLowerCase())))) + 1;
                                                        errorList.add(new ImportErrorBean(row, col, columnConfig[col].getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.duplicate") + " " + rowError, content));
                                                    }
                                                    lstDupplicates[col].add(content.toLowerCase());
                                                    lstRowDupplicates[col].add(row);
                                                }
                                                // Kiem tra do dai du lieu
                                                if (columnConfig[col].getLength() < content.length()) {
                                                    errorList.add(new ImportErrorBean(row, col, columnConfig[col].getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.tooLong", columnConfig[col].getLength()), content));
                                                } else {
                                                    // Kiem tra kieu du lieu
                                                    a[col] = checkDataType(req, columnConfig[col], content, row, col);
                                                }
                                            }
                                        }
                                        //#095 End
                                    } else if (!columnConfig[col].getNullable()) {
                                        errorList.add(new ImportErrorBean(row, col, columnConfig[col].getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.empty"), null));
                                    }
                                }
                            }
                            dataList.add(a); // chen vao danh sach du lieu
                            rowList.add(row);
                        }
                    }
                }
                this.data = dataList;// #094 start
                if (dataList.isEmpty()) {
                    importResult = NO_DATA_ERROR;
                } else if ((maxNumberOfRecord > 0) && (dataList.size() > maxNumberOfRecord)) {
                    importResult = EXCEED_MAX_NUMBER_OF_RECORD_ERROR;
                    req.setAttribute("maxNumberOfRecord", maxNumberOfRecord);
                    req.setAttribute("actualNumberOfRecord", dataList.size());
                } else {
                    if (!errorList.isEmpty()) {
                        importResult = DATA_CONTENT_ERROR;
                        req.setAttribute("errorList", errorList);
                    }
                }
            }
        }
        req.setAttribute("importResult", importResult);
        if (importResult == NO_ERROR) {
            req.setAttribute("numOfRows", dataList.size());
        }
        if (workbook != null) {
            workbook.close();
        }
        return (importResult == NO_ERROR);
    }

    /**
     * @param req
     * @param uploadFile
     * @param fileName
     * @param column
     * @return
     * @throws Exception
     */
    public List getDataByColumn(HttpServletRequest req, MultipartFile uploadFile, String fileName, int column)
            throws Exception {

        List dataList = new ArrayList();
        Workbook workbook = CommonUtil.saveImportExcelFile(uploadFile, fileName, CommonUtil.getConfig("uploadFolder") + "import/");
        Sheet sheet = null;
        boolean outOfMemory = false;
        try {
            sheet = workbook.getSheet(0);
        } catch (OutOfMemoryError ex) {
            LOGGER.debug("debug", ex);
            outOfMemory = true;
        } catch (Exception ex) {
            LOGGER.debug("debug", ex);
        }
        int importResult = NO_ERROR;
        if (outOfMemory) {
            importResult = OUT_OF_MEMORY_ERROR;
        } else if (sheet == null) {
            importResult = FORMAT_ERROR;
        } else {
            int rowNum = sheet.getRows();
            if (rowNum < firstDataRow) {
                importResult = FIRST_DATA_ROW_ERROR;
                req.setAttribute("firstDataRow", firstDataRow);
            } else {
                // Doc tung dong du lieu, cho vao danh sach
                for (int row = firstDataRow; row < rowNum; row++) {
                    Cell[] cells = sheet.getRow(row);
                    if (cells.length > column) { //&& cells[0].getContents().trim().length() > 0
                        dataList.add(cells[column].getContents().trim()); // chen vao danh sach du lieu
                        rowList.add(row);
                    }
                }
                if (dataList.isEmpty()) {
                    importResult = NO_DATA_ERROR;
                } else if ((maxNumberOfRecord > 0) && (dataList.size() > maxNumberOfRecord)) {
                    importResult = EXCEED_MAX_NUMBER_OF_RECORD_ERROR;
                    req.setAttribute("maxNumberOfRecord", maxNumberOfRecord);
                    req.setAttribute("actualNumberOfRecord", dataList.size());
                }
            }
        }
        req.setAttribute("importResult", importResult);
        if (importResult == NO_ERROR) {
            req.setAttribute("numOfRows", dataList.size());
        }
        workbook.close();
        return dataList;
    }

    /**
     * @param typeId
     * @return
     */
    private String getTypeDataIndex(Long typeId) {

        if (typeId.equals(1L)) {
            return "string";
        } else if (typeId.equals(2L)) {
            return "date";
        } else if (typeId.equals(3L)) {
            return "double";
        } else if (typeId.equals(4L)) {
            return "long";
        } else {
            return "string";
        }
    }

    /**
     * Check kieu du lieu.
     *
     * @param columnConfig Cau hinh cot Excel
     * @param content      Noi dung
     * @param row          Hang
     * @param col          Cot
     * @return Gia tri
     */
    private Object checkDataType(HttpServletRequest req, ImportConfigBean columnConfig, String content, int row, int col) {

        Object temp = null;
        String error = null;
        try {
            if (columnConfig.getType().equals(LONG)) {
                if (!content.matches(LONG_REGEX)) {
                    throw new Exception();
                }
                int index = content.indexOf(".");
                if (index >= 0) {
                    content = content.substring(0, index);
                }
                temp = Long.parseLong(content.replace(",", ""));
                if (((columnConfig.getMaxValue() != null) || (columnConfig.getMinValue() != null))) {
                    throw new NumberFormatException();
                }
            } else if (columnConfig.getType().equals(DOUBLE)) {
                if (!content.matches(DOUBLE_REGEX)) {
                    throw new Exception();
                }
                temp = Double.parseDouble(content.replace(",", ""));
                if (((columnConfig.getMaxValue() != null) || (columnConfig.getMinValue() != null))) {
                    throw new NumberFormatException();
                }
            } else if (columnConfig.getType().equals(DATE)) {
                temp = CommonUtil.convertStringToDate(content);
//                if (temp == null) {
//                    temp = CommonUtil.convertStringToDate(content, "dd-MM-yyyy");
//                }
                if ((content != null) && !content.isEmpty() && (temp == null)) {
                    throw new Exception();
                }
            } else if (columnConfig.getType().equals(STRING)) {
                temp = content == null ? null : CharsetConverter.convertCp1258ToUTF8(content.trim());
            }
        } catch (NumberFormatException ex) {
            LOGGER.debug("", ex);
            if (temp == null) {
                throw new NullPointerException();
            }
            Double val = Double.parseDouble(temp.toString());
            if (columnConfig.getMaxValue() != null) {
                if ((!columnConfig.getContainsMaxValue() && val >= columnConfig.getMaxValue())) {
                    error = columnConfig.getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.must") + " &lt; " + df.format(columnConfig.getMaxValue());
                } else if ((columnConfig.getContainsMaxValue() && val > columnConfig.getMaxValue())) {
                    error = columnConfig.getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.must") + " <= " + df.format(columnConfig.getMaxValue());
                }
            }
            if (error == null && columnConfig.getMinValue() != null) {
                if ((!columnConfig.getContainsMinValue() && val <= columnConfig.getMinValue())) {
                    error = columnConfig.getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.must") + " &gt; " + df.format(columnConfig.getMinValue());
                } else if ((columnConfig.getContainsMinValue() && val < columnConfig.getMinValue())) {
                    error = columnConfig.getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.must") + " >= " + df.format(columnConfig.getMinValue());
                }
            }
        } catch (Exception ex) {
            LOGGER.debug("", ex);
            String dataType = CommonUtil.getApplicationResource(req, "importUtils.importError." + TYPE_NAMES[columnConfig.getType().intValue()]);
            error = columnConfig.getExcelColumn() + " " + CommonUtil.getApplicationResource(req, "importUtils.importError.incorrectType", dataType);
        }
        if (error == null) {
            error = validator.validate(columnConfig, content, row, col);
        }
        if (error == null) {
            error = validator.validate(columnConfig, content, row, col, errorList);
        }
        if (error != null) {
            errorList.add(new ImportErrorBean(row, col, error, content));
        }
        return temp;
    }

    /**
     * Kiem tra xem du lieu tai cell cho truoc co loi hay khong
     *
     * @param row : so dong
     * @param col : so cot
     * @return
     */
    public boolean checkCellError(int row, int col) {

        for (ImportErrorBean error : errorList) {
            if (error.getColumn() - 1 == col
                    && error.getRow() - 1 == rowList.get(row)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Hien thi du lieu dong loi.
     *
     * @param a Mang du lieu
     */
    private void printErrorData(Object[] a) {

        for (int col = 0; col < columnConfig.length; col++) {
            if (!columnConfig[col].getDatabaseColumn().isEmpty() && (a[col] != null)) {
                LOGGER.debug(columnConfig[col].getDatabaseColumn() + ": " + a[col]);
            }
        }
    }

    /**
     * Them loi.
     *
     * @param dataIndex
     * @param col
     * @param errorMessage
     * @param content
     */
    public void addError(int dataIndex, int col, String errorMessage, String content) {

        errorList.add(new ImportErrorBean(rowList.get(dataIndex), col, errorMessage, content));
    }

    /**
     * @param columnIndex
     * @param columnName
     * @param dbField
     * @param type
     * @param length
     * @param nullable
     * @param ignore
     * @param checkDuplicate
     * @param containsMinValue
     * @param minValue
     * @param containsMaxValue
     * @param maxValue
     */
    public void addColumnConfig(int columnIndex, String columnName,
                                String dbField, Long type, Long length, boolean nullable,
                                boolean ignore, boolean checkDuplicate, boolean containsMinValue,
                                Double minValue, boolean containsMaxValue, Double maxValue) {
        if (columnConfig[columnIndex] == null) {
            columnConfig[columnIndex] = new ImportConfigBean();
        }
        columnConfig[columnIndex].setExcelColumn(CommonUtil.NVL(columnName));
        columnConfig[columnIndex].setDatabaseColumn(CommonUtil.NVL(dbField));
        columnConfig[columnIndex].setType(CommonUtil.NVL(type));
        columnConfig[columnIndex].setLength(CommonUtil.NVL(length));
        columnConfig[columnIndex].setNullable(nullable);
        columnConfig[columnIndex].setIgnore(ignore);
        columnConfig[columnIndex].setCheckDuplicate(checkDuplicate);
        columnConfig[columnIndex].setContainsMinValue(containsMinValue);
        columnConfig[columnIndex].setMinValue(minValue);
        columnConfig[columnIndex].setContainsMaxValue(containsMaxValue);
        columnConfig[columnIndex].setMaxValue(maxValue);
    }

    /**
     * @param columnIndex
     * @param columnName
     * @param dbField
     * @param type
     * @param length
     * @param nullable
     * @param ignore
     * @param checkDuplicate
     */
    public void addColumnConfig(int columnIndex, String columnName,
                                String dbField, Long type, Long length, boolean nullable,
                                boolean ignore, boolean checkDuplicate) {

        addColumnConfig(columnIndex, columnName, dbField, type, length, nullable, ignore, checkDuplicate, false, null, false, null);
    }

    /**
     * @param columnIndex
     * @param columnName
     * @param dbField
     * @param type
     * @param length
     * @param nullable
     * @param ignore
     */
    public void addColumnConfig(int columnIndex, String columnName,
                                String dbField, Long type, Long length, boolean nullable,
                                boolean ignore) {

        addColumnConfig(columnIndex, columnName, dbField, type, length, nullable, ignore, false, false, null, false, null);
    }

    /**
     * @param columnIndex
     * @param columnName
     * @param dbField
     * @param type
     * @param length
     * @param nullable
     */
    public void addColumnConfig(int columnIndex, String columnName,
                                String dbField, Long type, Long length, boolean nullable) {

        addColumnConfig(columnIndex, columnName, dbField, type, length, nullable, false, false, false, null, false, null);
    }

    /**
     * @param columnIndex
     * @param columnName
     * @param dbField
     * @param type
     * @param length
     */
    public void addColumnConfig(int columnIndex, String columnName,
                                String dbField, Long type, Long length) {

        addColumnConfig(columnIndex, columnName, dbField, type, length, false, false, false, false, null, false, null);
    }

    /**
     * Kiem tra co loi hay khong.
     *
     * @return
     */
    public boolean hasError() {

        return !errorList.isEmpty();
    }

    /**
     * @return
     */
    public List<ImportErrorBean> getErrorList() {

        return errorList;
    }

    /**
     * @param firstDataRow
     */
    public void setFirstDataRow(int firstDataRow) {
        this.firstDataRow = firstDataRow;
    }

    /**
     * @param maxNumberOfRecord
     */
    public void setMaxNumberOfRecord(int maxNumberOfRecord) {
        this.maxNumberOfRecord = maxNumberOfRecord;
    }

    /**
     * @return
     */
    public List<Integer> getRowList() {
        return rowList;
    }

    // #094 start

    /**
     * @param rowList
     */
    public void setRowList(List<Integer> rowList) {
        this.rowList = rowList;
    }

    public List<Object[]> getData() {
        return data;
    }

    // #094 end
}
