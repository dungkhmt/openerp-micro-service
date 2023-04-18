package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class Warehouse {
	private String code;
	private String locationCode;
	private int hardConstraintType;
	private int vehicleConstraintType;
	private int[] drivers;
	private int[] vehicles;
	private Checkin[] checkin;
	private Intervals[] breaktimes;	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public int getHardConstraintType() {
		return hardConstraintType;
	}
	public void setHardConstraintType(int hardConstraintType) {
		this.hardConstraintType = hardConstraintType;
	}
	public int getVehicleConstraintType() {
		return vehicleConstraintType;
	}
	public void setVehicleConstraintType(int vehicleConstraintType) {
		this.vehicleConstraintType = vehicleConstraintType;
	}
	public int[] getDrivers() {
		return drivers;
	}
	public void setDrivers(int[] drivers) {
		this.drivers = drivers;
	}
	public int[] getVehicles() {
		return vehicles;
	}
	public void setVehicles(int[] vehicles) {
		this.vehicles = vehicles;
	}
	public Checkin[] getCheckin(){
		return checkin;
	}
	public void setCheckin(Checkin[] checkin){
		this.checkin = checkin;
	}
	public Intervals[] getBreaktimes(){
		return this.breaktimes;
	}
	public void setBreaktimes(Intervals[] breaktimes){
		this.breaktimes = breaktimes;
	}
	public Warehouse(String code, String locationCode,
			int hardConstraintType,
			int vehicleConstraintType,
			int[] drivers,
			int[] vehicles,
			Checkin[] checkin,
			Intervals[] breaktimes) {
		super();
		this.code = code;
		this.locationCode = locationCode;
		this.hardConstraintType = hardConstraintType;
		this.vehicleConstraintType = vehicleConstraintType;
		this.drivers = drivers;
		this.vehicles = vehicles;
		this.checkin = checkin;
		this.breaktimes = breaktimes;
	}
	public Warehouse() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
