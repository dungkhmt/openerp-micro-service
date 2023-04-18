package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class StatisticInformation {
	private int totalRequests;
	private int totalRejectedRequests;
	private double totalDistance;
	private int numberTrucks;
	
	public StatisticInformation(int totalRequests, int totalRejectedRequests, double totalDistance, int numberTrucks){
		super();
		this.totalRequests = totalRequests;
		this.totalRejectedRequests = totalRejectedRequests;
		this.totalDistance = totalDistance;
		this.numberTrucks = numberTrucks;
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
	public int getNumberTrucks() {
		return numberTrucks;
	}
	public void setNumberTrucks(int numberTrucks) {
		this.numberTrucks = numberTrucks;
	}
	
	
}
