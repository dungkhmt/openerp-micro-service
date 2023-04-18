package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class Customer {
	private String customerCode;
	private String endWorkingTime;
	private String locationId;
	private String startWorkingTime;
	private double unloadDurationPerUnit;
	private double waittingDuration;
	private double limitedWeight;
	public Customer(String customerCode, String endWorkingTime, String locationId, String startWorkingTime,
			double unloadDurationPerUnit, double waittingDuration, double limitedWeight) {
		super();
		this.customerCode = customerCode;
		this.endWorkingTime = endWorkingTime;
		this.locationId = locationId;
		this.startWorkingTime = startWorkingTime;
		this.unloadDurationPerUnit = unloadDurationPerUnit;
		this.waittingDuration = waittingDuration;
		this.limitedWeight = limitedWeight;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
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
	public double getUnloadDurationPerUnit() {
		return unloadDurationPerUnit;
	}
	public void setUnloadDurationPerUnit(double unloadDurationPerUnit) {
		this.unloadDurationPerUnit = unloadDurationPerUnit;
	}
	public double getWaittingDuration() {
		return waittingDuration;
	}
	public void setWaitingDuration(double waittingDuration) {
		this.waittingDuration = waittingDuration;
	}
	public double getLimitedWeight() {
		return limitedWeight;
	}
	public void setLimitedWeight(double limitedWeight) {
		this.limitedWeight = limitedWeight;
	}
	
	
	
}
