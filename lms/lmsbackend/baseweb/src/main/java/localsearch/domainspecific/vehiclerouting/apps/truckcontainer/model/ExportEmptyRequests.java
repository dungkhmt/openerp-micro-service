package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class ExportEmptyRequests {
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
	private String earlyDateTimePickupAtDepot;
	private String lateDateTimePickupAtDepot;
	private String earlyDateTimeLoadAtWarehouse;
	private String lateDateTimeLoadAtWarehouse;
	private String moocCode;
	private double weight;
	private String depotContainerCode;
	private String wareHouseCode;
	private int linkContainerDuration;// thoi gian de dua cont. rong len mooc
	private int rejectCode;
	private int prevStatusID;
	
	public ExportEmptyRequests(int id, boolean isBreakRomooc, String containerCategory,
			String containerCode, String containerType, String containerNo, String orderCode, String customerCode,
			String customerName, String requestDate, String earlyDateTimePickupAtDepot,
			String lateDateTimePickupAtDepot,
			String earlyDateTimeLoadAtWarehouse,
			String lateDateTimeLoadAtWarehouse, String moocCode, double weight,
			String depotContainerCode, String wareHouseCode,
			int linkContainerDuration,
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
		this.earlyDateTimePickupAtDepot = earlyDateTimePickupAtDepot;
		this.lateDateTimePickupAtDepot = lateDateTimePickupAtDepot;
		this.earlyDateTimeLoadAtWarehouse = earlyDateTimeLoadAtWarehouse;
		this.lateDateTimeLoadAtWarehouse = lateDateTimeLoadAtWarehouse;
		this.moocCode = moocCode;
		this.weight = weight;
		this.depotContainerCode = depotContainerCode;
		this.wareHouseCode = wareHouseCode;
		this.linkContainerDuration = linkContainerDuration;
		this.prevStatusID = prevStatusID;
	}
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	public int getLinkContainerDuration() {
		return linkContainerDuration;
	}
	public void setLinkContainerDuration(int linkContainerDuration) {
		this.linkContainerDuration = linkContainerDuration;
	}
	public boolean getIsBreakRomooc() {
		return isBreakRomooc;
	}
	public void setIsBreakRomooc(boolean isBreakRomooc) {
		this.isBreakRomooc = isBreakRomooc;
	}
	public void setBreakRomooc(boolean isBreakRomooc) {
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
	public String getEarlyDateTimeLoadAtWarehouse() {
		return earlyDateTimeLoadAtWarehouse;
	}
	public void setEarlyDateTimeLoadAtWarehouse(String earlyDateTimeLoadAtWarehouse) {
		this.earlyDateTimeLoadAtWarehouse = earlyDateTimeLoadAtWarehouse;
	}
	public String getLateDateTimeLoadAtWarehouse() {
		return lateDateTimeLoadAtWarehouse;
	}
	public void setLateDateTimeLoadAtWarehouse(String lateDateTimeLoadAtWarehouse) {
		this.lateDateTimeLoadAtWarehouse = lateDateTimeLoadAtWarehouse;
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
	public String getDepotContainerCode() {
		return depotContainerCode;
	}
	public void setDepotContainerCode(String depotContainerCode) {
		this.depotContainerCode = depotContainerCode;
	}
	public String getWareHouseCode() {
		return wareHouseCode;
	}
	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
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
	//	public ExportEmptyRequests(boolean isBreakRomooc, String containerCategory,
//			String containerCode, String containerNo, String orderCode, String customerCode,
//			String requestDate, String earlyDateTimePickupAtDepot,
//			String lateDateTimePickupAtDepot,
//			String earlyDateTimeLoadAtWarehouse,
//			String lateDateTimeLoadAtWarehouse, String moocCode,
//			String depotContainerCode, String wareHouseCode) {
//		super();
//		this.isBreakRomooc = isBreakRomooc;
//		this.containerCategory = containerCategory;
//		this.containerCode = containerCode;
//		this.containerNo = containerNo;
//		this.orderCode = orderCode;
//		this.customerCode = customerCode;
//		this.requestDate = requestDate;
//		this.earlyDateTimePickupAtDepot = earlyDateTimePickupAtDepot;
//		this.lateDateTimePickupAtDepot = lateDateTimePickupAtDepot;
//		this.earlyDateTimeLoadAtWarehouse = earlyDateTimeLoadAtWarehouse;
//		this.lateDateTimeLoadAtWarehouse = lateDateTimeLoadAtWarehouse;
//		this.moocCode = moocCode;
//		this.depotContainerCode = depotContainerCode;
//		this.wareHouseCode = wareHouseCode;
//	}
	public ExportEmptyRequests() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
