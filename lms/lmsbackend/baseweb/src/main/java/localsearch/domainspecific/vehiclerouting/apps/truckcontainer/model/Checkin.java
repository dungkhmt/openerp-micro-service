package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class Checkin {
	private int driverID;
	private int count;
	
	public Checkin(int driverID, int count){
		super();
		this.driverID = driverID;
		this.count = count;
	}
	
	public int getDriverID(){
		return this.driverID;
	}
	public void setDriverID(int driverID){
		this.driverID = driverID;
	}

	public int getCount(){
		return this.count;
	}
	public void setCount(int count){
		this.count = count;
	}
	public Checkin(){
		super();
	}
}
