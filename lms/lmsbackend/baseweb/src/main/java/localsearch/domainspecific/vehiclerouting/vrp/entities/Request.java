package localsearch.domainspecific.vehiclerouting.vrp.entities;

public class Request {
	public int ID;
	public int arrivalTime;
	public Request(int ID, int arrivalTime){
		this.ID = ID;
		this.arrivalTime = arrivalTime;
	}
}
