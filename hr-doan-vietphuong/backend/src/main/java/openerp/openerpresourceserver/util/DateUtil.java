package openerp.openerpresourceserver.util;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DateUtil {
    public final int GRACE_PERIOD_IN_MINUTES = 0;
    public final int BREAK_TIME_IN_MINUTES = 90;
    public final LocalTime START_LUNCH = LocalTime.of(12, 0);
    public final LocalTime END_LUNCH = LocalTime.of(13, 30);
    public final LocalTime START_TIME = LocalTime.of(9, 0);
    public final LocalTime END_TIME = LocalTime.of(18, 30);

    public Integer convertLocalDateToInteger(LocalDate date) {
        if (date == null) return null;
        return Integer.parseInt(date.toString().replace("-", ""));
    }

    public LocalDate convertIntegerToLocalDate(Integer date) {
        if (date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(date.toString(), formatter);
    }

    public boolean isWeekend(Integer date) {
        LocalDate checkDate = convertIntegerToLocalDate(date);
        DayOfWeek dayOfWeek = checkDate.getDayOfWeek();
        return dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY);
    }

    public boolean isBeforeOrEqual(LocalTime time, LocalTime startTime) {
        return time.isBefore(startTime) || time.equals(startTime);
    }

    public boolean isAfterOrEqual(LocalTime time, LocalTime endTime) {
        return time.isAfter(endTime) || time.equals(endTime);
    }

    public LocalTime getLocalTime(LocalDateTime date) {
        return date == null ? null : date.toLocalTime();
    }

    public int getCurrentMonthValue() {
        return LocalDate.now().getMonthValue();
    }

    public int getCurrentYearValue() {
        return LocalDate.now().getYear();
    }

    public LocalDateTime getStartOfDayLocalDateTime(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.of(0, 0, 0));
    }

    public LocalDateTime getEndOfDayLocalDateTime(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.of(23, 59, 59));
    }

    public double getDifferenceInHours(LocalTime start, LocalTime end) {
        Duration duration = Duration.between(start, end);
        long minutes = duration.toMinutes();
        return minutes / 60.0;
    }

    public Duration convertDoubleHoursToDuration(double hours) {
        long wholeHours = (long) hours;
        long minutes = Math.round((hours - wholeHours) * 60);
        return Duration.ofHours(wholeHours).plusMinutes(minutes);
    }

    public List<Integer> getWeekendInDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> weekends = new ArrayList<>();

        // Loop through the date range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Check if the day is Saturday or Sunday
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                weekends.add(date);
            }
        }

        return weekends.stream()
                .map(DateUtil::convertLocalDateToInteger)
                .collect(Collectors.toList());
    }

    public static List<Integer> getWeekdayInDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> weekdays = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                weekdays.add(date);
            }
        }

        return weekdays.stream()
                .map(DateUtil::convertLocalDateToInteger)
                .collect(Collectors.toList());
    }

    public long countWeekendsBetween(LocalDate startDate, LocalDate endDate) {
        // Ensure the start date is before the end date
        long weekendCount = 0;

        // Loop from startDate to endDate
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                weekendCount++;
            }
            date = date.plusDays(1); // Move to the next day
        }

        return weekendCount;
    }

    public static List<Integer> getDatesInDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> weekdays = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            weekdays.add(date);
        }
        return weekdays.stream()
                .map(DateUtil::convertLocalDateToInteger)
                .collect(Collectors.toList());
    }

    public static String getMonthByDate(LocalDate date) {
        return date.getMonthValue() < 10 ? "0" + date.getMonthValue() : String.valueOf(date.getMonthValue());
    }

    public static String getNextMonthByDate(LocalDate date) {
        return String.valueOf(date.getMonth().plus(1).getValue());
    }

    public static List<Integer> getHolidayInDateRange(
            List<Integer> holidays,
            LocalDate startDate,
            LocalDate endDate) {
        return holidays.stream()
                .filter(date -> {
                    LocalDate holidayDate = convertIntegerToLocalDate(date);
                    // Check if the holiday date is within the range
                    return !holidayDate.isBefore(startDate) && !holidayDate.isAfter(endDate);
                })
                .toList();
    }

    public static Double convertMinuteToHour(int bonusTime) {
        return bonusTime / 60.0;
    }

    public LocalDateTime getMinTime(LocalDateTime newTime, LocalDateTime currentTime) {
        if (currentTime == null) return newTime;
        return newTime.isBefore(currentTime) ? newTime : currentTime;
    }

    public LocalDateTime getMaxTime(LocalDateTime newTime, LocalDateTime currentTime) {
        if (currentTime == null) return newTime;
        return newTime.isBefore(currentTime) ? currentTime : newTime;
    }

}
