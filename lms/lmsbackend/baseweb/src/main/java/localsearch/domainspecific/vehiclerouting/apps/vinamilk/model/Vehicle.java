package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class Vehicle {
	private String vehicleCode;
	private String locaionId;
	private double lowerLoadRate;
	private double upperLoadRate;
	private int ownership;
	private double weight;
	private int nbTrips;
	private String[] restrictedProducts;
	public Vehicle(String vehicleCode, String locaionId, double lowerLoadRate, double upperLoadRate, int ownership,
			double weight, int nbTrips, String[] restrictedProducts) {
		super();
		this.vehicleCode = vehicleCode;
		this.locaionId = locaionId;
		this.lowerLoadRate = lowerLoadRate;
		this.upperLoadRate = upperLoadRate;
		this.ownership = ownership;
		this.weight = weight;
		this.nbTrips = nbTrips;
		this.restrictedProducts = restrictedProducts;
	}
	public String getVehicleCode() {
		return vehicleCode;
	}
	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}
	public String getLocaionId() {
		return locaionId;
	}
	public void setLocaionId(String locaionId) {
		this.locaionId = locaionId;
	}
	public double getLowerLoadRate() {
		return lowerLoadRate;
	}
	public void setLowerLoadRate(double lowerLoadRate) {
		this.lowerLoadRate = lowerLoadRate;
	}
	public double getUpperLoadRate() {
		return upperLoadRate;
	}
	public void setUpperLoadRate(double upperLoadRate) {
		this.upperLoadRate = upperLoadRate;
	}
	public int getOwnership() {
		return ownership;
	}
	public void setOwnership(int ownership) {
		this.ownership = ownership;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public int getNbTrips() {
		return nbTrips;
	}
	public void setNbTrips(int nbTrips) {
		this.nbTrips = nbTrips;
	}
	public String[] getRestrictedProducts() {
		return restrictedProducts;
	}
	public void setRestrictedProducts(String[] restrictedProducts) {
		this.restrictedProducts = restrictedProducts;
	}
	
	
}
