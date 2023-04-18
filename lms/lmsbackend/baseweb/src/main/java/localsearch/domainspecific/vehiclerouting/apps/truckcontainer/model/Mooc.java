package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class Mooc {
	private int id;
	private String code;
	private String category;// 20, 40, 45
	private int categoryId;
	private double weight;//20, 40, 45
	private String status;
	private int statusId;
	private String depotMoocCode;
	private String depotMoocLocationCode;
	private String[] returnDepotCodes;// possible depots when finishing services
	private Intervals[] intervals;
	
	public Mooc(int id, String code, String category, int categoryId,
			double weight, String status, int statusId,
			String depotMoocCode, String depotMoocLocationCode, String[] returnDepotCodes, Intervals[] intervals) {
		super();
		this.id = id;
		this.code = code;
		this.category = category;
		this.categoryId = categoryId;
		this.weight = weight;
		this.status = status;
		this.statusId = statusId;
		this.depotMoocCode = depotMoocCode;
		this.depotMoocLocationCode = depotMoocLocationCode;
		this.returnDepotCodes = returnDepotCodes;
		this.intervals = intervals;
	}
	public String[] getReturnDepotCodes() {
		return returnDepotCodes;
	}
	public void setReturnDepotCodes(String[] returnDepotCodes) {
		this.returnDepotCodes = returnDepotCodes;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public String getDepotMoocCode() {
		return depotMoocCode;
	}
	public void setDepotMoocCode(String depotMoocCode) {
		this.depotMoocCode = depotMoocCode;
	}
	public String getDepotMoocLocationCode() {
		return depotMoocLocationCode;
	}
	public void setDepotMoocLocationCode(String depotMoocLocationCode) {
		this.depotMoocLocationCode = depotMoocLocationCode;
	}
	public Intervals[] getIntervals() {
		return intervals;
	}
	public void setIntervals(Intervals[] intervals) {
		this.intervals = intervals;
	}
	
	public Mooc() {
		super();
		// TODO Auto-generated constructor stub
	}
		
}
