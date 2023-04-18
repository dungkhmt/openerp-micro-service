package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class Port {
	private String code;
	private String locationCode;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public Port(String code, String locationCode) {
		super();
		this.code = code;
		this.locationCode = locationCode;
	}
	public Port() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
