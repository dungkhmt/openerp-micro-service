package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class Container {
	
	private String code;
	private double weight;
	private String categoryCode;
	private String depotContainerCode;
	private String[] returnDepotCodes;// possible depots when finishing services
	private boolean importedContainer;
	private String shipCompanyCode;
	
	
	public Container(String code, double weight, String categoryCode,
			String depotContainerCode, String[] returnDepotCodes,
			boolean importedContainer) {
		super();
		this.code = code;
		this.weight = weight;
		this.categoryCode = categoryCode;
		this.depotContainerCode = depotContainerCode;
		this.returnDepotCodes = returnDepotCodes;
		this.importedContainer = importedContainer;
	}
	public boolean isImportedContainer() {
		return importedContainer;
	}
	public void setImportedContainer(boolean importedContainer) {
		this.importedContainer = importedContainer;
	}
	public Container(String code, double weight, String categoryCode,
			String depotContainerCode, String[] returnDepotCodes) {
		super();
		this.code = code;
		this.weight = weight;
		this.categoryCode = categoryCode;
		this.depotContainerCode = depotContainerCode;
		this.returnDepotCodes = returnDepotCodes;
	}
	public String[] getReturnDepotCodes() {
		return returnDepotCodes;
	}
	public void setReturnDepotCodes(String[] returnDepotCodes) {
		this.returnDepotCodes = returnDepotCodes;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getDepotContainerCode() {
		return depotContainerCode;
	}
	public void setDepotContainerCode(String depotContainerCode) {
		this.depotContainerCode = depotContainerCode;
	}
	
	public String getShipCompanyCode() {
		return shipCompanyCode;
	}
	public void setShipCompanyCode(String shipCompanyCode) {
		this.shipCompanyCode = shipCompanyCode;
	}
	
	public Container() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
