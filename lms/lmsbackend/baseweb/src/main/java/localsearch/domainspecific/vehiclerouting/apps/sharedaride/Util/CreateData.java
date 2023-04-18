package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class CreateData {
	ArrayList<Request> listRequests;
	Graph graph;
	public CreateData() {
		listRequests = readRequest("input/ins_day_1_minSpd_5_maxSpd_60.txt");
		graph = new Graph("input/SanFranciscoRoad-connected-contracted-5-refine-50.txt");
	}
	public double [][] calDis(int []p)
	{
		int n = p.length;
		double [][] d = new double[n][n];
		for(int i=0;i<n;++i)
		{
			System.out.println(i);
			double [] dd = graph.dijkstra(p[i]);
			for(int j=0;j<n;++j)
				d[i][j] = dd[p[j]];
		}
		return d;
	}
	
	public ArrayList<Request> readRequest(String file)
	{
		ArrayList<Request> listrequest = new ArrayList<Request>();
		try {
			Scanner in = new Scanner(new File(file));
			in.nextLine();
			do{
//r.id  r.timeCall  r.pickupLocationID r.deliveryLocationID  r.earlyPickupTime  r.latePickupTime  r.earlyDeliveryTime  r.lateDeliveryTime  r.maxTravelDistance  r.maxNbStops
				Request r = new Request();
				r.id = in.nextInt();
				if(r.id==-1)
					break;
				in.nextInt();
				r.pickupId = in.nextInt();
				r.deliveryId = in.nextInt();
				r.earlyPickupTime = in.nextInt();
				r.latePickupTime = in.nextInt();
				r.earlyDeliveryTime = in.nextInt();
				r.lateDeliveryTime = in.nextInt();
				in.nextDouble();
				in.nextInt();
				listrequest.add(r);
			}while(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("cant open "+file +" file");
			System.exit(0);
		}
		return listrequest;
	}
	// create n different number < max 
	ArrayList<Integer> random(int n,int max)
	{
		ArrayList<Integer> re = new ArrayList<Integer>();
		while(n > 0)
		{
			int x = (int)(Math.random()*max);
			if(x>=max)
				x=0;
			if(re.contains(x))
				continue;
			re.add(x);
			n--;
		}
		return re;
	}
	public void creatData(String file,int nRequest,int nRoute)
	{
		try {
			PrintWriter out = new PrintWriter(new File(file));
			out.println(nRequest+"  "+nRoute);
			ArrayList<Integer> listIndRequest = random(nRequest, listRequests.size());
			int [] p = new int[2*nRequest+2*nRoute];
			for(int i=0;i<nRequest; ++i)
			{
				Request r = listRequests.get(listIndRequest.get(i));
				p[i*2] = r.pickupId;
				p[i*2+1] = r.deliveryId;
				int type = 0;
				if(Math.random() < 0.5)
					type = 1;
				out.println(r.id+" "+type+" "+i*2+" "+graph.nodes.get(r.pickupId).lat+" "+graph.nodes.get(r.pickupId).lng+" "+r.earlyPickupTime+" "+r.latePickupTime+" "+(1+(int)(Math.random()*600))+" "+(i*2+1)+" "+graph.nodes.get(r.deliveryId).lat+" "+graph.nodes.get(r.deliveryId).lng+" "+r.earlyDeliveryTime+" "+r.lateDeliveryTime+" "+(int)(Math.random()*600));
			}
			ArrayList<Integer> listIndLocation = random(nRoute, graph.n);
			for(int i = 0; i < nRoute; ++i)
			{
				int ind = listIndLocation.get(i)+1;
				p[i+nRequest*2+nRoute] = p[i+nRequest*2] = ind;
				double lat = graph.nodes.get(ind).lat;
				double lng  = graph.nodes.get(ind).lng;
				out.println((listIndLocation.get(i)+1)+"  "+lat+"  "+lng);
			}
			int n = nRequest*2+ nRoute*2;
			double [][]d = calDis(p);
			for(int i=0;i<n;++i)
			{
				for(int j=0;j<n;++j)
				{
					out.print(d[i][j]+" ");
				}
				out.println();
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Cant open file "+file);
			System.exit(0);
		}
		
	}
	public static void main(String []args)
	{
		CreateData cd = new CreateData();
		for(int i=1;i<=1;++i)
			cd.creatData("vrpData/n30r4_"+i+".txt", 30, 4);
	}
}
