package localsearch.domainspecific.vehiclerouting.vrp.online.app;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Request;

public class DARPRequest extends Request{
	public Point pickup;
	public Point delivery;
	public RequestType type;
	public DARPRequest(int ID, int arrivalTime, Point pickup, Point delivery, RequestType type){
		super(ID,arrivalTime);
		this.pickup = pickup;
		this.delivery = delivery;
		this.type = type;
	}
}
