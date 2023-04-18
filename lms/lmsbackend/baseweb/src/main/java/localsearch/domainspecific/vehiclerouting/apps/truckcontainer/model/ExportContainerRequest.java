package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

import localsearch.domainspecific.vehiclerouting.vrp.utils.*;

public class ExportContainerRequest {
	private String orderItemID;
	private String orderID;
	private String orderCode;
	private boolean isSwap;
	private String orderItemSwapID;
	private String shipCompanyCode;
	private String depotContainerCode;// depotContainer
	private String containerCategory;// 20, 40, 45
	private String containerCode;
	private String containerNo;
	private double weight;
	private String earlyDateTimePickupAtDepot;
	private String lateDateTimePickupAtDepot;
	
	//private String wareHouseCode;
	//private String earlyDateTimeLoadAtWarehouse;
	//private String lateDateTimeLoadAtWarehouse;
	private int loadDuration;
	//private int detachEmptyMoocContainerDuration;
	
	//private String earlyDateTimePickupLoadedContainerAtWarehouse;
	//private String lateDateTimePickupLoadedContainerAtWarehouse;
	//private int attachLoadedMoocContainerDuration;
	private PickupWarehouseInfo[] pickupWarehouses;
	
	private String portCode;
	private String earlyDateTimeUnloadAtPort;
	private String lateDateTimeUnloadAtPort;
	private int unloadDuration;
	private String customerCode;
	private String customerName;
	
	private int rejectCode;
	
	//private String planSegment;// "1","2","12","13","123",...
	public String getLateDateTimeLoadAtWarehouse(){
		String s = pickupWarehouses[0].getLateDateTimeLoadAtWarehouse();
		for(int i = 1; i < pickupWarehouses.length; i++){
			if(DateTimeUtils.dateTime2Int(s) < DateTimeUtils.dateTime2Int(pickupWarehouses[i].getLateDateTimeLoadAtWarehouse()))
				s = pickupWarehouses[i].getLateDateTimeLoadAtWarehouse();
		}
		return s;
	}
	public String getEarlyDateTimeLoadAtWarehouse(){
		String s = pickupWarehouses[0].getEarlyDateTimeLoadAtWarehouse();
		for(int i = 1; i < pickupWarehouses.length; i++){
			if(DateTimeUtils.dateTime2Int(s) > DateTimeUtils.dateTime2Int(pickupWarehouses[i].getEarlyDateTimeLoadAtWarehouse()))
				s = pickupWarehouses[i].getEarlyDateTimeLoadAtWarehouse();
		}
		return s;
	}
	public ExportContainerRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExportContainerRequest(String orderItemID, boolean isSwap, String orderItemSwapID, String shipCompanyCode,
			String depotContainerCode, String containerCategory, 
			String containerCode, String containerNo, double weight,
			String earlyDateTimePickupAtDepot,
			String lateDateTimePickupAtDepot,
			int loadDuration,
			PickupWarehouseInfo[] pickupWarehouses, String portCode,
			String earlyDateTimeUnloadAtPort, String lateDateTimeUnloadAtPort,
			int unloadDuration, String customerCode, String customerName) {
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
		this.earlyDateTimePickupAtDepot = earlyDateTimePickupAtDepot;
		this.lateDateTimePickupAtDepot = lateDateTimePickupAtDepot;
		this.loadDuration = loadDuration;
		this.pickupWarehouses = pickupWarehouses;
		this.portCode = portCode;
		this.earlyDateTimeUnloadAtPort = earlyDateTimeUnloadAtPort;
		this.lateDateTimeUnloadAtPort = lateDateTimeUnloadAtPort;
		this.unloadDuration = unloadDuration;
		this.customerCode = customerCode;
		this.customerName = customerName;
	}

	public String getOrderItemID() {
		return orderItemID;
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

	public String getShipCompanyCode() {
		return shipCompanyCode;
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
	public void setSwap(boolean isSwap) {
		this.isSwap = isSwap;
	}
	public void setOrderItemSwapID(String orderItemSwapID){
		this.orderItemSwapID = orderItemSwapID;
	}
	public void setShipCompanyCode(String shipCompanyCode) {
		this.shipCompanyCode = shipCompanyCode;
	}

	public String getDepotContainerCode() {
		return depotContainerCode;
	}

	public void setDepotContainerCode(String depotContainerCode) {
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

	public String getEarlyDateTimePickupAtDepot() {
		return earlyDateTimePickupAtDepot;
	}

	public void setEarlyDateTimePickupAtDepot(String earlyDateTimePickupAtDepot) {
		this.earlyDateTimePickupAtDepot = earlyDateTimePickupAtDepot;
	}

	public String getLateDateTimePickupAtDepot() {
		return lateDateTimePickupAtDepot;
	}

	public void setLateDateTimePickupAtDepot(String lateDateTimePickupAtDepot) {
		this.lateDateTimePickupAtDepot = lateDateTimePickupAtDepot;
	}
	
	public int getLoadDuration() {
		return loadDuration;
	}

	public void setLoadDuration(int loadDuration) {
		this.loadDuration = loadDuration;
	}

	public PickupWarehouseInfo[] getPickupWarehouses() {
		return pickupWarehouses;
	}

	public void setPickupWarehouses(PickupWarehouseInfo[] pickupWarehouses) {
		this.pickupWarehouses = pickupWarehouses;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getEarlyDateTimeUnloadAtPort() {
		return earlyDateTimeUnloadAtPort;
	}

	public void setEarlyDateTimeUnloadAtPort(String earlyDateTimeUnloadAtPort) {
		this.earlyDateTimeUnloadAtPort = earlyDateTimeUnloadAtPort;
	}

	public String getLateDateTimeUnloadAtPort() {
		return lateDateTimeUnloadAtPort;
	}

	public void setLateDateTimeUnloadAtPort(String lateDateTimeUnloadAtPort) {
		this.lateDateTimeUnloadAtPort = lateDateTimeUnloadAtPort;
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
