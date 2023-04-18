package localsearch.domainspecific.vehiclerouting.vrp.utils.googlemaps;

import java.util.ArrayList;

public class Direction {

	private ArrayList<StepDirection> stepsDirection;
	private String startAdd;
	private String endAdd;
	private double lat1;
	private double lng1;
	private double lat2;
	private double lng2;
	private int durations;
	private int distances;
	private String travelmode;
	
	public Direction(ArrayList<StepDirection> stepsDirection, String startAdd,
			String endAdd, double lat1, double lng1, double lat2, double lng2,
			int durations, int distances,String travelMode) {
		super();
		this.stepsDirection = stepsDirection;
		this.startAdd = startAdd;
		this.endAdd = endAdd;
		this.lat1 = lat1;
		this.lng1 = lng1;
		this.lat2 = lat2;
		this.lng2 = lng2;
		this.durations = durations;
		this.distances = distances;
		this.travelmode = travelMode;
	}
	public ArrayList<StepDirection> getStepsDirection() {
		return stepsDirection;
	}
	public void setStepsDirection(ArrayList<StepDirection> stepsDirection) {
		this.stepsDirection = stepsDirection;
	}
	public String getStartAdd() {
		return startAdd;
	}
	public void setStartAdd(String startAdd) {
		this.startAdd = startAdd;
	}
	public String getEndAdd() {
		return endAdd;
	}
	public void setEndAdd(String endAdd) {
		this.endAdd = endAdd;
	}
	public double getLat1() {
		return lat1;
	}
	public void setLat1(double lat1) {
		this.lat1 = lat1;
	}
	public double getLng1() {
		return lng1;
	}
	public void setLng1(double lng1) {
		this.lng1 = lng1;
	}
	public double getLat2() {
		return lat2;
	}
	public void setLat2(double lat2) {
		this.lat2 = lat2;
	}
	public double getLng2() {
		return lng2;
	}
	public void setLng2(double lng2) {
		this.lng2 = lng2;
	}
	public int getDurations() {
		return durations;
	}
	public void setDurations(int durations) {
		this.durations = durations;
	}
	public int getDistances() {
		return distances;
	}
	public void setDistances(int distances) {
		this.distances = distances;
	}
	public String getTravelmode() {
		return travelmode;
	}
	public void setTravelmode(String travelmode) {
		this.travelmode = travelmode;
	}
}
