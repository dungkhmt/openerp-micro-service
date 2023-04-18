/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 23/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp.functions;

import java.util.*;

import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
public class LexMultiFunctions {
	private ArrayList<IFunctionVR> functions;
	
	public LexMultiFunctions(){
		functions = new ArrayList<IFunctionVR>();
	}
	public void add(IFunctionVR f){
		functions.add(f);
	}
	public void add(IConstraintVR c){
		functions.add(new ConstraintViolationsVR(c));
	}
	public LexMultiValues evaluateTwoOptMoveOneRoute(Point x, Point y){
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateTwoOptMoveOneRoute(x, y));
		return eval;
	}

	public LexMultiValues evaluateKPointsMove(ArrayList<Point> x, ArrayList<Point> y){
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateKPointsMove(x, y));
		return eval;
	}
	
	public LexMultiValues evaluateOnePointMove(Point x, Point y){
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateOnePointMove(x, y));
		return eval;
	}
	
	public LexMultiValues evaluateTwoPointsMove(Point x, Point y){
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateTwoPointsMove(x, y));
		return eval;
	}
	
	public LexMultiValues evaluateTwoOptMove1(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateTwoOptMove1(x, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateTwoOptMove2(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateTwoOptMove2(x, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateTwoOptMove3(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateTwoOptMove3(x, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateTwoOptMove4(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateTwoOptMove4(x, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateTwoOptMove5(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateTwoOptMove5(x, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateTwoOptMove6(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateTwoOptMove6(x, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateTwoOptMove7(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateTwoOptMove7(x, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateTwoOptMove8(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateTwoOptMove8(x, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateOrOptMove1(Point x1, Point x2, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateOrOptMove1(x1, x2, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateOrOptMove2(Point x1, Point x2, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateOrOptMove2(x1, x2, y));
		}
		return eval;
	}
	
	public LexMultiValues evaluateThreeOptMove1(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateThreeOptMove1(x, y, z));
		}
		return eval;
	}
	
	public LexMultiValues evaluateThreeOptMove2(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateThreeOptMove2(x, y, z));
		}
		return eval;
	}
	
	public LexMultiValues evaluateThreeOptMove3(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateThreeOptMove3(x, y, z));
		}
		return eval;
	}
	
	public LexMultiValues evaluateThreeOptMove4(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateThreeOptMove4(x, y, z));
		}
		return eval;
	}
	
	public LexMultiValues evaluateThreeOptMove5(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateThreeOptMove5(x, y, z));
		}
		return eval;
	}
	
	public LexMultiValues evaluateThreeOptMove6(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateThreeOptMove6(x, y, z));
		}
		return eval;
	}
	
	public LexMultiValues evaluateThreeOptMove7(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateThreeOptMove7(x, y, z));
		}
		return eval;
	}
	
	public LexMultiValues evaluateThreeOptMove8(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(IFunctionVR f : functions) {
			eval.add(f.evaluateThreeOptMove8(x, y, z));
		}
		return eval;
	}
	
	public LexMultiValues evaluateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2){
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateCrossExchangeMove(x1, y1, x2, y2));
		return eval;
	}
	
	public LexMultiValues evaluateAddOnePoint(Point x, Point y) {
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateAddOnePoint(x, y));
		return eval;
	}
	
	public LexMultiValues evaluateRemoveOnePoint(Point x) {
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateRemoveOnePoint(x));
		return eval;
	}
	
	public LexMultiValues evaluateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateAddTwoPoints(x1, y1, x2, y2));
		return eval;
	}
	
	public LexMultiValues evaluateRemoveTwoPoints(Point x1, Point x2) {
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateRemoveTwoPoints(x1, x2));
		return eval;
	}
	
	public LexMultiValues evaluateAddRemovePoints(Point x, Point y, Point z) {
		LexMultiValues eval = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			eval.add(functions.get(i).evaluateAddRemovePoints(x, y, z));
		return eval;
	}
	
	public LexMultiValues getValues(){
		LexMultiValues V = new LexMultiValues();
		for(int i = 0; i < functions.size(); i++)
			V.add(functions.get(i).getValue());
		return V;
	}
	public int size(){
		return functions.size();
	}
	
	public static void main(String[] args){
		String s = "0123456";
		for(int i = 0; i < s.length(); i++){
			System.out.println(i + ":" + s.charAt(i));
		}
	}
}
