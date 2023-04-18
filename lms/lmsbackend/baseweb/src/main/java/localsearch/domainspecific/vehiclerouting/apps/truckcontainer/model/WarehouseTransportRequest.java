package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class WarehouseTransportRequest {
	private String orderID;
	private String orderCode;
	private WarehouseContainerTransportRequest[] warehouseContainerTransportRequests;
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
	public WarehouseContainerTransportRequest[] getWarehouseContainerTransportRequests() {
		return warehouseContainerTransportRequests;
	}
	public void setWarehouseContainerTransportRequests(
			WarehouseContainerTransportRequest[] warehouseContainerTransportRequests) {
		this.warehouseContainerTransportRequests = warehouseContainerTransportRequests;
	}
	public WarehouseTransportRequest(
			String orderID,
			String orderCode,
			WarehouseContainerTransportRequest[] warehouseContainerTransportRequests) {
		super();
		this.orderID = orderID;
		this.orderCode = orderCode;
		this.warehouseContainerTransportRequests = warehouseContainerTransportRequests;
	}
	public WarehouseTransportRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
}
