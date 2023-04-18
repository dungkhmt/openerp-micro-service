package localsearch.domainspecific.vehiclerouting.apps.vinamilk.service;

public class StatisticInformation {
	private int totalRequests;
	private int totalRejectedRequests;
	private double totalDistance;
	private int numberVehicles;
	
	public StatisticInformation(int totalRequests, int totalRejectedRequests, 
			double totalDistance, int numberVehicles){
		super();
		this.totalRequests = totalRequests;
		this.totalRejectedRequests = totalRejectedRequests;
		this.totalDistance = totalDistance;
		this.numberVehicles = numberVehicles;
	}
	
	
	public StatisticInformation(){
		super();
		
	}
	
	public int getTotalRequests() {
		return totalRequests;
	}
	public void setTotalRequests(int totalRequests) {
		this.totalRequests = totalRequests;
	}
	public int getTotalRejectedRequests() {
		return totalRejectedRequests;
	}


	public void setTotalRejectedRequests(int totalRejectedRequests) {
		this.totalRejectedRequests = totalRejectedRequests;
	}


	public double getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(double totalDistance) {
		this.totalDistance = totalDistance;
	}
	public int getNumberVehicles() {
		return numberVehicles;
	}
	public void setNumberVehicles(int numberVehicles) {
		this.numberVehicles = numberVehicles;
	}
}
