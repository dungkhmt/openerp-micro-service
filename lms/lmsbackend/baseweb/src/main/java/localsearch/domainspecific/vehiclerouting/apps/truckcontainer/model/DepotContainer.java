package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class DepotContainer {
	private String code;
	private String locationCode;
	private int pickupContainerDuration;
	private int deliveryContainerDuration;
	private boolean returnedContainer;
	public boolean getReturnedContainer() {
		return returnedContainer;
	}
	public void setReturnedContainer(boolean returnedContainer) {
		this.returnedContainer = returnedContainer;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public int getPickupContainerDuration() {
		return pickupContainerDuration;
	}
	public void setPickupContainerDuration(int pickupContainerDuration) {
		this.pickupContainerDuration = pickupContainerDuration;
	}
	public int getDeliveryContainerDuration() {
		return deliveryContainerDuration;
	}
	public void setDeliveryContainerDuration(int deliveryContainerDuration) {
		this.deliveryContainerDuration = deliveryContainerDuration;
	}
	public DepotContainer(String code, String locationCode,
			int pickupContainerDuration, int deliveryContainerDuration) {
		super();
		this.code = code;
		this.locationCode = locationCode;
		this.pickupContainerDuration = pickupContainerDuration;
		this.deliveryContainerDuration = deliveryContainerDuration;
	}
	public DepotContainer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
