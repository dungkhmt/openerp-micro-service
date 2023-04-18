package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class ConfigParam {
	private int cutMoocDuration;
	private int linkMoocDuration;
	private int hourPrev;
	private int hourPost;
	private String strategy;
	private boolean constraintWarehouseTractor;
	private boolean constraintWarehouseDriver;
	private boolean constraintWarehouseVendor;
	private boolean constraintWarehouseBreaktimes;
	private boolean constraintWarehouseHard;
	private boolean constraintDriverBalance;
	private int unlinkEmptyContainerDuration;
	private int unlinkLoadedContainerDuration;
	private int linkEmptyContainerDuration;
	private int linkLoadedContainerDuration;
	private String currentTime;
	
	
	public ConfigParam(int cutMoocDuration, int linkMoocDuration,
			int hourPrev, int hourPost,
			String strategy,
			boolean constraintWarehouseTractor,
			boolean constraintWarehouseDriver,
			boolean constraintWarehouseVendor,
			boolean constraintWarehouseBreaktimes,
			boolean constraintWarehouseHard,
			boolean constraintDriverBalance,
			int unlinkEmptyContainerDuration,
			int unlinkLoadedContainerDuration,
			int linkEmptyContainerDuration,
			int linkLoadedContainerDuration,
			String currentTime) {
		super();
		this.cutMoocDuration = cutMoocDuration;
		this.linkMoocDuration = linkMoocDuration;
		this.hourPrev = hourPrev;
		this.hourPost = hourPost;
		this.strategy = strategy;
		this.constraintWarehouseTractor = constraintWarehouseTractor;
		this.constraintWarehouseDriver = constraintWarehouseDriver;
		this.constraintWarehouseVendor = constraintWarehouseVendor;
		this.constraintWarehouseBreaktimes = constraintWarehouseBreaktimes;
		this.constraintWarehouseHard = constraintWarehouseHard;
		this.constraintDriverBalance = constraintDriverBalance;
		this.unlinkEmptyContainerDuration = unlinkEmptyContainerDuration;
		this.unlinkLoadedContainerDuration = unlinkLoadedContainerDuration;
		this.linkEmptyContainerDuration = linkEmptyContainerDuration;
		this.linkLoadedContainerDuration = linkLoadedContainerDuration;
		this.currentTime = currentTime;
	}
	public String getStrategy() {
		return strategy;
	}
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	public int getCutMoocDuration() {
		return cutMoocDuration;
	}
	public void setCutMoocDuration(int cutMoocDuration) {
		this.cutMoocDuration = cutMoocDuration;
	}
	public int getLinkMoocDuration() {
		return linkMoocDuration;
	}
	public void setLinkMoocDuration(int linkMoocDuration) {
		this.linkMoocDuration = linkMoocDuration;
	}
	public ConfigParam(int cutMoocDuration, int linkMoocDuration) {
		super();
		this.cutMoocDuration = cutMoocDuration;
		this.linkMoocDuration = linkMoocDuration;
	}
	public int getHourPrev(){
		return hourPrev;
	}
	public void setHourPrev(int hourPrev){
		this.hourPrev = hourPrev;
	}
	public int getHourPost(){
		return hourPost;
	}
	public void setHourPost(int hourPost){
		this.hourPost = hourPost;
	}
	public boolean getConstraintWarehouseTractor(){
		return this.constraintWarehouseTractor;
	}
	public void setConstraintWarehouseTractor(boolean constraintWarehouseTractor){
		this.constraintWarehouseTractor = constraintWarehouseTractor;
	}
	public boolean getConstraintWarehouseDriver(){
		return this.constraintWarehouseDriver;
	}
	public void setConstraintWarehouseDriver(boolean constraintWarehouseDriver){
		this.constraintWarehouseDriver = constraintWarehouseDriver;
	}
	public boolean getConstraintWarehouseVendor(){
		return this.constraintWarehouseVendor;
	}
	public void setConstraintWarehouseVendor(boolean constraintWarehouseVendor){
		this.constraintWarehouseVendor = constraintWarehouseVendor;
	}
	public boolean getConstraintWarehouseBreaktimes(){
		return this.constraintWarehouseBreaktimes;
	}
	public void setConstraintWarehouseBreaktimes(boolean constraintWarehouseBreaktimes){
		this.constraintWarehouseBreaktimes = constraintWarehouseBreaktimes;
	}
	public boolean getConstraintWarehouseHard(){
		return this.constraintWarehouseHard;
	}
	public void setConstraintWarehouseHard(boolean constraintWarehouseHard){
		this.constraintWarehouseHard = constraintWarehouseHard;
	}
	public boolean getConstraintDriverBalance(){
		return this.constraintDriverBalance;
	}
	public void setConstraintDriverBalance(boolean constraintDriverBalance){
		this.constraintDriverBalance = constraintDriverBalance;
	}
	public int getUnlinkEmptyContainerDuration(){
		return this.unlinkEmptyContainerDuration;
	}
	public void setUnlinkEmptyContainerDuration(int unlinkEmptyContainerDuration){
		this.unlinkEmptyContainerDuration = unlinkEmptyContainerDuration;
	}
	public int getUnlinkLoadedContainerDuration(){
		return this.unlinkLoadedContainerDuration;
	}
	public void setUnlinkLoadedContainerDuration(int unlinkLoadedContainerDuration){
		this.unlinkLoadedContainerDuration = unlinkLoadedContainerDuration;
	}
	public int getLinkEmptyContainerDuration(){
		return this.linkEmptyContainerDuration;
	}
	public void setLinkEmptyContainerDuration(int linkEmptyContainerDuration){
		this.linkEmptyContainerDuration = linkEmptyContainerDuration;
	}
	public int getLinkLoadedContainerDuration(){
		return this.linkLoadedContainerDuration;
	}
	public void setLinkLoadedContainerDuration(int linkLoadedContainerDuration){
		this.linkLoadedContainerDuration = linkLoadedContainerDuration;
	}

	public String getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
	public ConfigParam() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
