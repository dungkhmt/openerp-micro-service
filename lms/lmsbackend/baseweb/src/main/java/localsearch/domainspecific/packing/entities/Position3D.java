package localsearch.domainspecific.packing.entities;

public class Position3D {
	private int x_w;
	private int x_l;
	private int x_h;
	
	private int dw;// 
	private int dl;
	private int dh;
	
	public String toString(){
		return "(" + x_w + "," + x_l + "," + x_h + ")";
	}
	public int getDw() {
		return dw;
	}
	public void setDw(int dw) {
		this.dw = dw;
	}
	public int getDl() {
		return dl;
	}
	public void setDl(int dl) {
		this.dl = dl;
	}
	public int getDh() {
		return dh;
	}
	public void setDh(int dh) {
		this.dh = dh;
	}
	public int getX_w() {
		return x_w;
	}
	public void setX_w(int x_w) {
		this.x_w = x_w;
	}
	public int getX_l() {
		return x_l;
	}
	public void setX_l(int x_l) {
		this.x_l = x_l;
	}
	public int getX_h() {
		return x_h;
	}
	public void setX_h(int x_h) {
		this.x_h = x_h;
	}
	public Position3D(int x_w, int x_l, int x_h) {
		super();
		this.x_w = x_w;
		this.x_l = x_l;
		this.x_h = x_h;
	}
	
}
