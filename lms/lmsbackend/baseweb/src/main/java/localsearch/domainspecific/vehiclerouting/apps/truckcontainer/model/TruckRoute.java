package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class TruckRoute {
    private int index;
	private Truck truck;
	private int nbStops;
	private int travelTime;
	private RouteElement[] nodes;

	public TruckRoute(int index, Truck truck, int nbStops,
			int travelTime,
			RouteElement[] nodes){
		super();
		this.index = index;
		this.truck = truck;
		this.nbStops = nbStops;
		this.travelTime = travelTime;
		this.nodes = nodes;
	}
	public TruckRoute() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getIndex(){return index;}
    public void setIndex(int index){ this.index = index;}
	public Truck getTruck() {
		return truck;
	}
	public void setTruck(Truck truck) {
		this.truck = truck;
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
