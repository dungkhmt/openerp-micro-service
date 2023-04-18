package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

import localsearch.domainspecific.vehiclerouting.vrp.utils.*;

public class ImportContainerRequest {
	private String orderItemID;
	private String orderID;
	private String orderCode;
	private boolean isSwap;
	private String orderItemSwapID;
	// 1st segment (Port -> warehouse, Laden)
	private String shipCompanyCode;
	private String[] depotContainerCode;
	private String containerCategory;// 20, 40, 45
	private String containerCode;
	private String containerNo;
	private double weight;
	private String portCode;
	private String earlyDateTimePickupAtPort;
	private String lateDateTimePickupAtPort;
	private int loadDuration;
	private String customerCode;
	private String customerName;
	
	
	//private String wareHouseCode;
	//private String earlyDateTimeUnloadAtWarehouse;
	//private String lateDateTimeUnloadAtWarehouse;
	private int unloadDuration;
	//private int detachLoadedMoocContainerDuration;
	
	// 2nd segment (Warehouse -> depot, Empty)
	//private String earlyPickupEmptyContainerAtWarehouse;
	//private String latePickupEmptyContainerAtWarehouse;
	//private int attachEmptyMoocContainerDuration;
	private DeliveryWarehouseInfo[] deliveryWarehouses;
	
	
	private String earlyDateTimeDeliveryAtDepot;
	private String lateDateTimeDeliveryAtDepot;
	
	private int rejectCode;
	
	//private String levelRequest;// "1": only 1st requests, "2": only 2nd request, "12": both 1st and 2nd requests

	public ImportContainerRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ImportContainerRequest(String orderItemID, boolean isSwap, String orderItemSwapID, String shipCompanyCode,
			String[] depotContainerCode, String containerCategory,
			String containerCode, String containerNo, double weight, String portCode,
			String earlyDateTimePickupAtPort, String lateDateTimePickupAtPort,
			int loadDuration, DeliveryWarehouseInfo[] deliveryWarehouses,
			String earlyDateTimeDeliveryAtDepot,
			String lateDateTimeDeliveryAtDepot, String customerCode, String customerName) {
		super();
		this.orderItemID = orderItemID;
		this.isSwap = isSwap;
		this.orderItemSwapID = orderItemSwapID;
		this.shipCompanyCode = shipCompanyCode;
		this.depotContainerCode = depotContainerCode;
		this.containerCategory = containerCategory;
		this.containerCode = containerCode;
		this.containerNo = containerNo;
		this.weight = weight;
		this.portCode = portCode;
		this.earlyDateTimePickupAtPort = earlyDateTimePickupAtPort;
		this.lateDateTimePickupAtPort = lateDateTimePickupAtPort;
		this.loadDuration = loadDuration;
		this.deliveryWarehouses = deliveryWarehouses;
		this.earlyDateTimeDeliveryAtDepot = earlyDateTimeDeliveryAtDepot;
		this.lateDateTimeDeliveryAtDepot = lateDateTimeDeliveryAtDepot;
		this.customerCode = customerCode;
		this.customerName = customerName;
	}
	
	public String getLateDateTimeUnloadAtWarehouse(){
		String s = deliveryWarehouses[0].getLateDateTimeUnloadAtWarehouse();
		for(int i = 1; i < deliveryWarehouses.length; i++){
			if(DateTimeUtils.dateTime2Int(s) < DateTimeUtils.dateTime2Int(deliveryWarehouses[i].getLateDateTimeUnloadAtWarehouse()))
				s = deliveryWarehouses[i].getLateDateTimeUnloadAtWarehouse();
		}
		return s;
	}
	
	public String getEarlyDateTimeUnloadAtWarehouse(){
		String s = deliveryWarehouses[0].getEarlyDateTimeUnloadAtWarehouse();
		for(int i = 1; i < deliveryWarehouses.length; i++){
			if(DateTimeUtils.dateTime2Int(s) > DateTimeUtils.dateTime2Int(deliveryWarehouses[i].getEarlyDateTimeUnloadAtWarehouse()))
				s = deliveryWarehouses[i].getEarlyDateTimeUnloadAtWarehouse();
		}
		return s;
	}

