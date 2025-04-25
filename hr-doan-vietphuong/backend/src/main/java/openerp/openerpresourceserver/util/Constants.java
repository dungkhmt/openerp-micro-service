package openerp.openerpresourceserver.util;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final String TOKEN_PREFIX = "TOKEN_";
    public static final String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN_";
    public static final String ATTENDANCE_FORGOT = "QCC";
    public static final String HOLIDAY = "HOLIDAY";
    public static final String ABSENCE = "NP";
    public static final String NO_ABSENCE = "NKP";
    public static final String MCC = "MCC";
    public static final String PART_TIME = "Part-time";
    public static final String REPORT_TEMPLATE = "report_template.xlsx";
}

