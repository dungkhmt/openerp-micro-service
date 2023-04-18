
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * start date: 07/11/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.utils;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

import java.util.*;

public class Utility {
	public static double euclideanDistance(Point p1, Point p2){
		return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(),2));
	}
	public static double euclideanDistance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
	public static int select(HashSet<Integer> S){
		Random R = new Random();
		int r = R.nextInt(S.size());
		int i = 0;
		for(int e : S){
			i++;
			if(i == r){
				return e;
			}
		}
		return 0;
	}
	
	public static Point[] scaleUp(Point[] P, int scaleSz){
		//TODO		
		Point[] t_P = new Point[P.length + scaleSz];
		System.arraycopy(P, 0, t_P, 0, P.length);
		return t_P;
	}
	public static double[] scaleUp(double[] d, int scaleSz){
		double[] t_d = new double[d.length + scaleSz];
		System.arraycopy(d, 0, t_d, 0, d.length);
		return t_d;
	}
	public static int[] scaleUp(int[] i, int scaleSz){
		int[] t_i = new int[i.length + scaleSz];
		System.arraycopy(i, 0, t_i, 0, i.length);
		return t_i;
	}
	public static double[][] scaleUp(double[][] d, int scaleSz){
		double[][] t_d = new double[d.length + scaleSz][d.length + scaleSz];
		for(int i = 0; i < d.length; i++)
			for(int j = 0; j < d.length; j++)
				t_d[i][j] = d[i][j];
		return t_d;
	}
	
}
