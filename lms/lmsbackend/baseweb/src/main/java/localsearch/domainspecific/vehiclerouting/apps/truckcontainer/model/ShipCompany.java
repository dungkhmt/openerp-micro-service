package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class ShipCompany {
	private String code;
	private String[] containerDepotCodes;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String[] getContainerDepotCodes() {
		return containerDepotCodes;
	}
	public void setContainerDepotCodes(String[] containerDepotCodes) {
		this.containerDepotCodes = containerDepotCodes;
	}
	public ShipCompany(String code, String[] containerDepotCodes) {
		super();
		this.code = code;
		this.containerDepotCodes = containerDepotCodes;
	}
	public ShipCompany() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
