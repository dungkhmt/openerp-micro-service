package localsearch.domainspecific.packing.models;

import localsearch.domainspecific.packing.entities.Container3D;
import localsearch.domainspecific.packing.entities.Item3D;

/*
 * by dungkhmt@gmail.com
 * start date: 26/04/2017
 */
public class Model3D {

	public Model3D(Container3D container, Item3D[] items){
		this.container = container;
		this.items = items;
		x_w = new int[items.length];
		x_l = new int[items.length];
		x_h = new int[items.length];
	}
	public Container3D getContainer() {
		return container;
	}



	public void setContainer(Container3D container) {
		this.container = container;
	}



	public Item3D[] getItems() {
		return items;
	}



	public void setItems(Item3D[] items) {
		this.items = items;
	}



	public int[] getX_w() {
		return x_w;
	}



	public void setX_w(int[] x_w) {
		this.x_w = x_w;
	}



	public int[] getX_l() {
		return x_l;
	}



	public void setX_l(int[] x_l) {
		this.x_l = x_l;
	}



	public int[] getX_h() {
		return x_h;
	}



	public void setX_h(int[] x_h) {
		this.x_h = x_h;
	}



	// input data
	private Container3D container;
	private Item3D[] items;
	
	private int[] x_w;// coordinates in width axis
	private int[] x_l;// coordinates in length axis
	private int[] x_h;// coordinates in height axis
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
