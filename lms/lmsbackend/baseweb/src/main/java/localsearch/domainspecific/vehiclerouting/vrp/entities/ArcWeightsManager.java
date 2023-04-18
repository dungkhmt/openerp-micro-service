package localsearch.domainspecific.vehiclerouting.vrp.entities;

import java.util.*;

import localsearch.domainspecific.vehiclerouting.vrp.IDistanceManager;

public class ArcWeightsManager implements IDistanceManager{
	private ArrayList<Point> points;
	private HashMap<Point, Integer> map;
	private double[][] weights;
	
	public ArcWeightsManager(ArrayList<Point> points){
		this.points = points;
		map = new HashMap<Point, Integer>();
		for(int i = 0; i < points.size(); i++){
			map.put(points.get(i), i);
			//System.out.println(name() + "::constructor, map.put(" + points.get(i).ID + "," + i + ")");
		}
		weights = new double[points.size()][points.size()];
	}
	public String name(){
		return "ArcWeightsManager";
	}
	public void setWeight(Point p1, Point p2, double w){
		//System.out.println(name() + "::setWeight p1 = " + p1.ID + ", p2 = " + p2.ID + 
		//		", map p1 = " + map.get(p1) + ", map p2 = " + map.get(p2) + ", w = " + w);
		weights[map.get(p1)][map.get(p2)] = w;
	}
	public double getWeight(Point p1, Point p2){
		return weights[map.get(p1)][map.get(p2)];
	}

	public double getDistance(Point x, Point y){
		return getWeight(x,y);
	}
	public double[][] getWeight() {
		return weights;
	}
	public ArrayList<Point> getPoints(){
		return this.points;
	}

}
