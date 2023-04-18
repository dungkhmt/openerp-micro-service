package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class ImportLadenRequests {
	private int id;
	private boolean isBreakRomooc;
	private String containerCategory;
	private String containerCode;
	private String containerType;
	private String containerNo;
	private String orderCode;
	private String customerCode;
	private String customerName;
	private String requestDate;
	private String earlyDateTimePickupAtPort;
	private String lateDateTimePickupAtPort;
	private String earlyDateTimeUnloadAtWarehouse;
	private String lateDateTimeUnloadAtWarehouse;
	private String moocCode;
	private double weight;
	private String portCode;
	private String wareHouseCode;
	private int linkLoadedContainerAtPortDuration;
	
	private int rejectCode;
	private int prevStatusID;
	

	public ImportLadenRequests(int id, boolean isBreakRomooc, String containerCategory,
			String containerCode, String containerType, String containerNo, String orderCode, String customerCode,
			String customerName, String requestDate, String earlyDateTimePickupAtPort,
			String lateDateTimePickupAtPort,
			String earlyDateTimeUnloadAtWarehouse,
			String lateDateTimeUnloadAtWarehouse, String moocCode,
			double weight, String portCode, String wareHouseCode,
			int linkLoadedContainerAtPortDuration,
			int prevStatusID) {
		super();
		this.id = id;
		this.isBreakRomooc = isBreakRomooc;
		this.containerCategory = containerCategory;
		this.containerCode = containerCode;
		this.containerType = containerType;
		this.containerNo = containerNo;
		this.orderCode = orderCode;
		this.customerCode = customerCode;
		this.customerName = customerName;
		this.requestDate = requestDate;
		this.earlyDateTimePickupAtPort = earlyDateTimePickupAtPort;
		this.lateDateTimePickupAtPort = lateDateTimePickupAtPort;
		this.earlyDateTimeUnloadAtWarehouse = earlyDateTimeUnloadAtWarehouse;
		this.lateDateTimeUnloadAtWarehouse = lateDateTimeUnloadAtWarehouse;
		this.moocCode = moocCode;
		this.weight = weight;
		this.portCode = portCode;
		this.wareHouseCode = wareHouseCode;
		this.linkLoadedContainerAtPortDuration = linkLoadedContainerAtPortDuration;
		this.prevStatusID = prevStatusID;
	}

	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}

	public boolean getIsBreakRomooc() {
		return isBreakRomooc;
	}



	public void setBreakRomooc(boolean isBreakRomooc) {
		this.isBreakRomooc = isBreakRomooc;
	}

	public void setIsBreakRomooc(boolean isBreakRomooc) {
		this.isBreakRomooc = isBreakRomooc;
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

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public String getContainerNo() {
		return containerNo;
	}



	public void setContainerNo(String containerNo) {
		this.containerNo = containerNo;
	}



	public String getOrderCode() {
		return orderCode;
	}



	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}



	public String getCustomerCode() {
		return customerCode;
	}



	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName(){
		return this.customerName;
	}
	
	public void setCustomerName(String customerName){
		this.customerName = customerName;
	}

	public String getRequestDate() {
		return requestDate;
	}



	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
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



	public String getEarlyDateTimeUnloadAtWarehouse() {
		return earlyDateTimeUnloadAtWarehouse;
	}



	public void setEarlyDateTimeUnloadAtWarehouse(
			String earlyDateTimeUnloadAtWarehouse) {
		this.earlyDateTimeUnloadAtWarehouse = earlyDateTimeUnloadAtWarehouse;
	}



	public String getLateDateTimeUnloadAtWarehouse() {
		return lateDateTimeUnloadAtWarehouse;
	}



	public void setLateDateTimeUnloadAtWarehouse(
			String lateDateTimeUnloadAtWarehouse) {
		this.lateDateTimeUnloadAtWarehouse = lateDateTimeUnloadAtWarehouse;
	}



	public String getMoocCode() {
		return moocCode;
	}



	public void setMoocCode(String moocCode) {
		this.moocCode = moocCode;
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



	public String getWareHouseCode() {
		return wareHouseCode;
	}



	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}



	public int getLinkLoadedContainerAtPortDuration() {
		return linkLoadedContainerAtPortDuration;
	}



	public void setLinkLoadedContainerAtPortDuration(
			int linkLoadedContainerAtPortDuration) {
		this.linkLoadedContainerAtPortDuration = linkLoadedContainerAtPortDuration;
	}

	public int getRejectCode() {
		return rejectCode;
	}

	public void setRejectCode(int rejectCode) {
		this.rejectCode = rejectCode;
	}

	public int getPrevStatusID() {
		return prevStatusID;
	}

	public void setPrevStatusID(int prevStatusID) {
		this.prevStatusID = prevStatusID;
	}

	public ImportLadenRequests() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
