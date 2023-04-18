package localsearch.domainspecific.packing.algorithms;

import localsearch.domainspecific.packing.entities.*;
import java.util.*;
public class RotationGenerator {
	private ArrayList<Item3D> items;
	private int w;
	private int l;
	private int h;
	public RotationGenerator(Item3D I){
		w = I.getWidth(); l = I.getLength(); h = I.getHeight();
		items =new ArrayList<Item3D>();
	}
	public RotationGenerator(int w, int l, int h){
		this.w = w; this.l = l; this.h = h;
		items =new ArrayList<Item3D>();
	}
	
	public void generate(){
		items.clear();
		items.add(new Item3D(w,l,h));
		items.add(new Item3D(w,h,l));
		items.add(new Item3D(l,w,h));
		items.add(new Item3D(l,h,w));
		items.add(new Item3D(h,w,l));
		items.add(new Item3D(h,l,w));
	}
	public ArrayList<Item3D> getItems() {
		return items;
	}
	public void setItems(ArrayList<Item3D> items) {
		this.items = items;
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
	
	
}
