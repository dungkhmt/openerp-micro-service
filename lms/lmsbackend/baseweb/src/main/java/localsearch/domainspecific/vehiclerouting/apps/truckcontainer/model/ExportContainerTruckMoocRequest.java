package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class ExportContainerTruckMoocRequest {
	private String orderID;
	private String orderCode;
	private ExportContainerRequest[] containerRequest;
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
	public ExportContainerRequest[] getContainerRequest() {
		return containerRequest;
	}
	public void setContainerRequest(ExportContainerRequest[] containerRequest) {
		this.containerRequest = containerRequest;
	}
	public ExportContainerTruckMoocRequest(String orderID, String orderCode,
			ExportContainerRequest[] containerRequest) {
		super();
		this.orderID = orderID;
		this.orderCode = orderCode;
		this.containerRequest = containerRequest;
	}
	public ExportContainerTruckMoocRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
