package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class PickupWarehouseInfo {
	private String wareHouseCode;
	private String earlyDateTimeLoadAtWarehouse;
	private String lateDateTimeLoadAtWarehouse;
	private int loadDuration;
	private int detachEmptyMoocContainerDuration;
	
	private String earlyDateTimePickupLoadedContainerAtWarehouse;
	private String lateDateTimePickupLoadedContainerAtWarehouse;
	private int attachLoadedMoocContainerDuration;
	public PickupWarehouseInfo(String wareHouseCode,
			String earlyDateTimeLoadAtWarehouse,
			String lateDateTimeLoadAtWarehouse, int loadDuration,
			int detachEmptyMoocContainerDuration,
			String earlyDateTimePickupLoadedContainerAtWarehouse,
			String lateDateTimePickupLoadedContainerAtWarehouse,
			int attachLoadedMoocContainerDuration) {
		super();
		this.wareHouseCode = wareHouseCode;
		this.earlyDateTimeLoadAtWarehouse = earlyDateTimeLoadAtWarehouse;
		this.lateDateTimeLoadAtWarehouse = lateDateTimeLoadAtWarehouse;
		this.loadDuration = loadDuration;
		this.detachEmptyMoocContainerDuration = detachEmptyMoocContainerDuration;
		this.earlyDateTimePickupLoadedContainerAtWarehouse = earlyDateTimePickupLoadedContainerAtWarehouse;
		this.lateDateTimePickupLoadedContainerAtWarehouse = lateDateTimePickupLoadedContainerAtWarehouse;
		this.attachLoadedMoocContainerDuration = attachLoadedMoocContainerDuration;
	}
	public PickupWarehouseInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getWareHouseCode() {
		return wareHouseCode;
	}
	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
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
	public int getLoadDuration() {
		return loadDuration;
	}
	public void setLoadDuration(int loadDuration) {
		this.loadDuration = loadDuration;
	}
	public int getDetachEmptyMoocContainerDuration() {
		return detachEmptyMoocContainerDuration;
	}
	public void setDetachEmptyMoocContainerDuration(
			int detachEmptyMoocContainerDuration) {
		this.detachEmptyMoocContainerDuration = detachEmptyMoocContainerDuration;
	}
	public String getEarlyDateTimePickupLoadedContainerAtWarehouse() {
		return earlyDateTimePickupLoadedContainerAtWarehouse;
	}
	public void setEarlyDateTimePickupLoadedContainerAtWarehouse(
			String earlyDateTimePickupLoadedContainerAtWarehouse) {
		this.earlyDateTimePickupLoadedContainerAtWarehouse = earlyDateTimePickupLoadedContainerAtWarehouse;
	}
	public String getLateDateTimePickupLoadedContainerAtWarehouse() {
		return lateDateTimePickupLoadedContainerAtWarehouse;
	}
	public void setLateDateTimePickupLoadedContainerAtWarehouse(
			String lateDateTimePickupLoadedContainerAtWarehouse) {
		this.lateDateTimePickupLoadedContainerAtWarehouse = lateDateTimePickupLoadedContainerAtWarehouse;
	}
	public int getAttachLoadedMoocContainerDuration() {
		return attachLoadedMoocContainerDuration;
	}
	public void setAttachLoadedMoocContainerDuration(
			int attachLoadedMoocContainerDuration) {
		this.attachLoadedMoocContainerDuration = attachLoadedMoocContainerDuration;
	}
	
	
}
