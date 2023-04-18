package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class Truck {
	private int id;
	private String code;
	private double weight;
	private int driverID;
	private String driverCode;
	private String driverName;
	private String depotTruckCode;
	private String depotTruckLocationCode;
	private String startWorkingTime;
	private String endWorkingTime;
	private String status;
	private String[] returnDepotCodes;// possible depots when finishing services
	private Intervals[] intervals;
	
	public Truck(int id, String code, double weight,
			int driverID, String driverCode, String driverName,
			String depotTruckCode, String depotTruckLocationCode, String startWorkingTime,
			String endWorkingTime, String status,
			String[] returnDepotCodes, Intervals[] intervals) {
		super();
		this.id = id;
		this.code = code;
		this.weight = weight;
		this.driverID = driverID;
		this.driverCode = driverCode;
		this.driverName = driverName;
		this.depotTruckCode = depotTruckCode;
		this.depotTruckLocationCode = depotTruckLocationCode;
		this.startWorkingTime = startWorkingTime;
		this.endWorkingTime = endWorkingTime;
		this.status = status;
		this.returnDepotCodes = returnDepotCodes;
		this.intervals = intervals;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String[] getReturnDepotCodes() {
		return returnDepotCodes;
	}
	public void setReturnDepotCodes(String[] returnDepotCodes) {
		this.returnDepotCodes = returnDepotCodes;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public int getDriverID(){
		return this.driverID;
	}
	public void setDriverID(int driverID){
		this.driverID = driverID;
	}
	public String getDriverCode() {
		return driverCode;
	}
	public void setDriverCode(String driverCode) {
		this.driverCode = driverCode;
	}
	
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public String getDepotTruckCode() {
		return depotTruckCode;
	}
	public void setDepotTruckCode(String depotTruckCode) {
		this.depotTruckCode = depotTruckCode;
	}
	public String getDepotTruckLocationCode() {
		return depotTruckLocationCode;
	}
	public void setDepotTruckLocationCode(String depotTruckLocationCode) {
		this.depotTruckLocationCode = depotTruckLocationCode;
	}
	public String getStartWorkingTime() {
		return startWorkingTime;
	}
	public void setStartWorkingTime(String startWorkingTime) {
		this.startWorkingTime = startWorkingTime;
	}
	public String getEndWorkingTime() {
		return endWorkingTime;
	}
	public void setEndWorkingTime(String endWorkingTime) {
		this.endWorkingTime = endWorkingTime;
	}
	public String getStatus(){
		return this.status;
	}
	public void setStatus(String status){
		this.status = status;
	}
	public Intervals[] getIntervals() {
		return intervals;
	}
	public void setIntervals(Intervals[] intervals) {
		this.intervals = intervals;
	}
	public Truck() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
