package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class Order {
	private String orderCode;
	private String orderItem;
	private int quantity;
	private String shiptoCode;
	public Order(String orderCode, String orderItem, int quantity, String shiptoCode) {
		super();
		this.orderCode = orderCode;
		this.orderItem = orderItem;
		this.quantity = quantity;
		this.shiptoCode = shiptoCode;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(String orderItem) {
		this.orderItem = orderItem;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getShiptoCode() {
		return shiptoCode;
	}
	public void setShiptoCode(String shiptoCode) {
		this.shiptoCode = shiptoCode;
	}
	
	
}
