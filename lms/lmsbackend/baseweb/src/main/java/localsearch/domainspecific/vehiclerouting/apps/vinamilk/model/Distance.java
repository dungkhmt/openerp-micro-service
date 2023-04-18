package localsearch.domainspecific.vehiclerouting.apps.vinamilk.model;

public class Distance {
	private double d;
	private String from;
	private String to;
	private double t;
	public Distance(double d, String from, String to, double t) {
		super();
		this.d = d;
		this.from = from;
		this.to = to;
		this.t = t;
	}
	public double getD() {
		return d;
	}
	public void setD(double d) {
		this.d = d;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public double getT() {
		return t;
	}
	public void setT(double t) {
		this.t = t;
	}
	
}
