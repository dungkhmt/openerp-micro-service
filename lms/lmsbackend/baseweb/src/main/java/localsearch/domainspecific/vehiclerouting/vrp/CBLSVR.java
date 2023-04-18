
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 10/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
public class CBLSVR {
	public static final int MAX_INT = 2147483647;
	public static final double EPSILON = 0.0000000001;
	public static final Point NULL_POINT = new Point(-1);
	public static boolean equal(double a, double b){
		return Math.abs(a-b) < EPSILON;
	}
}
