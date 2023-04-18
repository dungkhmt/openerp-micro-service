package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class RouteElement {

	private String locationCode;
	private String action;
	
	private String arrivalTime;
	private String departureTime;
	private int travelTime;
	
	
	public RouteElement(String locationCode, String action,
			String arrivalTime, String departureTime, int travelTime){
		super();
		this.locationCode = locationCode;
		this.action = action;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.travelTime = travelTime;
	}
	public RouteElement() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getLocationCode(){
		return this.locationCode;
	}
	public void setLocationCode(String locationCode){
		this.locationCode = locationCode;
	}
	public String getAction(){
		return this.action;
	}
	public void setAction(String action){
		this.action = action;
	}
	
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	public int getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}
}

