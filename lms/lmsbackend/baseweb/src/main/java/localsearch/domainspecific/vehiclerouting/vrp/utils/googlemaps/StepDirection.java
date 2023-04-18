package localsearch.domainspecific.vehiclerouting.vrp.utils.googlemaps;

public class StepDirection {
    private String startLat;
    private String startLng;
    private String endlat;
    private String endLng;
    private int duaration;  //second
    private float distance; // met
    private String mode;    //DRIVING, WALK, BYCLE
    private String html_instruction; //Head <b>east</b> on <b>Hoàng Sa</b> toward <b>Rạch Bùng Binh</b>
    
    public StepDirection() {
	}
	public StepDirection(String startLat, String startLng, String endlat,
			String endLng, int duaration, float distance,String mode,String  html_instruction) {
		this.startLat = startLat;
		this.startLng = startLng;
		this.endlat = endlat;
		this.endLng = endLng;
		this.duaration = duaration; // second
		this.distance = distance;   //met
		this.mode = mode;
		this.html_instruction = html_instruction;
		
	}
	public String getStartLat() {
		return startLat;
	}
	public void setStartLat(String startLat) {
		this.startLat = startLat;
	}
	public String getStartLng() {
		return startLng;
	}
	public void setStartLng(String startLng) {
		this.startLng = startLng;
	}
	public String getEndlat() {
		return endlat;
	}
	public void setEndlat(String endlat) {
		this.endlat = endlat;
	}
	public String getEndLng() {
		return endLng;
	}
	public void setEndLng(String endLng) {
		this.endLng = endLng;
	}
	public int getDuaration() {
		return duaration;
	}
	public void setDuaration(int duaration) {
		this.duaration = duaration;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getHtml_instruction() {
		return html_instruction;
	}
	public void setHtml_instruction(String html_instruction) {
		this.html_instruction = html_instruction;
	}    
}
