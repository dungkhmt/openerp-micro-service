package wms.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author vietlv2
 * @version 1.0
 * @since Jul, 2018
 */
public class CommonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);
    private static final String[] SIGNED_ARR = new String[]{"à", "á", "ạ", "ả", "ã", "â", "ầ", "ấ", "ậ", "ẩ", "ẫ",
            "ă", "ằ", "ắ", "ặ", "ẳ", "ẵ", "è", "é", "ẹ", "ẻ", "ẽ", "ê", "ề", "ế", "ệ", "ể", "ễ", "ì", "í", "ị", "ỉ",
            "ĩ", "ò", "ó", "ọ", "ỏ", "õ", "ô", "ồ", "ố", "ộ", "ổ", "ỗ", "ơ", "ờ", "ớ", "ợ", "ở", "ỡ", "ù", "ú", "ụ",
            "ủ", "ũ", "ư", "ừ", "ứ", "ự", "ử", "ữ", "ỳ", "ý", "ỵ", "ỷ", "ỹ", "đ", "À", "Á", "Ạ", "Ả", "Ã", "Â", "Ầ",
            "Ấ", "Ậ", "Ẩ", "Ẫ", "Ă", "Ằ", "Ắ", "Ặ", "Ẳ", "Ẵ", "È", "É", "Ẹ", "Ẻ", "Ẽ", "Ê", "Ề", "Ế", "Ệ", "Ể", "Ễ",
            "Ì", "Í", "Ị", "Ỉ", "Ĩ", "Ò", "Ó", "Ọ", "Ỏ", "Õ", "Ô", "Ồ", "Ố", "Ộ", "Ổ", "Ỗ", "Ơ", "Ờ", "Ớ", "Ợ", "Ở",
            "Ỡ", "Ù", "Ú", "Ụ", "Ủ", "Ũ", "Ư", "Ừ", "Ứ", "Ự", "Ử", "Ữ", "Ỳ", "Ý", "Ỵ", "Ỷ", "Ỹ", "Đ"};
    private static final String[] UNSIGNED_ARR = new String[]{"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "i", "i", "i", "i",
            "i", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "u", "u", "u",
            "u", "u", "u", "u", "u", "u", "u", "u", "y", "y", "y", "y", "y", "d", "A", "A", "A", "A", "A", "A", "A",
            "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "E", "E", "E", "E", "E", "E", "E", "E", "E", "E", "E",
            "I", "I", "I", "I", "I", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O",
            "O", "U", "U", "U", "U", "U", "U", "U", "U", "U", "U", "U", "Y", "Y", "Y", "Y", "Y", "D"};

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    /**
     * Cast BigDemical to Long
     *
     * @param value
     * @return
     */
    public static Long demicalToLong(BigDecimal value) {
        if (value == null) {
            return 0L;
        } else {
            return value.longValue();
        }
    }

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
     * Check list object is null.
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(List data) {
        return (data == null || data.isEmpty());
    }

    /**
     * Chuyen doi tuong String thanh doi tuong Date.
     *
     * @param date Xau ngay, co dinh dang duoc quy trinh trong file Constants
     * @return Doi tuong Date
     * @throws Exception Exception
     */
    public static Date convertStringToDate(String date) throws Exception {
        if (date == null || date.trim().isEmpty()) {
            return null;
        } else {
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            dateFormat.setLenient(false);
            return dateFormat.parse(date);
        }
    }

    /**
     * Chuyen doi tuong Date thanh doi tuong String.
     *
     * @param date Doi tuong Date
     * @return Xau ngay, co dang dd/MM/yyyy
     */
    public static String convertDateToString(Date date) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.format(date);
        }
    }

    /**
     * Chuyen doi tuong Date thanh doi tuong String.
     *
     * @param date Doi tuong Date
     * @return Xau ngay, co dang dd/MM/yyyy
     */
    public static String convertDateToString(Date date, String pattern) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(date);
        }
    }

    /**
     * Conver tu string sang date theo dinh dang mong muon
     *
     * @param date
     * @param pattern : kieu dinh dang vd: "dd/MM/yyyy hh:MM"
     * @return
     * @throws Exception
     */
    public static Date convertStringToDateTime(String date, String pattern) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            dateFormat.setLenient(false);
            try {
                return dateFormat.parse(date);
            } catch (Exception ex) {
                LOGGER.error("convertStringToDateTime", ex);
                return null;
            }
        }
    }

    /**
     * Lay gia tri tu file config.properties.
     *
     * @param key Khoa
     * @return Gia tri
     */
    public static String getConfig(String key) {
        ResourceBundle rb = ResourceBundle.getBundle("config");
        return rb.getString(key);
    }

    /**
     * Lay gia tri tu file don_gia_ttbl.properties.
     *
     * @param key Khoa
     * @return Gia tri
     */
    public static String getXNKConfig(String key) {
        ResourceBundle rb = ResourceBundle.getBundle("xnk_ttbl_config");
        return rb.getString(key);
    }

    /**
     * Lay gia tri tu file permissionCode.properties.
     *
     * @param key Khoa
     * @return Gia tri
     */
    public static String getPermissionCode(String key) {
        ResourceBundle rb = ResourceBundle.getBundle("permissionCode");
        return rb.getString(key);
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
     * Lay xau gia tri tu file ApplicationResources.properties.
     *
     * @param key Khoa
     * @return Gia tri
     */
    public static String getApplicationResource(HttpServletRequest req, String key) {
        try {
            String locale = getCurrentLanguage(req);
            ResourceBundle rb = ResourceBundle.getBundle("ApplicationResources", new Locale(locale));
            return rb.getString(key);
        } catch (Exception ex) {
            LOGGER.error("getApplicationResource:", ex);
            return "";
        }

    }

    /**
     * Lay xau gia tri tu file ApplicationResources.properties.
     *
     * @param key Khoa
     * @return Gia tri
     */
    public static String getApplicationResource(String key, Object... args) {
        try {
            ResourceBundle rb = ResourceBundle.getBundle("ApplicationResources", new Locale("vi"));
            return String.format(rb.getString(key), args);
        } catch (Exception ex) {
            LOGGER.error("getApplicationResource:", ex);
            return "";
        }

    }

    /**
     * Lay xau gia tri tu file ApplicationResources.properties.
     *
     * @param key Khoa
     * @return Gia tri
     */
    public static String getApplicationResource(HttpServletRequest req, String key, Object... args) {
        try {
            String locale = getCurrentLanguage(req);
            ResourceBundle rb = ResourceBundle.getBundle("ApplicationResources", new Locale(locale));
            return String.format(rb.getString(key), args);
        } catch (Exception ex) {
            LOGGER.error("getApplicationResource:", ex);
            return "";
        }

    }

    public static Object NVL(Object value, Object defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Double NVL(Double value) {

        return NVL(value, new Double(0));
    }

    public static Integer NVL(Integer value) {
        return value == null ? new Integer(0) : value;
    }

    public static Integer NVL(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static BigDecimal NVL(BigDecimal value) {
        return value == null ? new BigDecimal(0) : value;
    }

    public static Double NVL(Double value, Double defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Long NVL(Long value, Long defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static String NVL(String value, String nullValue, String notNullValue) {

        return value == null ? nullValue : notNullValue;
    }

    public static String NVL(String value, String defaultValue) {

        return NVL(value, defaultValue, value);
    }

    public static String NVL(Object value, String defaultValue) {
        return value == null ? defaultValue : NVL(value.toString(), defaultValue, value.toString());
    }

    public static String NVL(String value) {

        return NVL(value, "");
    }

    public static Long NVL(Long value) {

        return NVL(value, 0L);
    }

    public static Long checkBoxValue(Long value) {
        if (value != null && value.equals(1L)) {
            return 1L;
        } else {
            return 0L;
        }
    }

    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-FORWARDED-FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Convert String to list
     *
     * @param sourceString
     * @param pattern
     * @return
     */
    public static List<String> toList(String sourceString, String pattern) {
        List<String> results = new LinkedList<String>();
        if ("".equals(NVL(sourceString).trim())) {
            return results;
        }
        String[] sources = NVL(sourceString).split(pattern);
        for (String source : sources) {
            results.add(NVL(source).trim());
        }
        return results;
    }

    /**
     * Convert String to list
     *
     * @param sourceString
     * @param pattern
     * @return
     */
    public static List<Long> toLongList(String sourceString, String pattern) {
        List<Long> results = new LinkedList<Long>();
        String[] sources = NVL(sourceString).split(pattern);
        for (String source : sources) {
            if (!CommonUtil.isNullOrEmpty(source)) {
                results.add(Long.valueOf(source));
            }
        }
        return results;
    }

    /**
     * @param endDate
     * @param startDate
     * @return
     */
    public static double monthsBetween(Date endDate, Date startDate) {
        Calendar endCalendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        startCalendar.setTime(startDate);
        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        return diffMonth;
    }

    /**
     * @param month
     * @param year
     * @return
     * @throws Exception
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * @param month
     * @param year
     * @return
     * @throws Exception
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * Format Double theo he thong kieu Phap
     *
     * @param itemValue
     * @return
     */
    public static String formatFrenchNumber(Double itemValue) {
        if (itemValue != null) {
            try {
                DecimalFormat df = new DecimalFormat();
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                symbols.setGroupingSeparator('.');
                df.setDecimalFormatSymbols(symbols);

                return df.format(itemValue);
            } catch (Exception ex) {
                LOGGER.error("formatFrenchNumber", ex);
                return String.valueOf(itemValue);
            }
        } else {
            return "";
        }
    }

    /**
     * Convert image to base64 code.
     *
     * @param imageFile
     * @return
     * @throws IOException
     */
    public static String getBase64StringOfImage(File imageFile) throws IOException {
        String imgString;
        BufferedImage buffImage = ImageIO.read(imageFile);
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            ImageIO.write(buffImage, "jpg", bout);
            byte[] imageBytes = bout.toByteArray();
            imgString = Base64.getEncoder().encodeToString(imageBytes);
        }
        return imgString;
    }

    /**
     * Bypass HHTPS
     *
     * @throws Exception
     */
//    public static void disableSslVerification() throws Exception {
//        // Create a trust manager that does not validate certificate chains
//        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//            @Override
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//
//            @Override
//            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//            }
//        }};
//
//        // Install the all-trusting trust manager
//        SSLContext sc = SSLContext.getInstance("SSL");
//        sc.init(null, trustAllCerts, new SecureRandom());
//        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//        // Create all-trusting host name verifier
//        HostnameVerifier allHostsValid = new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        };
//
//        // Install the all-trusting host verifier
//        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//    }

    /**
     * Trunc date.
     *
     * @param inputDate
     * @return
     */
    public static Date TRUNC(Date inputDate) {
        if (inputDate == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(inputDate);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR, 0);
            return cal.getTime();
        }
    }

    /**
     * kiem tra 1 xau rong hay null khong
     *
     * @param str         : xau
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filter(String str, StringBuilder queryString, List<Object> paramList, String field) {
        if ((str != null) && !"".equals(str.trim())) {
            queryString.append(" AND LOWER(").append(field).append(") LIKE ? ESCAPE '/'");
            str = str.replace("  ", " ");
            str = "%" + str.trim().toLowerCase().replace("/", "//").replace("_", "/_").replace("%", "/%") + "%";
            paramList.add(str);
        }
    }

    /**
     * Tìm kiếm theo đnn
     *
     * @param str         : xau
     * @param queryString
     * @param paramList
     * @param field
     */
    public static String fGetLanguage(String tableName, String columnName, String objectId, String langCode, String defaultValue) {
        return String.format("F_GET_LANGUAGE('%s', '%s', %s, '%s', %s)", tableName, columnName, objectId, langCode, defaultValue);

    }

    /**
     * kiem tra 1 so rong hay null khong
     *
     * @param n           So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filter(Long n, StringBuilder queryString, List<Object> paramList, String field) {
        if ((n != null) && (n > 0L)) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(n);
        }
    }

    /**
     * kiem tra 1 so rong hay null khong
     *
     * @param n           So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filter(Double n, StringBuilder queryString, List<Object> paramList, String field) {
        if ((n != null) && (n > 0L)) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(n);
        }
    }

    /**
     * kiem tra 1 so rong hay null khong
     *
     * @param n           So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filter(Boolean n, StringBuilder queryString, List<Object> paramList, String field) {
        if (n != null) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(n);
        }
    }

    /**
     * kiem tra 1 Integer rong hay null khong
     *
     * @param n           So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filter(Integer n, StringBuilder queryString, List<Object> paramList, String field) {
        if ((n != null) && (n > 0)) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(n);
        }
    }

    /**
     * kiem tra 1 xau rong hay null khong
     *
     * @param date        So
     * @param queryString
     * @param field
     * @param paramList
     */
    public static void filter(Date date, StringBuilder queryString, List<Object> paramList, String field) {
        if ((date != null)) {
            queryString.append(" AND ").append(field).append(" = ? ");
            paramList.add(date);
        }
    }

    /**
     * kiem tra 1 xau rong hay null khong
     *
     * @param arrIds
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filterSelectInL(String arrIds, StringBuilder queryString, List<Object> paramList, String field) {
        if (!CommonUtil.isNullOrEmpty(arrIds)) {
            queryString.append(" AND ").append(field).append(" IN (-1 ");
            String[] ids = arrIds.split(",");
            for (String strId : ids) {
                queryString.append(", ?");
                paramList.add(Long.parseLong(strId.trim()));
            }
            queryString.append(" ) ");
        }
    }

    /**
     * Kiem tra lon hon hoac bang.
     *
     * @param obj         So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filterGe(Object obj, StringBuilder queryString, List<Object> paramList, String field) {
        if (obj != null && !"".equals(obj)) {
            queryString.append(" AND ").append(field).append(" >= ? ");
            paramList.add(obj);
        }
    }

    /**
     * Kiem tra nho hon hoac bang.
     *
     * @param obj         So
     * @param queryString
     * @param paramList
     * @param field
     */
    public static void filterLe(Object obj, StringBuilder queryString, List<Object> paramList, String field) {
        if (obj != null && !"".equals(obj)) {
            queryString.append(" AND ").append(field).append(" <= ? ");
            paramList.add(obj);
        }
    }

    /**
     * filter for inserting preparedStatement
     *
     * @param value
     * @param index
     * @param preparedStatement
     * @throws Exception
     */
    public static void filter(String value, PreparedStatement preparedStatement, int index) throws Exception {

        if (value != null) {
            preparedStatement.setString(index, value.trim());
        } else {
            preparedStatement.setNull(index, java.sql.Types.NULL);
        }
    }

    /**
     * @param value
     * @param preparedStatement
     * @param index
     * @throws Exception
     */
    public static void filter(Double value, PreparedStatement preparedStatement, int index) throws Exception {

        if (value != null) {
            preparedStatement.setDouble(index, value);
        } else {
            preparedStatement.setNull(index, java.sql.Types.NULL);
        }
    }

    /**
     * @param value
     * @param preparedStatement
     * @param index
     * @throws Exception
     */
    public static void filter(Long value, PreparedStatement preparedStatement, int index) throws Exception {

        if (value != null) {
            preparedStatement.setLong(index, value);
        } else {
            preparedStatement.setNull(index, java.sql.Types.NULL);
        }
    }

    /**
     * @param value
     * @param preparedStatement
     * @param index
     * @throws Exception
     */
    public static void filter(Object value, PreparedStatement preparedStatement, int index) throws Exception {
        if (value != null) {
            if (value instanceof Date) {
                Date temp = (Date) value;
                preparedStatement.setObject(index, new java.sql.Timestamp(temp.getTime()));
            } else {
                preparedStatement.setObject(index, value);
            }

        } else {
            preparedStatement.setNull(index, java.sql.Types.NULL);
        }
    }

    /**
     * @param value
     * @param preparedStatement
     * @param index
     * @throws Exception
     */
    public static void filter(java.sql.Date value, PreparedStatement preparedStatement, int index) throws Exception {

        if (value != null) {
            preparedStatement.setDate(index, value);
        } else {
            preparedStatement.setNull(index, java.sql.Types.NULL);
        }
    }

    /**
     * kiem tra mot chuoi co chua ky tu Unicode khong
     *
     * @param str
     * @return
     */
    public static boolean containUnicode(String str) {
        String signChars = "ăâđêôơưàảãạáằẳẵặắầẩẫậấèẻẽẹéềểễệếìỉĩịíòỏõọóồổỗộốờởỡợớùủũụúừửữựứỳỷỹỵý";
        if ("".equals(str)) {
            return false;
        } else {
            int count = 0;
            String subStr;
            while (count < str.length()) {
                subStr = str.substring(count, count + 1);
                if (signChars.contains(subStr)) {
                    return true;
                }
                count++;
            }
        }
        return false;
    }

    /**
     * kiem tra mot chuoi co chua ky tu Unicode khong
     *
     * @param str
     * @return
     */
    public static boolean containPhoneNumber(String str) {
        String signChars = "0123456789";
        if ("".equals(str)) {
            return false;
        } else {
            int count = 0;
            String subStr;
            while (count < str.length()) {
                subStr = str.substring(count, count + 1);
                if (!signChars.contains(subStr)) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * replaceSpecialKeys
     *
     * @param str String
     * @return String
     */
    public static String replaceSpecialKeys(String str) {
        str = str.replace("  ", " ");
        str = "%" + str.trim().toLowerCase().replace("/", "//").replace("_", "/_").replace("%", "/%") + "%";
        return str;
    }


    /**
     * Format so.
     *
     * @param d So
     * @return Xau
     */
    public static String formatNumber(Double d) {
        if (d == null) {
            return "";
        } else {
            DecimalFormat format = new DecimalFormat("######.#####");
            return format.format(d);
        }
    }

    /**
     * Format so.
     *
     * @param d So
     * @return Xau
     */
    public static String formatNumber(Long d) {
        if (d == null) {
            return "";
        } else {
            DecimalFormat format = new DecimalFormat("######");
            return format.format(d);
        }
    }

    /**
     * Format so.
     *
     * @param d      So
     * @param parten
     * @return Xau
     */
    public static String formatNumber(Object d, String parten) {
        if (d == null) {
            return "";
        } else {
            DecimalFormat format = new DecimalFormat(parten);
            return format.format(d);
            //NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            //DecimalFormat formatter = (DecimalFormat)nf;
            //formatter.applyPattern(parten);
            //return formatter.format(d);
        }
    }


    /**
     * Chuyen string -> List Long
     *
     * @param inpuString
     * @param separator
     * @return
     */
    public static List<Long> string2ListLong(String inpuString, String separator) {
        List<Long> outPutList = new ArrayList<Long>();

        if (inpuString != null && !"".equals(inpuString.trim()) && separator != null && !"".equals(separator.trim())) {
            String[] idArr = inpuString.split(separator);
            for (String idArr1 : idArr) {
                if (idArr1 != null && !"".equals(idArr1.trim())) {
                    outPutList.add(Long.parseLong(idArr1.trim()));
                }
            }
        }

        return outPutList;
    }

    public static List<String> string2ListString(String inpuString, String separator) {
        List<String> outPutList = new ArrayList<String>();
        if (inpuString != null && !"".equals(inpuString.trim()) && separator != null && !"".equals(separator.trim())) {
            String[] idArr = inpuString.split(separator);
            for (String idArr1 : idArr) {
                if (!isNullOrEmpty(idArr1)) {
                    outPutList.add(idArr1.trim());
                }
            }
        }
        return outPutList;
    }

    /**
     * chuyen list string ve chuỗi phuc vu tim kiem
     *
     * @param lstObject lst
     * @param separator
     * @return ket qua
     * @throws Exception ex
     */
    public static String convertListToString(List lstObject, String separator) throws Exception {
        try {
            if (lstObject != null && !lstObject.isEmpty()) {
                StringBuilder result = new StringBuilder("");
                int size = lstObject.size();
                result.append("'").append(lstObject.get(0)).append("'");
                for (int i = 1; i < size; i++) {
                    result.append(separator);
                    result.append("'");
                    result.append(lstObject.get(i));
                    result.append("'");
                }
                return result.toString();
            } else {
                return "";
            }
        } catch (Exception e) {
            LOGGER.error("convertListToString:", e);
            throw e;
        }
    }

    public static Date subDayFromDate(Date date, int day) {
        if (date != null) {
            Calendar cld = Calendar.getInstance();
            cld.setTime(date);
            cld.add(Calendar.DATE, -day);
            return cld.getTime();
        }
        return null;
    }

    /**
     * Convert Object To String Json
     *
     * @param object
     * @return
     */
    public static String convertObjectToStringJson(Object object) {
        String strMess = "";
        try {
            Gson gson = new Gson();
            strMess = gson.toJson(object);
        } catch (Exception e) {
            LOGGER.error("Error! Convert object to json", e);
        }
        return strMess;
    }

    /**
     * Get long parameter.
     *
     * @param req
     * @param name
     * @return
     */
    public static Long getParameterLong(HttpServletRequest req, String name) {
        try {
            return Long.parseLong(req.getParameter(name));
        } catch (Exception ex) {
            LOGGER.error("Get parameter long from: " + name + " ERROR: ", ex);
            return null;
        }
    }

    /**
     * Get long parameter.
     *
     * @param req
     * @param name
     * @return
     */
    public static Integer getParameterInt(HttpServletRequest req, String name) {
        try {
            return Integer.parseInt(req.getParameter(name));
        } catch (Exception ex) {
            LOGGER.error("Get parameter long from: " + name + " ERROR: ", ex);
            return null;
        }
    }

    /**
     * Checks if is collection empty.
     *
     * @param collection the collection
     * @return true, if is collection empty
     */
    private static boolean isCollectionEmpty(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is object empty.
     *
     * @param object the object
     * @return true, if is object empty
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            if (((String) object).trim().length() == 0) {
                return true;
            }
        } else if (object instanceof Collection) {
            return isCollectionEmpty((Collection<?>) object);
        }
        return false;
    }



    /**
     * Builds the paginated query.
     *
     * @param baseQuery          the base query
     * @param paginationCriteria the pagination criteria
     * @return the string
     */
    public static String buildCountQuery(String baseQuery) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM (#BASE_QUERY#) FILTERED_ORDERD_RESULTS ");
        String finalQuery = null;
        finalQuery = sb.toString().replace("#BASE_QUERY#", baseQuery);
        return (null == finalQuery) ? baseQuery : finalQuery;
    }

    /**
     * <p>
     * Escapes the characters in a <code>String</code> to be suitable to pass to an
     * SQL query.
     * </p>
     *
     * <p>
     * For example,
     *
     * <pre>
     * statement
     * 		.executeQuery("SELECT * FROM MOVIES WHERE TITLE='" + StringEscapeUtils.escapeSql("McHale's Navy") + "'");
     * </pre>
     * </p>
     *
     * <p>
     * At present, this method only turns single-quotes into doubled single-quotes
     * (<code>"McHale's Navy"</code> => <code>"McHale''s Navy"</code>). It does not
     * handle the cases of percent (%) or underscore (_) for use in LIKE clauses.
     * </p>
     * <p>
     * see http://www.jguru.com/faq/view.jsp?EID=8881
     *
     * @param str the string to escape, may be null
     * @return a new String, escaped for SQL, <code>null</code> if null string input
     */
    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("'", "''").replace("/", "//").replace("_", "/_").replace("%", "/%");
    }

    /**
     * getAvatarPath
     *
     * @param fileId
     * @return
     */
    public static String getAvatarPath(String fileId) {
        if (!CommonUtil.isNullOrEmpty(fileId)) {
            return String.format("%s/avatar/image/%s", CommonUtil.getConfig("fileStorage.serverUrl"), fileId);
        } else {
            return null;
        }
    }

    /**
     * ham tra lai prefix dia chi email theo dinh dang chuan
     *
     * @param fullName ten nhap vao
     * @return phan prefix cua dia chi email Nguyen van bien --> result biennv
     */
    public static String getPrefixEmailByFullName(String fullName) {
        String emailResult = "";
        if (!CommonUtil.isNullOrEmpty(fullName)) {
            fullName = removeSign(fullName);
            String[] str = fullName.trim().split(" ");
            int strLen = str.length;
            emailResult = str[strLen - 1];
            for (int i = 0; i < strLen - 1; i++) {
                String subStr = str[i].trim();
                if (!"".equals(subStr)) {
                    emailResult += subStr.substring(0, 1);
                }
            }
        }
        return emailResult.toLowerCase();
    }

    /**
     * Loai bo cac dau, ten file chi chua cac ky tu ASCII.
     *
     * @param originalName
     * @return String : xau sau khi bo dau
     */
    public static String removeSign(String originalName) {
        if (originalName == null) {
            return "";
        }
        String result = originalName;
        for (int i = 0; i < SIGNED_ARR.length; i++) {
            result = result.replaceAll(SIGNED_ARR[i], UNSIGNED_ARR[i]);
        }
        return result;
    }

    /**
     * lay user id login
     *
     * @return
     */
    public static String getUserLoginName(HttpServletRequest req) {
        // TODO Auto-generated method stub
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal().toString();
    }

    /**
     * getMailConfigProperties
     *
     * @param key
     * @return
     */
    public static String getMailConfigProperties(String key) {
        ResourceBundle rb = ResourceBundle.getBundle("mail");
        return rb.getString(key);
    }

    /**
     * getCurrentLanguage
     *
     * @return
     */
    public static String getCurrentLanguage(HttpServletRequest req) {
        // TODO Auto-generated method stub
        String langCode = req.getHeader("Current-Language");
        return NVL(langCode, "vi");
    }

    /**
     * @return
     */
    public static Long getCurrentMarketId(HttpServletRequest req) {
        // TODO Auto-generated method stub
        String marketId = req.getHeader("Current-Market");
        if (isNullOrEmpty(marketId)) {
            return -1L;
        }
        return Long.valueOf(marketId);
    }

    /**
     * Luu file import Excel.
     *
     * @param uploadFile Doi tuong MultipartFile
     * @param fileName   Ten file
     * @param uploadPath Duong dan thu muc
     * @return Doi tuong Workbook
     * @throws Exception Exception
     */
    public static Workbook saveImportExcelFile(MultipartFile uploadFile, String fileName, String uploadPath)
            throws Exception {
        fileName = CommonUtil.getSafeFileName(CommonUtil.removeSign(fileName));
        if (isAllowedType(fileName)) {
            saveFile(uploadFile, fileName, uploadPath);
            File desDir = new File(uploadPath);
            File serverFile = new File(desDir.getAbsolutePath() + File.separator + fileName);
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252"); // UTF-8
            ws.setCellValidationDisabled(true);
            Workbook workbook = Workbook.getWorkbook(serverFile, ws);
            return workbook;
        } else {
            throw new Exception("FILE TYPE NOT ALLOW");
        }
    }

    /**
     * Loai cac ki tu /, \, null trong ten file Fix loi path traversal
     *
     * @param input : string
     * @return String
     */
    public static String getSafeFileName(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != '/' && c != '\\' && c != 0) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * @param fileName
     * @return
     */
    public static boolean isAllowedType(String fileName) {
        if (fileName != null && !"".equals(fileName.trim())) {
            String[] allowedType = {".jpg", ".jpeg", ".png", ".doc", ".docx", ".xls", ".xlsx", ".pdf", ".rar", ".zip",
                    ".gif", ".txt", ".log", ".xml", ".7zip"};
            String ext = extractFileExt(fileName);
            if (ext == null) {
                return true;
            }
            ext = ext.toLowerCase();
            for (String extendFile : allowedType) {
                if (extendFile.equals(ext)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param fileName
     * @return
     */
    public static String extractFileExt(String fileName) {
        int dotPos = fileName.lastIndexOf(".");
        if (dotPos != -1) {
            return fileName.substring(dotPos);
        }
        return null;
    }

    /**
     * Luu file len server.
     *
     * @param uploadFile Doi tuong FormFile
     * @param fileName   Ten file
     * @param uploadPath Duong dan thu muc
     * @throws Exception Exception
     */
    public static void saveFile(MultipartFile uploadFile, String fileName, String uploadPath) throws Exception {
        if (isAllowedType(uploadFile.getName())) {
            File desDir = new File(uploadPath);
            if (!desDir.exists()) {
                desDir.mkdir();
            }
            String url = desDir.getAbsolutePath() + File.separator + getSafeFileName(fileName);
            try (OutputStream outStream = new FileOutputStream(url)) {
                InputStream inStream = uploadFile.getInputStream();
                int bytesRead;
                byte[] buffer = new byte[1024 * 8];
                while ((bytesRead = inStream.read(buffer, 0, 1024 * 8)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                inStream.close();
//                outStream.close();
            }
        } else {
            throw new Exception("FILE TYPE NOT ALLOW");
        }
    }

    /**
     * Cong valueToAdd ngay vao ngay inputDate.
     *
     * @param inputDate
     * @param valueToAdd
     * @return
     * @throws Exception
     */
    public static Date addDate(Date inputDate, int valueToAdd) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(inputDate);
        c.add(Calendar.DATE, valueToAdd);
        return c.getTime();
    }

    /**
     * @param month
     * @param year
     * @return
     * @throws Exception
     */
    public static Date getLastDayOfMonth(int month, int year) throws Exception {
        if (month > 9) {
            return CommonUtil.convertStringToDate(getDaysOfMonth(month, year) + "/" + month + "/" + year);
        } else {
            return CommonUtil.convertStringToDate(getDaysOfMonth(month, year) + "/0" + month + "/" + year);
        }
    }

    /**
     * So ngay trong thang.
     *
     * @param month Thang, thang chuan
     * @param year  Nam, nam chuan
     * @return So ngay trong thang
     */
    public static int getDaysOfMonth(int month, int year) {
        final int[] MONTH_DAYS = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int num = MONTH_DAYS[month];
        if (month == 2) {
            if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)) {
                num++;
            }
        }
        return num;
    }

    /**
     * @param startDate1
     * @param endDate1
     * @param startDate2
     * @param endDate2
     * @return
     */
    public static boolean checkDates(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        if (startDate1 == null) {
            return true;
        } else {
            if (startDate2.before(startDate1)) {
                return false;
            } else {
                if (endDate1 != null) {
                    if (startDate2.after(endDate1)) {
                        return false;
                    } else {
                        if (endDate2 == null) {
                            return false;
                        } else {
                            return (!endDate2.after(endDate1));
                        }
                    }
                } else {
                    return true;
                }
            }
        }
    }

    /**
     * prefixByLanguage
     *
     * @param req
     * @return
     */
    public static String prefixByLanguage(HttpServletRequest req) {
        String lang = CommonUtil.getCurrentLanguage(req);
        if ("vi".equals(lang)) {
            return "vi";
        } else {
            return "en";
        }
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    /**
     * filterConflictDate
     *
     * @return
     */
    public static void filterConflictDate(Date startDate, Date endDate, StringBuilder queryString, List<Object> paramList
            , String fieldStartDate, String fieldEndDate) {
        String cond = " AND (0=1 ";
        if (endDate == null) { // input endDate == null
            /**
             * ----------------Si|------------------------------->Ei
             * -------------------S|------------------------------->E
             */
            cond += " OR ( " + fieldEndDate + " IS NOT NULL ";
            cond += "     AND DATE(" + fieldEndDate + ") >= ? ) ";
            paramList.add(startDate);
            cond += " OR ( " + fieldEndDate + " IS NULL ";
            cond += "     AND DATE(" + fieldStartDate + ") >= ? ) ";
            paramList.add(startDate);
        } else { // input endDate != null
            /**
             * ----------------Si|-------------------------------|Ei
             * ------------------------S-----------------------E-------->
             */
            cond += " OR ( " + fieldEndDate + " IS NOT NULL ";
            cond += "     AND DATE(" + fieldEndDate + ") >= ? ";
            paramList.add(startDate);
            cond += "     AND DATE(?) >= " + fieldStartDate + " ) ";
            paramList.add(endDate);

            cond += " OR ( " + fieldEndDate + " IS NULL ";
            cond += "     AND DATE(" + fieldStartDate + ") BETWEEN ? AND ? ) ";
            paramList.add(startDate);
            paramList.add(endDate);
        }
        cond += " ) ";
        queryString.append(cond);
    }

    /**
     * ham kiem tra data picker co duoc cau hinh lay du lieu theo thi truong hang khong
     *
     * @param objectBO
     * @return
     */
    public static boolean isByMarketCompany(String objectBO) {
        // TODO Auto-generated method stub
        ResourceBundle rb = ResourceBundle.getBundle("dataPicker");
        try {
            return "true".equalsIgnoreCase(rb.getString(String.format("%s.marketCompanyId", objectBO)));
        } catch (Exception e) {
            LOGGER.error("isByMarketCompany:", e);
            return false;
        }
    }

    /**
     * ham kiem tra data picker co duoc cau hinh lay du lieu theo thi truong hang khong
     *
     * @param objectBO
     * @return
     */
    public static boolean isSearchByDomainData(String objectBO) {
        // TODO Auto-generated method stub
        ResourceBundle rb = ResourceBundle.getBundle("dataPicker");
        try {
            return "true".equalsIgnoreCase(rb.getString(String.format("%s.searchByDomainData", objectBO)));
        } catch (Exception e) {
            LOGGER.error("isSearchByDomainData:", e);
            return false;
        }
    }

    /**
     * ham kiem tra data picker co duoc cau hinh lay du lieu đa ngôn ngữ hay không
     *
     * @param objectBO
     * @return
     */
    public static boolean isMultilLanguage(String objectBO) {
        // TODO Auto-generated method stub
        ResourceBundle rb = ResourceBundle.getBundle("dataPicker");
        try {
            return "true".equalsIgnoreCase(rb.getString(String.format("%s.isMultilLanguage", objectBO)));
        } catch (Exception e) {
            LOGGER.error("isMultilLanguage:", e);
            return false;
        }
    }

    /**
     * getDataPickerConfig
     *
     * @param key
     * @return
     */
    public static String getDataPickerConfig(String key) {
        ResourceBundle rb = ResourceBundle.getBundle("dataPicker");
        return rb.getString(key);
    }


    //

    /**
     * Cau hinh vuot https.
     *
     * @return
     */
//    public static TrustManager[] get_trust_mgr() {
//        TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//
//            public void checkClientTrusted(X509Certificate[] certs, String t) {
//            }
//
//            public void checkServerTrusted(X509Certificate[] certs, String t) {
//            }
//        }};
//        return certs;
//    }

    /**
     * By pass link https.
     *
     * @param linkRSS
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     * @throws KeyStoreException
     */
//    public static void byPassHttps(URL linkRSS) throws NoSuchAlgorithmException,
//            KeyManagementException, IOException, KeyStoreException {
//        // Create a context that doesn't check certificates.
//        SSLContext ssl_ctx = SSLContext.getInstance("TLS");
//        TrustManager[] trust_mgr = get_trust_mgr();
//        ssl_ctx.init(null, // key manager
//                trust_mgr, // trust manager
//                new SecureRandom()); // random number generator
//        HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx
//                .getSocketFactory());
//
//        HttpsURLConnection con = (HttpsURLConnection) linkRSS.openConnection();
//
//        // Guard against "bad hostname" errors during handshake.
//        con.setHostnameVerifier(new HostnameVerifier() {
//            public boolean verify(String host, SSLSession sess) {
//                if ("localhost".equals(host)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
//    }

    public static Double getDoubleValue(Object value) {
        if (value != null && !value.toString().equals("")) {
            return Double.valueOf(value.toString());
        } else {
            return 0d;
        }
    }

    public static Long getLongValue(Object value) {
        if (value != null && !value.toString().equals("")) {
            return (Double.valueOf(value.toString())).longValue();
        } else {
            return 0L;
        }
    }

    public static BigDecimal converEtoDecimal(Double value) {
        return BigDecimal.valueOf(value);
    }

    /**
     * Chuyen doi tuong string ve doi tuong date theo dinh dang
     *
     * @param date
     * @param regex   mau du lieu
     * @param pattern mau dinh dang
     * @return
     */
    public static Date convertStringToDate(String date, String regex, String pattern) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        } else if (!date.matches(regex)) {
            return null;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            try {
                return sdf.parse(date);
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    /**
     * Chuyen xau thanh so nguyen. Kiem tra luon neu xau khong hop le se tra ve
     * null.
     *
     * @param s Xau
     * @return So
     */
    public static Double parseDouble(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                Double number = Double.parseDouble(s);
                return number;
            } catch (Exception ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Double parseDouble1(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                Double number = Double.parseDouble(s);
                return number;
            } catch (Exception ex) {
                return 0d;
            }
        } else {
            return 0d;
        }
    }

    public static Double parseDouble2(Object s) {
        if (s != null) {
            try {
                Double number = Double.parseDouble(s.toString());
                return number;
            } catch (Exception ex) {
                return 0d;
            }
        } else {
            return 0d;
        }
    }

    public static String parseString(Object s) {
        if (s != null) {
            return s.toString();
        } else {
            return "";
        }
    }

    public static Integer parseInteger(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                Integer number = Integer.parseInt(s);
                return number;
            } catch (Exception ex) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Convert mot so sang dang so La Ma.
     *
     * @param decimal So binh thuong
     * @return So La Ma
     */
    public static String convertDecimalToRoman(int decimal) {
        final String[] ROMAN_CODE = {"M", "CM", "D", "CD", "C", "XC", "L",
                "XL", "X", "IX", "V", "IV", "I"};
        final int[] DECIMAL_VALUE = {1000, 900, 500, 400, 100, 90, 50,
                40, 10, 9, 5, 4, 1};
        if (decimal <= 0 || decimal >= 4000) {
            throw new NumberFormatException("Value outside roman numeral range.");
        }
        StringBuilder roman = new StringBuilder();
        for (int i = 0; i < ROMAN_CODE.length; i++) {
            while (decimal >= DECIMAL_VALUE[i]) {
                decimal -= DECIMAL_VALUE[i];
                roman.append(ROMAN_CODE[i]);
            }
        }
        return roman.toString();
    }

    /**
     * Sắp xếp thời gian theo thứ tự tăng dần
     *
     * @param listOriginal
     */
    public static void sortListDateASC(List<Date> listOriginal) {
        Collections.sort(listOriginal, new Comparator<Date>() {
            @Override
            public int compare(Date arg0, Date arg1) {
                if (arg0.before(arg1)) {
                    return -1;
                } else {
                    if (arg0.equals(arg1)) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            }
        });
    }


    /**
     * convertNumberColumnToColumnLabel
     * chuyển cột số sang tên cột trong file import
     *
     * @param colData
     * @return
     */
    public static String convertNumberColumnToColumnLabel(int colData) {
        String columnLabel = "";
        if (colData < 26) {
            columnLabel = String.valueOf((char) ('A' + colData));
        } else {
            int temp = colData / 26;
            colData -= 26 * temp;
            String result = String.valueOf((char) ('A' + temp - 1));
            columnLabel = result + (char) ('A' + colData);
        }
        return columnLabel;
    }

    /**
     * Format tiền
     *
     * @param d So
     * @return Xau
     */
    public static String formatNumber2Currency(Double d) {
        if (d == null) {
            return "";
        } else {
            DecimalFormat format = new DecimalFormat("###,###.###");
            return format.format(d);
        }
    }

    /**
     * Format tiền
     *
     * @param d So
     * @return Xau
     */
    public static String formatNumber2Currency(Long d) {
        if (d == null) {
            return "";
        } else {
            DecimalFormat format = new DecimalFormat("###,###,###");
            return format.format(d);
        }
    }

    /**
     * filter
     *
     * @param strCondition
     * @param paramList
     * @param listId
     */
    public static void filter(List<Long> listId, StringBuilder strCondition, List<Object> paramList, String field) {
        boolean first = true;
        if (!CommonUtil.isNullOrEmpty(listId)) {
            strCondition.append(" AND ( ");
            for (Long id : listId) {
                if (!first) {
                    strCondition.append(" OR ");
                } else {
                    first = false;
                }
                strCondition.append(" CONCAT(','," + field + ",',') LIKE ? ");
                paramList.add("%," + id.toString() + ",%");
            }

            strCondition.append(" ) ");
        }
    }

    /**
     * Returns this host's non-loopback IPv4 addresses.
     *
     * @return
     * @throws SocketException
     */
    public static String getInet4Addresses() throws SocketException {
        List<Inet4Address> ret = new ArrayList<Inet4Address>();

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                    ret.add((Inet4Address) inetAddress);
                }
            }
        }

        return !ret.isEmpty()
                ? ret.get(0).getHostAddress()
                : null;
    }


    public static <T> Double getValueDoubleField(T o, String nameField) {
        try {
            Field field = o.getClass().getDeclaredField(nameField);
            field.setAccessible(true);
            Double value = (Double) field.get(o);
            return value;
        } catch (Exception e) {
            return 0d;
        }
    }

    public static void copyObject (Object from, Object to, List<String> fieldList){
        for (String fieldName : fieldList) {
            try {
                Field fieldFrom = from.getClass().getDeclaredField(fieldName);
                fieldFrom.setAccessible(true);

                Field fieldTo = from.getClass().getDeclaredField(fieldName);
                fieldTo.setAccessible(true);
                fieldTo.set(to, fieldFrom.get(from));
            } catch (Exception e) {
                LOGGER.error("copy obj error", e);
            }
        }

    }
}
