package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class DistributionCenter {
	private String endWorkingTime;
	private double loadDurationPerUnit;
	private String locationId;
    private String startWorkingTime;
    private double waittingDuration;
	public DistributionCenter(String endWorkingTime, double loadDurationPerUnit, String locationId,
			String startWorkingTime, double waittingDuration) {
		super();
		this.endWorkingTime = endWorkingTime;
		this.loadDurationPerUnit = loadDurationPerUnit;
		this.locationId = locationId;
		this.startWorkingTime = startWorkingTime;
		this.waittingDuration = waittingDuration;
	}
	public String getEndWorkingTime() {
		return endWorkingTime;
	}
	public void setEndWorkingTime(String endWorkingTime) {
		this.endWorkingTime = endWorkingTime;
	}
	public double getLoadDurationPerUnit() {
		return loadDurationPerUnit;
	}
	public void setLoadDurationPerUnit(double loadDurationPerUnit) {
		this.loadDurationPerUnit = loadDurationPerUnit;
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
	public double getWaittingDuration() {
		return waittingDuration;
	}
	public void setWaittingDuration(double waittingDuration) {
		this.waittingDuration = waittingDuration;
	}
    
    
}
