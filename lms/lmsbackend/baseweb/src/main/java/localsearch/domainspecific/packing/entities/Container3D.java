package localsearch.domainspecific.packing.entities;

public class Container3D {
	private int width;
	private int length;
	private int height;
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public Container3D(int width, int length, int height) {
		super();
		this.width = width;
		this.length = length;
		this.height = height;
	}
	
}
