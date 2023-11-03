package openerp.openerpresourceserver.common;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CommonUtil {

    public static final Integer TIME_FOR_A_CREW = 50;

    public static final Integer MINUTE_PER_HOUR = 60;

    public static Integer roundUpDivision(Integer dividend, Integer divisor) {
        BigDecimal result = new BigDecimal(dividend).divide(new BigDecimal(divisor), 0, RoundingMode.CEILING);
        return result.intValue();
    }

    public static Integer calculateTimeCrew(String start, String finish) {
        int timeStudy = 0;
        if (start.length() < 2 && finish.length() < 2) {
            Integer begin = Integer.parseInt(start);
            Integer end = Integer.parseInt(finish);
            return (end - begin) + 1;
        }
        Integer hourStart = Integer.parseInt(start.substring(0, 2));
        Integer minuteStart = Integer.parseInt(start.substring(2, 4));
        Integer hourFinish = Integer.parseInt(finish.substring(0, 2));
        Integer minuteFinish = Integer.parseInt(finish.substring(2, 4));

        int tmpMinute = minuteFinish - minuteStart;
        int tmpHour = hourFinish - hourStart;
        if (tmpMinute < 0) {
            timeStudy = ( tmpHour - 1) * MINUTE_PER_HOUR + (tmpMinute + MINUTE_PER_HOUR);
        } else {
            timeStudy = tmpHour * MINUTE_PER_HOUR + tmpMinute;
        }
        return roundUpDivision(timeStudy, TIME_FOR_A_CREW);
    }
}
