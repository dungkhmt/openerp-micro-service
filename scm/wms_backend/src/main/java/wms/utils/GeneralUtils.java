package wms.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GeneralUtils {
    public static String generateCodeFromSysTime() {
        return String.valueOf(System.currentTimeMillis()).substring(1, 9);
    }
    public static ZonedDateTime convertFromStringToDate(String dayMonthYear) {
        //https://stackoverflow.com/questions/23596530/unable-to-obtain-zoneddatetime-from-temporalaccessor-using-datetimeformatter-and
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(dayMonthYear, formatter);
        return date.atStartOfDay(ZoneId.systemDefault());
    }
}
