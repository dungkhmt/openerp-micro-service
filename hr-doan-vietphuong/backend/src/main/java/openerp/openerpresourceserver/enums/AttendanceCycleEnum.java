package openerp.openerpresourceserver.enums;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import openerp.openerpresourceserver.dto.response.attendance.AttendanceCycleResponse;
import openerp.openerpresourceserver.exception.InvalidRequestException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public enum AttendanceCycleEnum {
    MONTHLY {
        @Override
        public String createJsonSettingValue(Integer endDay) throws JsonProcessingException {
            Map<String, String> map = new HashMap<>();
            map.put(END_DAY, String.valueOf(endDay));
            map.put(TYPE, this.name());
            return objectMapper.writeValueAsString(map);
        }

        @Override
        public AttendanceCycleResponse getAttendanceDateRange(String endDay, int month, int year) {
            // Use YearMonth to get the start and end of the month
            YearMonth yearMonth = YearMonth.of(year, month);
            // Get the first and last day of the month
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            return new AttendanceCycleResponse(startDate, endDate, this);
        }
    },
    REPEATING {
        @Override
        public String createJsonSettingValue(Integer endDay) throws JsonProcessingException {
            try {
                if (endDay == null) {
                    throw new InvalidRequestException("end_day must not be null");
                }
                if (endDay < 1 || endDay > 31) {
                    throw new InvalidRequestException("end_day must be between 1 and 31");
                }
                Map<String, String> map = createBaseJsonMap(endDay, this.name());
                return objectMapper.writeValueAsString(map);
            } catch (DateTimeParseException e) {
                throw new InvalidRequestException("Invalid date format");
            }
        }

        @Override
        public AttendanceCycleResponse getAttendanceDateRange(String endDay, int month, int year) {
            int end = Integer.parseInt(endDay);
            if (end < LocalDate.now().getDayOfMonth()) {
                month += 1;
            }
            LocalDate endDate = LocalDate.of(year, month, end);
            LocalDate startDate = endDate
                    .minusMonths(1)
                    .plusDays(1);
            return new AttendanceCycleResponse(startDate, endDate, this);
        }
    };
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String END_DAY = "end_day";
    private static final String TYPE = "type";

    private static Map<String, String> createBaseJsonMap(int endDate, String type) {
        Map<String, String> map = new HashMap<>();
        map.put(END_DAY, String.valueOf(endDate));
        map.put(TYPE, type);
        return map;
    }

    public abstract String createJsonSettingValue(Integer endDay) throws JsonProcessingException;

    public abstract AttendanceCycleResponse getAttendanceDateRange(String endDay, int month, int year);
}
