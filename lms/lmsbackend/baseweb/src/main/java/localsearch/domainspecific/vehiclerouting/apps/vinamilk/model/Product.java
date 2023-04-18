package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class Product {
	private double grssWeight;
	private String productCode;
	private String type;
	public Product(double grssWeight, String productCode, String type) {
		super();
		this.grssWeight = grssWeight;
		this.productCode = productCode;
		this.type = type;
	}
	public double getGrssWeight() {
		return grssWeight;
	}
	public void setGrssWeight(double grssWeight) {
		this.grssWeight = grssWeight;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
