package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util;

import java.util.ArrayList;

public class Node {
	int id;
	double lat;
	double lng;
	ArrayList<Node>adj;
	ArrayList<Double>w;
	Node()
	{
		adj = new ArrayList<Node>();
		w = new ArrayList<Double>();
	}
	public void addAdj(Node node,double d)
	{
		adj.add(node);
		w.add(d);
	}
}
