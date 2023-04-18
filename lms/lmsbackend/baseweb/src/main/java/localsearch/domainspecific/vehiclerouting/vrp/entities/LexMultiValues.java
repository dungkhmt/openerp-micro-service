/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 22/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp.entities;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
public class LexMultiValues {
	private ArrayList<Double> values;
	public LexMultiValues(){
		values = new ArrayList<Double>();
	}
	public LexMultiValues(LexMultiValues V){
		values = new ArrayList<Double>();
		for(int i = 0; i < V.size(); i++)
			values.add(V.get(i));
	}
	public LexMultiValues(ArrayList<Double> values){
		this.values = values;
	}
	public LexMultiValues(double v){
		values = new ArrayList<Double>();
		values.add(v);
	}
	public LexMultiValues(double v1, double v2){
		values = new ArrayList<Double>();
		values.add(v1);
		values.add(v2);
	}
	public void fill(int sz, double v){
		values.clear();
		for(int i = 0; i < sz; i++)
			values.add(v);
	}
	public int size(){
		return values.size();
	}
	public void clear(){
		values.clear();
	}
	public void add(double v){
		values.add(v);
	}
	public double get(int i){
		return values.get(i);
	}
	public LexMultiValues plus(LexMultiValues mv){
		ArrayList<Double> A = new ArrayList<Double>();
		for(int i = 0; i < size(); i++)
			A.add(get(i) + mv.get(i));
		return new LexMultiValues(A);
	}
	public boolean lt(LexMultiValues V){
		for(int i = 0; i < values.size(); i++){
			double x = values.get(i);
			double y = V.get(i);
			if (!CBLSVR.equal(x, y)) {
				return x < y; 
			}
		}
		return false;
	}
	public boolean lt(double v){
		for(int i = 0; i < values.size(); i++){
			double x = values.get(i);
			if (!CBLSVR.equal(x, v)) {
				return x < v; 
			}
		}
		return false;
	}
	
	public boolean leq(LexMultiValues V){
		for(int i = 0; i < values.size(); i++){
			double x = values.get(i);
			double y = V.get(i);
			if (!CBLSVR.equal(x, y)) {
				return x < y; 
			}
		}
		return true;
	}
	
	public boolean eq(LexMultiValues V){
		for(int i = 0; i < values.size(); i++){
			double x = values.get(i);
			double y = V.get(i);
			if (!CBLSVR.equal(x, y)) {
				return false; 
			}
		}
		return true;
	}
	public void set(LexMultiValues v){
		values.clear();
		for(int i = 0; i < v.size(); i++){
			values.add(v.get(i));
		}
	}
	public String toString(){
		String s = "";
		for(int i = 0; i < values.size(); i++)
			s = s + values.get(i) + ", ";
		return s;
	}
}
