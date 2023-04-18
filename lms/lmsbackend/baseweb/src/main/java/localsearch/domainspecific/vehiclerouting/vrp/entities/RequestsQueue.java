package localsearch.domainspecific.vehiclerouting.vrp.entities;
import java.util.*;
public class RequestsQueue {
	private ArrayList<Request> queue;
	public RequestsQueue(){
		queue = new ArrayList<Request>();
	}
	public void add(Request r){
		queue.add(r);
	}
	public Request pop(){
		Request r = queue.get(0);
		queue.remove(0);
		return r;
	}
	public void clear(){
		queue.clear();
	}
	public int size(){ return queue.size();}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
