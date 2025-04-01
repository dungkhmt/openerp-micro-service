package openerp.openerpresourceserver.timeseriesanalysis.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    public static String std(int v, int L){
        String s = "" + v;
        while(s.length() < L) s = s + "0";
        return s;
    }
    public static int distance(String startDate, String endDate){
        int res = 0;
        String date = startDate;
        int cnt = 0;
        while(!date.equals(endDate)){
            cnt++; if(cnt > 10000) break;
            res++;
            date = nextDate(date);
        }
        return res;
    }
    public static String nextDate(String curDate){
        try {
            final SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd");
            final Date date = format.parse(curDate);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String nextDate = format.format(calendar.getTime());
            return nextDate;
            //for (int i = 1; i <= 60; i++) {
            //    calendar.add(Calendar.DAY_OF_YEAR, 1);
            //    String nextDate = format.format(calendar.getTime());
            //    System.out.println(i + ": new date = " + nextDate);
            //}
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
        /*
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

         */
    }
    public static void main(String[] args){
        try {
            String curDate = "2025:01:24";
            final SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd");
            final Date date = format.parse(curDate);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            for(int i = 1; i <= 60; i++) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                String nextDate = format.format(calendar.getTime());
                System.out.println(i + ": new date = " + nextDate);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
