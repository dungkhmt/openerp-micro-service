package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

public class Graph {
	ArrayList<Node> nodes;
	int n;
	Graph(String file)
	{
		nodes = new ArrayList<Node>();
		nodes.add(null);
		try {
			Scanner in = new Scanner(new File(file));
			do{
				Node node = new Node();
				node.id = in.nextInt();
				if(node.id==-1)
					break;
				node.lat = in.nextDouble();
				node.lng = in.nextDouble();
				nodes.add(node);
			}while(in.hasNextInt());
			while(in.hasNextInt())
			{
				int id1 = in.nextInt();
				if(id1==-1)
					break;
				int id2 = in.nextInt();
				double dd = in.nextDouble();
				nodes.get(id1).addAdj(nodes.get(id2),dd);
				nodes.get(id2).addAdj(nodes.get(id1),dd);
			}
			n = nodes.size()-1;
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Cant open file "+file);
			System.exit(0);
		}
	}
	double [] dijkstra(int id)
	{
		TreeSet<Point2D>st = new TreeSet<Point2D>(new Poit2DCompare());
		double [] d = new double[n+1];
		boolean [] mark = new boolean[n+1];
		int i,j;

		for(i=1;i<=n;++i)
		{
			mark[i] = false;
			d[i] = Double.MAX_VALUE;
			st.add(new Point2D.Double(Double.MAX_VALUE,i));
		}
		st.add(new Point2D.Double(0,id));
		d[id] = 0;
		while(st.size()>0)
		{
			Point2D p = st.last();
			st.remove(p);
			int cid = (int)p.getY();
			double cd = p.getX();
			if(mark[cid])
				continue;
			mark[cid] = true;
			d[cid] = cd;
			Node cNode = nodes.get(cid);
			for(i=0;i<cNode.adj.size();++i)
			{
				Node cAdj = cNode.adj.get(i);
				double dd = cNode.w.get(i);
				if(mark[cAdj.id])
					continue;
				double newD = ( cd == Double.MAX_VALUE ? Double.MAX_VALUE : dd + cd);
				st.add(new Point2D.Double(newD,cAdj.id));
			}
		}
		return d;
	}
	
	public static void main(String []args)
	{
		Graph graph = new Graph("input.txt");
		double []d = graph.dijkstra(1);
		for(int i=1;i<=6;++i)
			System.out.println(i+"  "+d[i]);
	}
}
