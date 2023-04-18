package localsearch.domainspecific.vehiclerouting.vrp.utils.googlemaps;

public class LatLng {

	/**
	 * @param args
	 */
	public double lat;
	public double lng;
	public LatLng(double lat, double lng){ this.lat = lat; this.lng = lng;}
	public String toString(){ return lat + "," + lng;}
}
