package localsearch.domainspecific.vehiclerouting.vrp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
	public static String extendDateTime(String dt, int s){// s seconds
		long d = dateTime2Int(dt);
		return unixTimeStamp2DateTime(d + s);
	}
	public static String date2YYYYMMDD(Date d){
		SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-DD");
		
		String strDate = df.format(d).toString();
		String s = d.getDate() + "";
		if(s.length() == 1) s = "0" + s;
		strDate = strDate.substring(0,8) + s;
		return strDate;
	}

	public static Date convertYYYYMMDD2Date(String dt){
		try{
			//System.out.println(name() + "::dateTime2Int, dt = " + dt);
			//String[] s = dt.split(":");
			//if(s.length < 3) dt += ":00";// add :ss to hh:mm --> hh:mm::ss
			dt += " 00:00:00";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = dateFormat.parse(dt);
			return date;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	// return the distance dt1 - dt2 (in seconds)
	public static long distance(String dt1, String dt2){
		return dateTime2Int(dt1) - dateTime2Int(dt2);
	}
	public static String meanDatetime(String dt1, String dt2){
		long u1 = dateTime2Int(dt1);
		long u2 = dateTime2Int(dt2);
		long u = (u1+u2)/2;
		return unixTimeStamp2DateTime(u);
	}
	// in seconds
	public static long dateTime2Int(String dt){
		// convert datetime to int (seconds), datetime example is 2016-10-04 10:30:15
		/*
		String[] s = dt.split(" ");
		String[] d = s[0].split("-");
		int year = Integer.valueOf(d[0]);
		int month = Integer.valueOf(d[1]);
		int day = Integer.valueOf(d[2]);
		String[] t = s[1].split(":");
		int hour = Integer.valueOf(t[0]);
		int minute = Integer.valueOf(t[1]);
		int second = Integer.valueOf(t[2]);
		*/
		try{
			//System.out.println(name() + "::dateTime2Int, dt = " + dt);
			String[] s = dt.split(":");
			if(s.length < 3) dt += ":00";// add :ss to hh:mm --> hh:mm::ss
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = dateFormat.parse(dt);
			return date.getTime()/1000;// in seconds
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return 0;
	}
	public static String unixTimeStamp2DateTime(long dt){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//dt is measured in seconds and must be converted into milliseconds
		return dateFormat.format(dt*1000);
	}
	
	public static String second2HMS(int hms){
		//String s = "";
		int h = hms/3600;
		int m = (hms - h*3600)/60;
		int s = hms - h*3600 - m*60;
		return h + ":" + m + ":" + s;
	}
	public static long computeStartTimePoint(long early1, long late1, long traveltime, long early2, long late2){
		//[early1,late1] is the time windows of start point
		//[early2,late2] is the time windows of end point
		// travel time from start point to end point
		if(early1 + traveltime >= early2) return early1;
		return early2 - traveltime;
	}
	
	public static int getHour(String dt){
		// date time dt is of format yyyy-mm-dd hh:mm:ss
		// return hour
		String[] s = dt.split(" ");
		String[] s1 = s[1].split(":");
		return Integer.valueOf(s1[0].trim());
	}
	public static int getMinute(String dt){
		// date time dt is of format yyyy-mm-dd hh:mm:ss
		// return hour
		String[] s = dt.split(" ");
		String[] s1 = s[1].split(":");
		return Integer.valueOf(s1[1].trim());
	}
	public static String name(){
		return "DateTimeUtils";
	}
	public static boolean isHighTraffic(String dt){
		int hour = getHour(dt);
		int minute = getMinute(dt);
		int hm = hour*60 + minute;
		//System.out.println(name() + "::isHighTraffic, hm = " + hm);
		return (8*60 <= hm && hm <= 9*60 || 16*60 <= hm && hm <= 18*60);
	}
	
	public static String currentDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
		Date date = new Date();
		
		//System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
		String DT = dateFormat.format(date);
		System.out.println(DT);
		String[] s = DT.split(":");
		return s[0] + s[1] + s[2];
	}
	public static void main(String[] args){
		String DT1 = "2016-10-04 10:30:15";
		String DT2 = "2016-10-04 10:31:10";
		
		long t1 = (long)DateTimeUtils.dateTime2Int(DT1);
		long t2 = (long)DateTimeUtils.dateTime2Int(DT2);
		long t = t1 - t2;
		String dt1 = DateTimeUtils.unixTimeStamp2DateTime(t1);
		
		System.out.println("t1 = " + t1 + ", t2 = " + t2 + ", t = " + t + ", dt1 = " + dt1);
		String dt = "2016-03-10 10:30:03";
		System.out.println("hour of " + dt + " is " + DateTimeUtils.getHour(dt));
		
		System.out.println(meanDatetime(DT1, DT2));
		
		System.out.println(DateTimeUtils.unixTimeStamp2DateTime(Integer.MAX_VALUE));
		
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
		Date date = new Date();
		
		//System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
		String DT = dateFormat.format(date);
		System.out.println(DT);
		
		System.out.println(DateTimeUtils.currentDate());
		
		System.out.println(DateTimeUtils.extendDateTime(dt, 1800));
	}
}
