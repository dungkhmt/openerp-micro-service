package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util;

import java.awt.geom.Point2D;
import java.util.Comparator;

class Poit2DCompare implements Comparator<Point2D>{

	@Override
	public int compare(Point2D arg0, Point2D arg1) {
		// TODO Auto-generated method stub
		if(arg0.getX() < arg1.getX())
			return 1;
		if(arg0.getX()==arg1.getX())
			if(arg0.getY() < arg1.getY())
				return 1;
			else
				if(arg0.getY() == arg1.getY())
					return 0;
		return -1;
	}
	
}
