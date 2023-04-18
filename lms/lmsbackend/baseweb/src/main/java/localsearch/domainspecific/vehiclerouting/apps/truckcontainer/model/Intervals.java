package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class Intervals {
	private String dateStart;
	private String dateEnd;
	
	public Intervals(String dateStart, String dateEnd){
		super();
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
	}
	
	public String getDateStart(){
		return this.dateStart;
	}
	public void setDateStart(String dateStart){
		this.dateStart = dateStart;
	}

	public String getDateEnd(){
		return this.dateEnd;
	}
	public void setDateEnd(String dateEnd){
		this.dateEnd = dateEnd;
	}
	public Intervals(){
		super();
	}
}
