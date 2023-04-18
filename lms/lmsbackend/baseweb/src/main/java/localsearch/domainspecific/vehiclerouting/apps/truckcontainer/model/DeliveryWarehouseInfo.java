package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class DeliveryWarehouseInfo {
	private String wareHouseCode;
	private String earlyDateTimeUnloadAtWarehouse;
	private String lateDateTimeUnloadAtWarehouse;
	private int unloadDuration;
	private int detachLoadedMoocContainerDuration;
	
	// 2nd segment (Warehouse -> depot, Empty)
	private String earlyPickupEmptyContainerAtWarehouse;
	private String latePickupEmptyContainerAtWarehouse;
	private int attachEmptyMoocContainerDuration;
	public String getWareHouseCode() {
		return wareHouseCode;
	}
	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
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
	public int getUnloadDuration() {
		return unloadDuration;
	}
	public void setUnloadDuration(int unloadDuration) {
		this.unloadDuration = unloadDuration;
	}
	public int getDetachLoadedMoocContainerDuration() {
		return detachLoadedMoocContainerDuration;
	}
	public void setDetachLoadedMoocContainerDuration(
			int detachLoadedMoocContainerDuration) {
		this.detachLoadedMoocContainerDuration = detachLoadedMoocContainerDuration;
	}
	public String getEarlyPickupEmptyContainerAtWarehouse() {
		return earlyPickupEmptyContainerAtWarehouse;
	}
	public void setEarlyPickupEmptyContainerAtWarehouse(
			String earlyPickupEmptyContainerAtWarehouse) {
		this.earlyPickupEmptyContainerAtWarehouse = earlyPickupEmptyContainerAtWarehouse;
	}
	public String getLatePickupEmptyContainerAtWarehouse() {
		return latePickupEmptyContainerAtWarehouse;
	}
	public void setLatePickupEmptyContainerAtWarehouse(
			String latePickupEmptyContainerAtWarehouse) {
		this.latePickupEmptyContainerAtWarehouse = latePickupEmptyContainerAtWarehouse;
	}
	public int getAttachEmptyMoocContainerDuration() {
		return attachEmptyMoocContainerDuration;
	}
	public void setAttachEmptyMoocContainerDuration(
			int attachEmptyMoocContainerDuration) {
		this.attachEmptyMoocContainerDuration = attachEmptyMoocContainerDuration;
	}
	public DeliveryWarehouseInfo(String wareHouseCode,
			String earlyDateTimeUnloadAtWarehouse,
			String lateDateTimeUnloadAtWarehouse, int unloadDuration,
			int detachLoadedMoocContainerDuration,
			String earlyPickupEmptyContainerAtWarehouse,
			String latePickupEmptyContainerAtWarehouse,
			int attachEmptyMoocContainerDuration) {
		super();
		this.wareHouseCode = wareHouseCode;
		this.earlyDateTimeUnloadAtWarehouse = earlyDateTimeUnloadAtWarehouse;
		this.lateDateTimeUnloadAtWarehouse = lateDateTimeUnloadAtWarehouse;
		this.unloadDuration = unloadDuration;
		this.detachLoadedMoocContainerDuration = detachLoadedMoocContainerDuration;
		this.earlyPickupEmptyContainerAtWarehouse = earlyPickupEmptyContainerAtWarehouse;
		this.latePickupEmptyContainerAtWarehouse = latePickupEmptyContainerAtWarehouse;
		this.attachEmptyMoocContainerDuration = attachEmptyMoocContainerDuration;
	}
	public DeliveryWarehouseInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
