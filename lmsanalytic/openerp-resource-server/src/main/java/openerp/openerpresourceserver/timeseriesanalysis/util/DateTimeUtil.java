package openerp.openerpresourceserver.timeseriesanalysis.util;

public class DateTimeUtil {
    public static String std(int v, int L){
        String s = "" + v;
        while(s.length() < L) s = s + "0";
        return s;
    }
    public static String nextDate(String date){
        String[] s = date.split("-");
        int year = Integer.valueOf(s[0]);
        int month = Integer.valueOf(s[1]);
        int day = Integer.valueOf(s[2]);
        String res = "";
        day++;
        if(day > 30){
            day = 1; month++;
            if(month > 12){
                year++; month = 1;
            }
        }
        return year + "-" + std(month,2) + "-" + std(day,2);
    }
}
