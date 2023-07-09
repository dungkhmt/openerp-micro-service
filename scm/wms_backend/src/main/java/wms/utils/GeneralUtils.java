package wms.utils;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GeneralUtils {
    public static String generateCodeFromSysTime() {
        return String.valueOf(System.currentTimeMillis()).substring(1, 10);
    }
    public static ZonedDateTime convertFromStringToDate(String dayMonthYear) {
        //https://stackoverflow.com/questions/23596530/unable-to-obtain-zoneddatetime-from-temporalaccessor-using-datetimeformatter-and
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(dayMonthYear, formatter);
        return date.atStartOfDay(ZoneId.systemDefault());
    }
    public static String convertVietnameseCurrency(Double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        // Format the amount as Vietnamese currency
        return currencyFormatter.format(amount);
    }
}
