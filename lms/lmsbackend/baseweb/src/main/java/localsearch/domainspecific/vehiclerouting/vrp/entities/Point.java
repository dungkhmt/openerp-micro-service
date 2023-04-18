
package localsearch.domainspecific.vehiclerouting.vrp.entities;

import java.text.DecimalFormat;
import java.util.ArrayList;

/*
 * Authors: Nguyen Thanh Hoang (thnbk56@gmail.com)
 * date: 30/08/2015
 */
public class Point {
	public int ID;
	public String locationId;
	private String typeOfPoint;
	private String typeOfProduct;
	private double orderWeight;
	private double limitedWeight;
	private double x, y;
	private String locationCode;
    //ArrayList<Integer> bucketIDs;
	public Point(int iD, String locationId, String typeOfPoint, String typeOfProduct, double orderWeight,
			double limitedWeight) {
		super();
		ID = iD;
		this.locationId = locationId;
		this.typeOfPoint = typeOfPoint;
		this.typeOfProduct = typeOfProduct;
		this.orderWeight = orderWeight;
		this.limitedWeight = limitedWeight;
	}
	public Point(int ID, double x, double y){
    	this.ID = ID;
    	this.x = x; this.y = y;
    	//this.bucketIDs = new ArrayList<Integer>();
    }
    public Point(){
    	this.ID = 0;this.x = 0;this.y = 0;
    }
    public Point(int ID){
    	this.ID = ID; this.x = 0; this.y = 0;
    }
    
    public Point(int ID, String locationCode){
    	this.ID = ID;
    	this.locationCode = locationCode;
    	this.x = 0;
    	this.y = 0;
    }
    
	public void setID(int iD) {
		ID = iD;
	}

    public int getID() {
    	return ID;
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
//    public ArrayList<Integer> getBucketIDs(){
//    	return bucketIDs;
//    }
//    
//    public void setBucketIDs(ArrayList<Integer> bkIDs){
//    	this.bucketIDs = bkIDs;
//    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    
    public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getTypeOfPoint() {
		return typeOfPoint;
	}
	public void setTypeOfPoint(String typeOfPoint) {
		this.typeOfPoint = typeOfPoint;
	}
	public String getTypeOfProduct() {
		return typeOfProduct;
	}
	public void setTypeOfProduct(String typeOfProduct) {
		this.typeOfProduct = typeOfProduct;
	}
	public double getOrderWeight() {
		return orderWeight;
	}
	public void setOrderWeight(double orderWeight) {
		this.orderWeight = orderWeight;
	}
	public double getLimitedWeight() {
		return limitedWeight;
	}
	public void setLimitedWeight(double limitedWeight) {
		this.limitedWeight = limitedWeight;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

    double degrees(Point p) {
        double X = p.x - x;
        double Y = p.y - y;
        double d = Math.toDegrees(Math.atan2(Y, X));
        if (d < 0) d += 360;
        return d;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        //this.bucketIDs = new ArrayList<Integer>();
    }

    public double distance(Point p) {
        return Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
    }
    public double mahattanDistance(Point p){
    	Point p1 = new Point(x,p.y);
    	return distance(p1) + p1.distance(p);
    }
    public Point clone(){
    	return new Point(ID,x,y);
    }
    public String toString(){
    	DecimalFormat df = new DecimalFormat("#.00");
    	return ID + " (" + df.format(x) + "," + df.format(y) + ")";
    }
    public static void main(String[] argn) {
        System.out.print(Math.toDegrees(Math.atan2(-0.5, 1)));
    }
}
