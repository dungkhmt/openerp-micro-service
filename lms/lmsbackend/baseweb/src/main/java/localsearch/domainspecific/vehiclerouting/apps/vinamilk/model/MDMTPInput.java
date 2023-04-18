package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class MDMTPInput {
	Customer[] customer;
	Vehicle[] vehicle;
	Parking[] parking;
	DistributionCenter[] distributionCenter;
	Product[] product;
	Order[] order;
	Distance[] distance;
	
	public MDMTPInput(Customer[] customer, Vehicle[] vehicle, Parking[] parking,
			DistributionCenter[] distributionCenter, Product[] product, Order[] order, Distance[] distance) {
		super();
		this.customer = customer;
		this.vehicle = vehicle;
		this.parking = parking;
		this.distributionCenter = distributionCenter;
		this.product = product;
		this.order = order;
		this.distance = distance;
	}
	public Customer[] getCustomer() {
		return customer;
	}
	public void setCustomer(Customer[] customer) {
		this.customer = customer;
	}
	public Vehicle[] getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle[] vehicle) {
		this.vehicle = vehicle;
	}
	public Parking[] getParking() {
		return parking;
	}
	public void setParking(Parking[] parking) {
		this.parking = parking;
	}
	public DistributionCenter[] getDistributionCenter() {
		return distributionCenter;
	}
	public void setDistributionCenter(DistributionCenter[] distributionCenter) {
		this.distributionCenter = distributionCenter;
	}
	public Product[] getProduct() {
		return product;
	}
	public void setProduct(Product[] product) {
		this.product = product;
	}

	public Order[] getOrder() {
		return order;
	}
	public void setOrder(Order[] order) {
		this.order = order;
	}
	public Distance[] getDistance() {
		return distance;
	}
	public void setDistance(Distance[] distance) {
		this.distance = distance;
	}
	
	
}
