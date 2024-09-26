

package wms.common.constant;

import java.time.format.DateTimeFormatter;

public class DateTimeConst {
    private DateTimeConst() {
    }
    public static final Integer ONE_HOUR = 60 * 60 * 1000;
    public static final Integer ONE_DAY = 24 * ONE_HOUR;
    public static long ONE_HOUR_IN_MINUTE = (long) 60;
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_SQl = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DOT = "yyyy.MM.dd";
    public static final String DATE_FORMAT_DTO = "dd/MM/yyyy";
    public static final String DATE_FORMAT_DTO_2 = "MM/dd/yyyy";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateTimeConst.DATE_TIME_FORMAT);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DateTimeConst.DATE_FORMAT);
    public static final DateTimeFormatter DATE_FORMATTER_DOT = DateTimeFormatter.ofPattern(DateTimeConst.DATE_FORMAT_DTO);
    public static final DateTimeFormatter DATE_FORMATTER_DOT_2 = DateTimeFormatter.ofPattern(DateTimeConst.DATE_FORMAT_DTO_2);
}