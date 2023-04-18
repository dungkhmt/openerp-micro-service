package localsearch.domainspecific.vehiclerouting.apps.sharedaride;

import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.utils.ScannerInput;

public class Info {
	int nbVehicle;
	int nRequest;
    int n;
    double [] earliest;
    double [] lastest;
    double[][] cost;
    double [] serviceTime;
    Point [] p;
    int [] type;
    int INF_TIME = 100000000;
    public static int V = 50;
    Info(String filename)
    {
    	ScannerInput fi = new ScannerInput(filename);
    	nRequest = fi.readInt();
    	nbVehicle = fi.readInt();
    	n = 2*nRequest + nbVehicle*2+1;
    	earliest = new double[n];
		lastest = new double[n];
		cost = new double[n][n];
		serviceTime = new double[n];
		p = new Point[n];
		type = new int[n];
		for(int i=0;i<nRequest; ++i)
		{
//r.id+" "+type+" "+i*2+" "+r.earlyPickupTime+" "+r.latePickupTime+" "+(i*2+1)+" "+r.earlyDeliveryTime+" "+r.lateDeliveryTime
			fi.readInt();
			type[i*2+1] = fi.readInt();
			fi.readInt();
			p[i*2+1] = new Point(fi.readDouble(),fi.readDouble());
			earliest[i*2+1] = fi.readInt();
			lastest[i*2+1] = fi.readInt();
			serviceTime[i*2+1] = fi.readInt();
			fi.readInt();
			p[i*2+2] = new Point(fi.readDouble(),fi.readDouble());
			earliest[i*2+2] = fi.readInt();
			lastest[i*2+2] = fi.readInt();
			serviceTime[i*2+2] = fi.readInt();
		}
		int np = nRequest*2;
		for(int i= 0 ; i < nbVehicle; ++i)
		{
			fi.readInt();
			double lat = fi.readDouble();
			double lng = fi.readDouble();
			earliest[i+np+1] = earliest[i+np+nbVehicle+1] = 0;
			lastest[i+np+1] = lastest[i+np+nbVehicle+1] = INF_TIME;
			serviceTime[i+np+1] = serviceTime[i+np+nbVehicle+1] = 0;
			p[i+np+1] = new Point(lat, lng);
			p[i+np+1+nbVehicle] = new Point(lat,lng);
		}
		for (int i = 1; i <n; i++) {
			for (int j = 1; j < n; j++) {
				double dis = fi.readDouble();
				cost[i][j] = (int)(dis);
			}
		}
		double XBase =  20.987610;
		double YBase =  105.809916;
		double maxDx = 21.039575 - XBase;
		double maxDy = 105.871547 - YBase;
		
		double minX = 10000;
		double minY = 10000;
		double maxX = -1000;
		double maxY = -1000;
		for(int i=1; i<n; ++i)
		{
			minX = Math.min(minX, p[i].getX());
			maxX = Math.max(maxX, p[i].getX());
			minY = Math.min(minY, p[i].getY());
			maxY = Math.max(maxY, p[i].getY());
		}
		double nDx = maxX  - minX;
		double nDy = maxY - minY;
		for(int i = 1; i < n; ++i)
		{
			double nX = (p[i].getX() - minX)*maxDx/nDx+ XBase;
			double nY = YBase + (p[i].getY() - minY)*maxDy/nDy;
			System.out.println(nX+"  "+nY);
			p[i] = new Point(nX, nY);
		}
		fi.close();
    }

  
}