	public String getOrderItemID() {
		return orderItemID;
	}

	public void setSwap(boolean isSwap) {
		this.isSwap = isSwap;
	}

	public void setOrderItemID(String orderItemID) {
		this.orderItemID = orderItemID;
	}
	
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

	public boolean getIsSwap(){
		return this.isSwap;
	}
	public void setIsSwap(boolean isSwap){
		this.isSwap = isSwap;
	}
	public String getOrderItemSwapID(){
		return this.orderItemSwapID;
	}
	public void setOrderItemSwapID(String orderItemSwapID){
		this.orderItemSwapID = orderItemSwapID;
	}
	public String getShipCompanyCode() {
		return shipCompanyCode;
	}

	public void setShipCompanyCode(String shipCompanyCode) {
		this.shipCompanyCode = shipCompanyCode;
	}

	public String[] getDepotContainerCode() {
		return depotContainerCode;
	}

	public void setDepotContainerCode(String[] depotContainerCode) {
		this.depotContainerCode = depotContainerCode;
	}

	public String getContainerCategory() {
		return containerCategory;
	}

	public void setContainerCategory(String containerCategory) {
		this.containerCategory = containerCategory;
	}

	public String getContainerCode() {
		return containerCode;
	}

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}

	public String getContainerNo() {
		return containerNo;
	}
	public void setContainerNo(String containerNo) {
		this.containerNo = containerNo;
	}
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getEarlyDateTimePickupAtPort() {
		return earlyDateTimePickupAtPort;
	}

	public void setEarlyDateTimePickupAtPort(String earlyDateTimePickupAtPort) {
		this.earlyDateTimePickupAtPort = earlyDateTimePickupAtPort;
	}

	public String getLateDateTimePickupAtPort() {
		return lateDateTimePickupAtPort;
	}

	public void setLateDateTimePickupAtPort(String lateDateTimePickupAtPort) {
		this.lateDateTimePickupAtPort = lateDateTimePickupAtPort;
	}

	public int getLoadDuration() {
		return loadDuration;
	}

	public void setLoadDuration(int loadDuration) {
		this.loadDuration = loadDuration;
	}

	public DeliveryWarehouseInfo[] getDeliveryWarehouses() {
		return deliveryWarehouses;
	}

	public void setDeliveryWarehouses(DeliveryWarehouseInfo[] deliveryWarehouses) {
		this.deliveryWarehouses = deliveryWarehouses;
	}

	public String getEarlyDateTimeDeliveryAtDepot() {
		return earlyDateTimeDeliveryAtDepot;
	}

	public void setEarlyDateTimeDeliveryAtDepot(String earlyDateTimeDeliveryAtDepot) {
		this.earlyDateTimeDeliveryAtDepot = earlyDateTimeDeliveryAtDepot;
	}

	public String getLateDateTimeDeliveryAtDepot() {
		return lateDateTimeDeliveryAtDepot;
	}

	public void setLateDateTimeDeliveryAtDepot(String lateDateTimeDeliveryAtDepot) {
		this.lateDateTimeDeliveryAtDepot = lateDateTimeDeliveryAtDepot;
	}
	
	public int getUnloadDuration() {
		return unloadDuration;
	}

	public void setUnloadDuration(int unloadDuration) {
		this.unloadDuration = unloadDuration;
	}
	
	public String getCustomerCode(){
		return this.customerCode;
	}
	
	public void setCustomerCode(String customerCode){
		this.customerCode = customerCode;
	}
	
	public String getCustomerName(){
		return this.customerName;
	}
	
	public void setCustomerName(String customerName){
		this.customerName = customerName;
	}

	public int getRejectCode() {
		return rejectCode;
	}

	public void setRejectCode(int rejectCode) {
		this.rejectCode = rejectCode;
	}
	
}
