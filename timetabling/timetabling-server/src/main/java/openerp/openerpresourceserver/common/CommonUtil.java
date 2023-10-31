package openerp.openerpresourceserver.common;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CommonUtil {

    public static final Integer TIME_FOR_A_CREW = 45;

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
        Integer hourStart = Integer.parseInt(start.substring(0, 1));
        Integer minuteStart = Integer.parseInt(start.substring(2, 3));
        Integer hourFinish = Integer.parseInt(finish.substring(0, 1));
        Integer minuteFinish = Integer.parseInt(finish.substring(2, 3));
        if (minuteFinish - minuteStart < 0) {
            timeStudy = (hourFinish - hourStart - 1) * 60 + (minuteFinish + 60 - minuteStart);
        } else {
            timeStudy = (hourFinish - hourStart) * 60 + (minuteFinish - minuteStart);
        }
        return roundUpDivision(timeStudy, TIME_FOR_A_CREW);
    }
}
