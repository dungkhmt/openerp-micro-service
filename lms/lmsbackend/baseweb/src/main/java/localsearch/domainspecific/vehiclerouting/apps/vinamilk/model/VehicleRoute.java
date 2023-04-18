package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class VehicleRoute {
	private Vehicle vehicle;
	private int nbStops;
	private int travelTime;
	private RouteElement[] nodes;

	public VehicleRoute(Vehicle vehicle, int nbStops,
			int travelTime,
			RouteElement[] nodes){
		super();
		this.vehicle = vehicle;
		this.nbStops = nbStops;
		this.travelTime = travelTime;
		this.nodes = nodes;
	}
	public VehicleRoute() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	public int getNbStops() {
		return nbStops;
	}
	public void setNbStops(int nbStops) {
		this.nbStops = nbStops;
	}
	public int getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}
	public RouteElement[] getNodes() {
		return nodes;
	}
	public void setNodes(RouteElement[] nodes) {
		this.nodes = nodes;
	}
}
