/**
 * Copyright (c) 2015,
 *      Pham Quang Dung (dungkhmt@gmail.com),
 *      Nguyen Thanh Hoang (thnbk56@gmail.com),
 *	Nguyen Van Thanh (nvthanh1994@gmail.com),
 *	Le Kim Thu (thulek@gmail.com) 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * start date: 07/11/2015
 */

/*
 * This class represents the vehicle routing variable in online problem (requests, clients arrive online)
 */
package localsearch.domainspecific.vehiclerouting.vrp.online;

import java.io.PrintWriter;
import java.util.*;


import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class VarRoutesVROnline extends VarRoutesVR {
	private VRManagerOnline mgr;
	private boolean[] fixed;// fixed[v] = true if the point v has been passed
	private HashSet<Point> activePoints;
	private ArrayList<Point>[] passedPoints;
	private HashMap<Integer, Boolean> moving;
	protected int maxNbRoutes = 100;
	protected static final int scaleSz = 100;
	private PrintWriter log = null;
	
	public VarRoutesVROnline(VRManagerOnline mgr, int maxNbVehicles, int maxNbClients){
		super(mgr);
		this.mgr = mgr;
	}
	public void setLog(PrintWriter log){
		this.log = log;
	}
	public VarRoutesVROnline(VRManagerOnline mgr){
		super(mgr);
		this.mgr = mgr;
		activePoints = new HashSet<Point>();
		passedPoints = new ArrayList[maxNbRoutes];
		for(int i = 0; i < maxNbRoutes; i++)
			passedPoints[i] = new ArrayList<Point>();
		
		moving = new HashMap<Integer, Boolean>();
	}
	protected void scaleUp(){
		maxNbRoutes += scaleSz;
		ArrayList<Point>[] t_passedPoints = new ArrayList[maxNbRoutes];
		for(int i = 0; i < t_passedPoints.length; i++){
			t_passedPoints[i] = new ArrayList<Point>();
		}
		for(int i = 0; i < passedPoints.length; i++){
			for(Point p : passedPoints[i])
				t_passedPoints[i].add(p);
		}
		passedPoints = t_passedPoints;
	}
	public HashSet<Point> getActivePoints(){
		return activePoints;
	}
	public Point startingPoint(int k){
		return super.getStartingPointOfRoute(k);
		//int x = super.getStartingPointOfRoute(k);
		//return getAllPoints().get(x-1);
	}
	public Point terminatingPoint(int k){
		return super.getTerminatingPointOfRoute(k);
		//int x = super.getTerminatingPointOfRoute(k);
		//return getAllPoints().get(x-1);
	}
	public void setMoving(int k, boolean isMoving){
		moving.put(k, isMoving);
	}
	public boolean isMoving(int k){
		if(moving.get(k) == true) return true;
		return false;
	}
	
	public String name(){
		return "VarRoutesVROnline";
	}
	public void update(int k, Point nextPoint){
		//System.out.println(name() + "::update(" + k + ", nextPoint = " + nextPoint.ID + ")");
		//System.exit(-1);
		
		Point s = next(getStartingPointOfRoute(k));
		//passedPoints[k].clear();
		while(s != nextPoint){
			activePoints.remove(s);
			passedPoints[k].add(s);
			//log.println(name() + "::update(" + k + "," + nextPoint.ID + ") --> passedPoint[" + k + "].add(" + s.ID + ")" + 
			//", --> XR.full = " + toStringFull());
			
			s = next(s);
		}
		performRemoveSequencePoints(getStartingPointOfRoute(k),nextPoint);
		
	}
	public String toStringFull(){
		String s = "\n";
		for(int k = 1; k <= getNbRoutes(); k++){
			Point st = getStartingPointOfRoute(k);
			
			s = s + "route["+ k + "] = " + st.ID + " -> ";
			for(Point p: passedPoints[k]){
				s = s + p.ID + " -> ";
			}
			Point t = getTerminatingPointOfRoute(k);
			s = s + t.ID + "\n";
		}
		return s;
	}
	public Point getPoint(int id){
		return getAllPoints().get(id-1);
	}
	
	public ArrayList<Point> getPassedPoints(int k){
		// return the set of points that the vehicle k has passed from the last time point
		return passedPoints[k];
	}
	public boolean moving(int k){
		return getStartingPointOfRoute(k) != getTerminatingPointOfRoute(k);
	}
	public VRManagerOnline getVRManagerOnline(){
		return mgr;
	}
	public void performAddOnePoint(Point x, Point y){
		super.performAddOnePoint(x, y);
		activePoints.add(x);
	}
	public void performRemoveOnePoint(int x){
		activePoints.remove(x);
	}
	public void addRoute(Point sp, Point tp){
		if(passedPoints.length >= maxNbRoutes){
			scaleUp();
		}
		super.addRoute(sp, tp);
		activePoints.add(sp);
		activePoints.add(tp);
	}
	public String toString(){
		String s = super.toString();
		return s;
	}
 	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
