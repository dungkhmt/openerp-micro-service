package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class ImportContainerTruckMoocRequest {
	private String orderID;
	private String orderCode;
	private ImportContainerRequest[] containerRequest;
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public ImportContainerRequest[] getContainerRequest() {
		return containerRequest;
	}
	public void setContainerRequest(ImportContainerRequest[] containerRequest) {
		this.containerRequest = containerRequest;
	}
	public ImportContainerTruckMoocRequest(String orderID, String orderCode,
			ImportContainerRequest[] containerRequest) {
		super();
		this.orderID = orderID;
		this.orderCode = orderCode;
		this.containerRequest = containerRequest;
	}
	public ImportContainerTruckMoocRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
