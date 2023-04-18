package localsearch.domainspecific.packing.entities;

public class Move3D {
	private Position3D position;
	private int w;
	private int l;
	private int h;
	public Position3D getPosition() {
		return position;
	}
	public void setPosition(Position3D position) {
		this.position = position;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public int getL() {
		return l;
	}
	public void setL(int l) {
		this.l = l;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public Move3D(Position3D position, int w, int l, int h) {
		super();
		this.position = position;
		this.w = w;
		this.l = l;
		this.h = h;
	}
	
	
}
