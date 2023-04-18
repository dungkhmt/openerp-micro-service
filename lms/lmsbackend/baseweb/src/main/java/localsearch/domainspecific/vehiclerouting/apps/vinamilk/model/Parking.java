package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class Parking {
	private String endWorkingTime;
	private String locationId;
    private String startWorkingTime;
	public Parking(String endWorkingTime, String locationId, String startWorkingTime) {
		super();
		this.endWorkingTime = endWorkingTime;
		this.locationId = locationId;
		this.startWorkingTime = startWorkingTime;
	}
	public String getEndWorkingTime() {
		return endWorkingTime;
	}
	public void setEndWorkingTime(String endWorkingTime) {
		this.endWorkingTime = endWorkingTime;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getStartWorkingTime() {
		return startWorkingTime;
	}
	public void setStartWorkingTime(String startWorkingTime) {
		this.startWorkingTime = startWorkingTime;
	}
    
    
}
