package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class DepotMooc{
	private String code;
	private String locationCode;
	private int pickupMoocDuration;
	private int deliveryMoocDuration;
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
	public int getPickupMoocDuration() {
		return pickupMoocDuration;
	}
	public void setPickupMoocDuration(int pickupMoocDuration) {
		this.pickupMoocDuration = pickupMoocDuration;
	}
	public int getDeliveryMoocDuration() {
		return deliveryMoocDuration;
	}
	public void setDeliveryMoocDuration(int deliveryMoocDuration) {
		this.deliveryMoocDuration = deliveryMoocDuration;
	}
	public DepotMooc(String code, String locationCode, int pickupMoocDuration,
			int deliveryMoocDuration) {
		super();
		this.code = code;
		this.locationCode = locationCode;
		this.pickupMoocDuration = pickupMoocDuration;
		this.deliveryMoocDuration = deliveryMoocDuration;
	}
	public DepotMooc() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
		
}
