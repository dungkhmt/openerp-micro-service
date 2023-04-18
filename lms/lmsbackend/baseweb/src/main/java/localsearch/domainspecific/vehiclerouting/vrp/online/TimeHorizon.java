package localsearch.domainspecific.vehiclerouting.vrp.online;

public class TimeHorizon {

	/**
	 * @param args
	 */
	public int start;
	public int end;
	public int endrequest;
	public int currentTimePoint;
	
	public TimeHorizon(int start, int endrequest, int end){
		this.start = start; this.end = end;
		this.endrequest = endrequest;
		currentTimePoint = start;
	}
	public boolean finished(){
		return currentTimePoint >= end;
	}
	public void reset(){
		currentTimePoint = start;
	}
	public boolean stopRequest(){
		//System.out.println("TimeHorizon::stopRequest, currentTimePoint = " + currentTimePoint + ", endrequest = " + endrequest);
		return currentTimePoint > endrequest;
	}
	public String currentTimePointHMS(){
		String hms = "";
		int h = currentTimePoint/3600;
		int r = currentTimePoint - 3600*h;
		int m = (r)/60;
		r = r - m*60;
		int s = r%60;
		
		return h + ":" + m + ":" + s;
	}
	public String timePointHMS(int t){
		String hms = "";
		int h = t/3600;
		int r = t - 3600*h;
		int m = (r)/60;
		r = r - m*60;
		int s = r%60;
		
		return h + ":" + m + ":" + s;
	}
	public void move(){
		currentTimePoint++;
	}
	public void move(int t){
		currentTimePoint += t;
	}
	public static int hms2Int(int hour, int minute, int second){
		return hour*3600 + minute*60 + second;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
