package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class ExportLadenRequests {
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
	private String earlyDateTimeAttachAtWarehouse;
	private String lateDateTimeUnloadAtPort;
	private String moocCode;
	private double weight;
	private String wareHouseCode;
	private String portCode;
	private int linkContainerAtWarehouseDuration;
	private int releaseLoadedContainerAtPortDuration;
	private int rejectCode;
	private int prevStatusID;
	

	public ExportLadenRequests(int id, boolean isBreakRomooc, String containerCategory,
			String containerCode, String containerType, String containerNo, String orderCode, String customerCode,
			String customerName, String requestDate, String earlyDateTimeAttachAtWarehouse,
			String lateDateTimeUnloadAtPort,
			String moocCode, double weight, String wareHouseCode,
			String portCode, int linkContainerAtWarehouseDuration,
			int releaseLoadedContainerAtPortDuration,
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
		this.earlyDateTimeAttachAtWarehouse = earlyDateTimeAttachAtWarehouse;
		this.lateDateTimeUnloadAtPort = lateDateTimeUnloadAtPort;
		this.moocCode = moocCode;
		this.weight = weight;
		this.wareHouseCode = wareHouseCode;
		this.portCode = portCode;
		this.linkContainerAtWarehouseDuration = linkContainerAtWarehouseDuration;
		this.releaseLoadedContainerAtPortDuration = releaseLoadedContainerAtPortDuration;
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


	public String getEarlyDateTimeAttachAtWarehouse() {
		return earlyDateTimeAttachAtWarehouse;
	}


	public void setEarlyDateTimeAttachAtWarehouse(
			String earlyDateTimeAttachAtWarehouse) {
		this.earlyDateTimeAttachAtWarehouse = earlyDateTimeAttachAtWarehouse;
	}

	public String getLateDateTimeUnloadAtPort() {
		return lateDateTimeUnloadAtPort;
	}


	public void setLateDateTimeUnloadAtPort(String lateDateTimeUnloadAtPort) {
		this.lateDateTimeUnloadAtPort = lateDateTimeUnloadAtPort;
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


	public String getWareHouseCode() {
		return wareHouseCode;
	}


	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}


	public String getPortCode() {
		return portCode;
	}


	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}


	public int getLinkContainerAtWarehouseDuration() {
		return linkContainerAtWarehouseDuration;
	}


	public void setLinkContainerAtWarehouseDuration(
			int linkContainerAtWarehouseDuration) {
		this.linkContainerAtWarehouseDuration = linkContainerAtWarehouseDuration;
	}


	public int getReleaseLoadedContainerAtPortDuration() {
		return releaseLoadedContainerAtPortDuration;
	}


	public void setReleaseLoadedContainerAtPortDuration(
			int releaseLoadedContainerAtPortDuration) {
		this.releaseLoadedContainerAtPortDuration = releaseLoadedContainerAtPortDuration;
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

	public ExportLadenRequests() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
