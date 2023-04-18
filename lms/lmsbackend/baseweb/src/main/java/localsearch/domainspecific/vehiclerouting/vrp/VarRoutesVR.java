
/*
 * authors: Pham Quang Dung (dungkhmt@gmail.com), Nguyen Thanh Hoang (thnbk56@gmail.com)
 * date: 19/08/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

// import sun.nio.cs.ext.ISCII91;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.enums.PointType;

/*
 * K: number of vehicles (also the number of routes)
 * n: number of clients
 * Indexing:
 * * N = 2*K+n: total number of points
 * * clients are numbered 1, 2, ..., n
 * * (n+i, n+i+K) are respectively the starting end terminating points of vehicle i, \forall i = 1,...,K
 * * n+i and n+i+K are mapped to the physical depot
 * Modelling solutions:
 * * next[i] is the next point of the point i, next[i] \in {1,...,N}, \forall i = 1,...,N
 * * prev[i] is the previous point of the point i, prev[i]\in {1,...,N}, \forall i=1,...,N
 * * route[i] is the route index of the point i, route[i]\in {1,...,K}, forall i = 1,...,N
 */
public class VarRoutesVR{

	private int[] next;
	private int[] prev;
	private int[] route;
	private int N;
	private int K;
	private int n;
	
	// store old values of next, prev, route
	private int[] old_next;
	private int[] old_prev;
	private int[] old_route;
	
	private int[] index;
	
	private PointType[] pointType;
	private ArrayList<Point> startingPoints;
	private ArrayList<Point> terminatingPoints;
	private ArrayList<Point> clientPoints;
	private ArrayList<Point> allPoints;
	private HashMap<Point, Integer> mPoint2Index;
	private int maxNbPoints;
	private VRManager mgr;
	
	private final int MAXP = 1000;
	
	public VarRoutesVR(VRManager mgr){
		maxNbPoints = MAXP;
		
		clientPoints = new ArrayList<Point>();
		startingPoints = new ArrayList<Point>();
		terminatingPoints = new ArrayList<Point>();
		allPoints = new ArrayList<Point>();
		N = 0;
		K = 0; n = 0;
		next = new int[maxNbPoints];
		prev = new int[maxNbPoints];
		route = new int[maxNbPoints];
		
		old_next = new int[maxNbPoints];
		old_prev = new int[maxNbPoints];
		old_route = new int[maxNbPoints];
		
		index = new int[maxNbPoints];
		pointType = new PointType[maxNbPoints];
		
		mPoint2Index = new HashMap<Point, Integer>();
		
		this.mgr = mgr;
		mgr.post(this);
	}
	
	private void scaleUp() {
		maxNbPoints += MAXP;
		int[] _next = new int[maxNbPoints];
		int[] _prev = new int[maxNbPoints];
		int[] _route = new int[maxNbPoints];
		int[] _index = new int[maxNbPoints];
		int[] _old_next = new int[maxNbPoints];
		int[] _old_prev = new int[maxNbPoints];
		int[] _old_route = new int[maxNbPoints];
		PointType[] _pointType = new PointType[maxNbPoints];
		System.arraycopy(next, 0, _next, 0, next.length);
		System.arraycopy(prev, 0, _prev, 0, next.length);
		System.arraycopy(route, 0, _route, 0, next.length);
		System.arraycopy(index, 0, _index, 0, next.length);
		System.arraycopy(old_next, 0, _old_next, 0, next.length);
		System.arraycopy(old_prev, 0, _old_prev, 0, next.length);
		System.arraycopy(old_route, 0, _old_route, 0, next.length);
		System.arraycopy(pointType, 0, _pointType, 0, pointType.length);
		next = _next;
		prev = _prev;
		route = _route;
		index = _index;
		old_next = _old_next;
		old_prev = _old_prev;
		old_route = _old_route;
		pointType = _pointType;
	}
	
	public void addRoute(Point sp, Point tp){
		if (N + 2 > maxNbPoints) {
			scaleUp();
		}
		
		K++;
		allPoints.add(sp);
		startingPoints.add(sp);
		mPoint2Index.put(sp, N++);
		pointType[N - 1] = PointType.STARTING_ROUTE;
		
		allPoints.add(tp);
		terminatingPoints.add(tp);
		mPoint2Index.put(tp, N++);
		pointType[N - 1] = PointType.TERMINATING_ROUTE;
		
		next[N - 2] = N - 1;
        prev[N - 1] = N - 2;
        prev[N - 2] = next[N - 1] = Constants.NULL_POINT;
        route[N - 2] = route[N - 1] = K;
        old_next[N - 2] = old_prev[N - 2] = old_route[N - 2] = Constants.NULL_POINT;
        old_next[N - 1] = old_prev[N - 1] = old_route[N - 1] = Constants.NULL_POINT;
        update(K);
	}
	
	public void addClientPoint(Point p){
		if(mPoint2Index.get(p) != null) return;
		if (N + 1 > maxNbPoints) {
			scaleUp();
		}
		allPoints.add(p);
		clientPoints.add(p);
		mPoint2Index.put(p, N++);
		pointType[N - 1] = PointType.CLIENT;
		n++;
		
		next[N - 1] = prev[N - 1] = index[N - 1] = route[N - 1] = Constants.NULL_POINT;
		old_next[N - 1] = old_prev[N - 1] = old_route[N - 1] = Constants.NULL_POINT;
	}
	
	public ArrayList<Point> getAllPoints(){
		return allPoints;
	}
	
	public ArrayList<Point> getClientPoints() {
		return clientPoints;
	}
	
	public ArrayList<Point> getStartingPoints() {
		return startingPoints;
	}
	
	public ArrayList<Point> getTerminatingPoints() {
		return terminatingPoints;
	}
	
	public void setValue(ValueRoutesVR val){
		copySolution();
		for (Point p : allPoints) {
			int x = getIndex(p);
			if (val.next(p) != null) {
				next[x] = getIndex(val.next(p));
			}
			if (val.prev(p) != null) {
				prev[x] = getIndex(val.prev(p));
			}
			route[x] = val.route(p);
		}	
		for(int k= 1; k <= getNbRoutes(); k++){
			update(k);
		}
		
		mgr.initPropagation();
	}
	
	public String toString(){
		String s = "";
		for(int k = 1; k <= K; k++){
			s += "route[" + k + "] = ";
			Point x = getStartingPointOfRoute(k);
			while(x != getTerminatingPointOfRoute(k)){
				s = s + x.getID() + " " + " -> ";
				x = next(x);
			}
			s = s + x.getID() + "\n";
		}
		return s;
	}
	public String routeString(int k){
		String s = "";
		for(Point p = startPoint(k); p != endPoint(k); p = next(p)){
			s += p.ID + " -> ";
		}
		s += endPoint(k).ID;
		return s;
	}
	// return the number of points
	public int getTotalNbPoints(){
		return N;
	}
	public int getNbClients(){
		return n;
	}
	public int getNbRoutes(){
		return K;
	}
	
	public int getIndex(Point p) {
		if( mPoint2Index.get(p) != null) return mPoint2Index.get(p);
		return Constants.NULL_POINT;
	}
	public Point startPoint(int k){
		return getStartingPointOfRoute(k);
	}
	public Point endPoint(int k){
		return getTerminatingPointOfRoute(k);
	}
	public Point getStartingPointOfRoute(int k){
		return (k <= 0 || k > K) ? null : startingPoints.get(k-1);
	}
	
	public Point getTerminatingPointOfRoute(int k){
		return (k <= 0 || k > K) ? null : terminatingPoints.get(k-1);
	}
	
	public VRManager getVRManager(){
		return this.mgr;
	}
	
	// add the point o to the end of the route k
    private void addPoint2Route(int k, int u) {
        next[prev[getIndex(getTerminatingPointOfRoute(k))]] = u;
        prev[u] = prev[getIndex(getTerminatingPointOfRoute(k))];
        next[u] = getIndex(getTerminatingPointOfRoute(k));
        prev[getIndex(getTerminatingPointOfRoute(k))] = u;
        route[u] = k;
    }
    
    // remove the point u from its current route
    private void removePointFromRoute(int u) {
        next[prev[u]] = next[u];
        prev[next[u]] = prev[u];
    }

    // reverse the direction of path from s to t on their route
    private void reverse(int s, int t) {
        while (s != t) {
            int tmp = next[s];
            next[s] = prev[s];
            prev[s] = tmp;
            s = next[s];
        }
        int tmp = next[s];
        next[s] = prev[s];
        prev[s] = tmp;
    }

    private void update(int k) {
    	int s = getIndex(getStartingPointOfRoute(k));
    	int t = getIndex(getTerminatingPointOfRoute(k));
    	index[s] = 0;
    	for (int x = s; x != t; x = next[x]) {
    		index[next[x]] = index[x] + 1;
    	}
    }
    
    public Point next(Point x) {
    	int idx = getIndex(x);
    	if(idx == Constants.NULL_POINT) return null;
    	return (next[idx] == Constants.NULL_POINT) ? null : allPoints.get(next[getIndex(x)]);
    }
    
    public Point prev(Point x) {
    	int idx = getIndex(x);
    	if(idx == Constants.NULL_POINT) return null;
    	return (prev[idx] == Constants.NULL_POINT) ? null : allPoints.get(prev[getIndex(x)]);
    }
    
    public int route(Point x) {
    	//System.out.println(name() + "::route of point " + x.ID);
    	if(getIndex(x) == Constants.NULL_POINT) return Constants.NULL_POINT;
    	return route[getIndex(x)];
    }
    
    public int index(Point x) {
    	if(getIndex(x) == Constants.NULL_POINT) return Constants.NULL_POINT;
    	return index[getIndex(x)];
    }
    
    public Point oldNext(Point x) {
    	int idx = getIndex(x);
    	return (old_next[idx] == Constants.NULL_POINT) ? null : allPoints.get(old_next[getIndex(x)]);
    }
    
    public Point oldPrev(Point x) {
    	int idx = getIndex(x);
    	return (old_prev[idx] == Constants.NULL_POINT) ? null : allPoints.get(old_prev[getIndex(x)]);
    }
    
    public int oldRoute(Point x) {
    	if(getIndex(x) == Constants.NULL_POINT) return Constants.NULL_POINT;
    	return old_route[getIndex(x)];
    }
    
	public String name(){
		return "VarRoutesVR";
	}
	
	public boolean isBefore(Point x, Point y) {
		int idx = getIndex(x);
		int idy = getIndex(y);
		return route[idx] == route[idy] && index[idx] < index[idy];
	}
	public ArrayList<Point> collectCurrentClientPointsOnRoute(){
		ArrayList<Point> L = new ArrayList<Point>();
		for(int k = 1; k <= this.getNbRoutes(); k++){
			for(Point p = next(startPoint(k)); p != endPoint(k); p = next(p)){
				L.add(p);
			}
		}
		return L;
	}
	public ArrayList<Point> collectCurrentClientAndStartPointsOnRoute(){
		ArrayList<Point> L = new ArrayList<Point>();
		for(int k = 1; k <= this.getNbRoutes(); k++){
			for(Point p = startPoint(k); p != endPoint(k); p = next(p)){
				L.add(p);
			}
		}
		return L;
	}
	public ArrayList<Point> collectCurrentPointsOnRoute(){
		ArrayList<Point> L = new ArrayList<Point>();
		for(int k = 1; k <= this.getNbRoutes(); k++){
			for(Point p = startPoint(k); p != endPoint(k); p = next(p)){
				L.add(p);
			}
			L.add(endPoint(k));
		}
		return L;
	}
	private void initRandom(){
		
		initStartingTerminatingPoints();
		
		//System.out.println(name() + "::setRandom, add points to a random route");
		Random rand = new Random();
        for (int i = 0; i < n; i++) {
        	addPoint2Route(rand.nextInt(K) + 1, getIndex(clientPoints.get(i)));
        }
        copySolution();
        
        //System.out.println(name() + "::setRandom, update route");
        
        for (int i = 1; i <= K; i++) {
        	update(i);
        }
//		Random rand = new Random();
//		int k = rand.nextInt(10) + 1;
//		for (int i = 0; i < k; i++) {
//			int idx = rand.nextInt(N);
//			int idy = rand.nextInt(N);
//			while (!checkPerformTwoPointsMove(allPoints.get(idx), allPoints.get(idy))) {
//				idx = rand.nextInt(N);
//				idy = rand.nextInt(N);
//			}
//			mgr.performTwoPointsMove(allPoints.get(idx), allPoints.get(idy));
//		}
	}
	
	private void initStartingTerminatingPoints(){
		for (int i = 1; i <= K; i++) {
			int ids = getIndex(getStartingPointOfRoute(i));
			int idt = getIndex(getTerminatingPointOfRoute(i));
            next[ids] = idt;
            prev[idt] = ids;
            prev[ids] = next[idt] = Constants.NULL_POINT;
            route[ids] = route[idt] = i;
        }
	}
	
	public void initSequential(){
		initStartingTerminatingPoints();
		Random rand = new Random();
		int nbClientsPerRoute = n/K;
		int client = 0;	
		for(int k = 1; k < K; k++){
			for(int j = 1; j <= nbClientsPerRoute; j++) {
				addPoint2Route(k, getIndex(clientPoints.get(client++)));
			}
		}
		while(client < n){
			addPoint2Route(K, getIndex(clientPoints.get(client++)));
		}
        
        copySolution();
  
        for (int i = 1; i <= K; i++) {
        	update(i);
        }
        mgr.initPropagation();
	}
	
	
	public void setRandom(){
		initRandom();
		mgr.initPropagation();
	}
	public void initTimeWindowRandom(HashMap<Point,Point>clientpair){
		
		initStartingTerminatingPoints();
		
		Random rand = new Random();
        for (int i = 0; i < n; i++) {
        	Point p = clientPoints.get(i);
        	if(clientpair.containsKey(p))
        	{
        		int k = rand.nextInt(K) + 1;
        		addPoint2Route(k, getIndex(p));
        		addPoint2Route(k, getIndex(clientpair.get(p)));
        	}
        	
        }
        copySolution();       
        for (int i = 1; i <= K; i++) {
        	update(i);
        }
	}
	public void setTimeWindow(HashMap<Point,Point>clientpair)
	{
		initTimeWindowRandom(clientpair);
		mgr.initPropagation();
	}
	
	private void copySolution() {
		System.arraycopy(next, 0, old_next, 0, next.length);
        System.arraycopy(prev, 0, old_prev, 0, prev.length);
        System.arraycopy(route, 0, old_route, 0, route.length);
	}
	
	public boolean isStartingPoint(Point p){
		int v = getIndex(p);
		return v >= 0 && pointType[v] == PointType.STARTING_ROUTE;
	}
	
	public boolean isTerminatingPoint(Point p){
		int v = getIndex(p);
		return v >= 0 && pointType[v] == PointType.TERMINATING_ROUTE;
	}
	
	public boolean isClientPoint(Point p){
		int v = getIndex(p);
		return v >= 0 && pointType[v] == PointType.CLIENT;
	}
	
    public boolean checkPerformTwoOptMoveOneRoute(Point x, Point y){
    	// x and y are in the same route, x is before y
    	// remove (x,next[x]) and (y,next[y])
    	// add (x,y) and (next[x],next[y]), reverse path from y to next[x]
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	//if(idx != idy) return false;
    	if(!isBefore(x, y)) return false;
    	if(y == endPoint(route(y))) return false;    		
    	return true;
    	
    }
    public void performTwoOptMoveOneRoute(Point x, Point y){
    	// x and y are in the same route, x is before y
    	// remove (x,next[x]) and (y,next[y])
    	// add (x,y) and (next[x],next[y]), reverse path from y to next[x]
    	if(!checkPerformTwoOptMoveOneRoute(x,y)){
    		System.out.println(name() + ":: Error performTwoOptMoveOneRoute: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMoveOneRoute(idx, idy);
    }
    private void performTwoOptMoveOneRoute(int x, int y){
    	// x and y are in the same route, x is before y
    	// remove (x,next[x]) and (y,next[y])
    	// add (x,y) and (next[x],next[y]), reverse path from y to next[x]
    	copySolution();
    	int nx = next[x];
    	int ny = next[y];
    	reverse(y,nx);
    	next[x] = y;
    	prev[y] = x;
    	next[nx] = ny;
    	prev[ny] = nx;
    	int rX = route[x];
    	update(rX); 
    }

    
    // move of type a [Groer et al., 2010]
    // move customer x to from route of x to route of y; insert x into the position between y and next[y]
    // x and y are not the depot
	public boolean checkPerformOnePointMove(Point x, Point y) {
		int idx = getIndex(x);
		int idy = getIndex(y);
		boolean ok1 = isClientPoint(x);
		boolean ok2 = (isStartingPoint(y) || isClientPoint(y));
		boolean ok3 =  x != y;
		boolean ok4 = route[idx] != Constants.NULL_POINT && route[idy] != Constants.NULL_POINT && next(y)!= x;
		return ok1 && ok2 && ok3 && ok4;
	}
	
	public void performOnePointMove(Point x, Point y){
		if (!checkPerformOnePointMove(x, y)) {
    		System.out.println(name() + ":: Error performOnePointMove: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
		int idx = getIndex(x);
		int idy = getIndex(y);
		performOnePointMove(idx, idy);
	}
	
    private void performOnePointMove(int x, int y){
    	copySolution();
    	move(x, y);
    	HashSet<Integer> oldR = new HashSet<Integer>();
    	oldR.add(old_route[x]); 
    	oldR.add(old_route[y]);
    	for (int r : oldR) {
    		update(r);
    	}
    }
    // move of type b [Groer et al., 2010]
    // x and y are on the same route and are not the depots, y locates before x on the route
    // remove (prev[x],x) and (x,next[x]) and (prev[y], y) and (y, next(y)
    // insert (x,prev[y]) and (next[y],x) and (next[x],y) and (y, prev[x])
    public boolean checkPerformTwoPointsMove(Point x, Point y) {
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	return (isClientPoint(x) && isClientPoint(y) && x != y && route[idx] != Constants.NULL_POINT && route[idy] != Constants.NULL_POINT);
    }
    
    public void performTwoPointsMove(Point x, Point y){
    	if (!checkPerformTwoPointsMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoPointsMove: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoPointsMove(idx, idy);
    }
    
    private void performTwoPointsMove(int x, int y){
    	if (next[x] == y) {
    		performTwoPointsMove(y, x, prev[x], prev[x]);
    	} else if (next[y] == x) {
    		performTwoPointsMove(x, y, prev[y], prev[y]);
    	} else {
    		performTwoPointsMove(x, y, prev[y], prev[x]);
    	}
    }

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[x],next(y))
    public boolean checkPerformTwoOptMove(Point x, Point y) {
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	return (isClientPoint(x) && isClientPoint(y) && route[idx] != route[idy] && route[idx] != Constants.NULL_POINT && route[idy] != Constants.NULL_POINT);
    	//return (isClientPoint(x) && isClientPoint(y) && route[idx] != Constants.NULL_POINT && route[idy] != Constants.NULL_POINT);
    }
    
    public void performTwoOptMove1(Point x, Point y){
    	if (!checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoOptMove1: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMove1(idx, idy);
    }
    
    private void performTwoOptMove1(int x, int y){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y]; 
        int startRX = getIndex(getStartingPointOfRoute(route[x]));
        int startRY = getIndex(getStartingPointOfRoute(route[y]));
        int endRX = getIndex(getTerminatingPointOfRoute(route[x]));
        int endRY = getIndex(getTerminatingPointOfRoute(route[y]));

        reverse(y, startRY);
        reverse(endRX, nextX);
        next[nextX] = nextY;
        prev[nextY] = nextX;
        next[x] = y;
        prev[y] = x;

        next[startRY] = next[endRX];
        prev[next[endRX]] = startRY;
        prev[endRX] = prev[startRY];
        next[prev[startRY]] = endRX;
        next[endRX] = prev[startRY] = Constants.NULL_POINT;

        int rX = route[x];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[x],next(y))
    public void performTwoOptMove2(Point x, Point y){
    	if (!checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoOptMove2: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMove2(idx, idy);
    }
    
    private void performTwoOptMove2(int x, int y){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int startRX = getIndex(getStartingPointOfRoute(route[x]));
        int startRY = getIndex(getStartingPointOfRoute(route[y]));
        int endRX = getIndex(getTerminatingPointOfRoute(route[x]));
        int endRY = getIndex(getTerminatingPointOfRoute(route[y]));

        reverse(x, startRX);
        reverse(endRX, nextX);
        next[nextX] = nextY;
        prev[nextY] = nextX;
        next[y] = x;
        prev[x] = y;

        next[startRX] = next[startRY];
        prev[next[startRY]] = startRX;
        prev[endRX] = prev[startRX];
        next[prev[startRX]] = endRX;
        next[startRY] = next[endRX];
        prev[next[endRX]] = startRY;
        prev[startRX] = next[endRX] = Constants.NULL_POINT;

        int rX = route[x];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,y) and (next[y],next(x))
    public void performTwoOptMove3(Point x, Point y){
    	if (!checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoOptMove3: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMove3(idx, idy);
    }
    	
    public void performTwoOptMove3(int x, int y){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int startRX = getIndex(getStartingPointOfRoute(route[x]));
        int startRY = getIndex(getStartingPointOfRoute(route[y]));
        int endRX = getIndex(getTerminatingPointOfRoute(route[x]));
        int endRY = getIndex(getTerminatingPointOfRoute(route[y]));

        reverse(y, startRY);
        reverse(endRY, nextY);
        next[nextY] = nextX;
        prev[nextX] = nextY;
        next[x] = y;
        prev[y] = x;

        prev[endRY] = prev[endRX];
        next[prev[endRX]] = endRY;
        prev[endRX] = prev[startRY];
        next[prev[startRY]] = endRX;
        next[startRY] = next[endRY];
        prev[next[endRY]] = startRY;
        prev[startRY] = next[endRY] = Constants.NULL_POINT;

        int rX = route[x];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (y,x) and (next[y],next(x))
    public void performTwoOptMove4(Point x, Point y){
    	if (!checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoOptMove4: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMove4(idx, idy);
    }
    
    private void performTwoOptMove4(int x, int y){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int startRX = getIndex(getStartingPointOfRoute(route[x]));
        int startRY = getIndex(getStartingPointOfRoute(route[y]));
        int endRX = getIndex(getTerminatingPointOfRoute(route[x]));
        int endRY = getIndex(getTerminatingPointOfRoute(route[y]));
        
        reverse(x, startRX);
        reverse(endRY, nextY);
        next[nextY] = nextX;
        prev[nextX] = nextY;
        next[y] = x;
        prev[x] = y;

        prev[endRY] = prev[endRX];
        next[prev[endRX]] = endRY;
        next[startRX] = next[startRY];
        prev[next[startRY]] = startRX;
        prev[endRX] = prev[startRX];
        next[prev[startRX]] = endRX;
        next[startRY] = next[endRY];
        prev[next[endRY]] = startRY;
        prev[startRX] = next[endRY] = Constants.NULL_POINT;

        int rX = route[x];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (y,next[x])
    public void performTwoOptMove5(Point x, Point y){
    	if (!checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoOptMove5: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMove5(idx, idy);
    }
    
    private void performTwoOptMove5(int x, int y){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int endRX = getIndex(getTerminatingPointOfRoute(route[x]));
        int endRY = getIndex(getTerminatingPointOfRoute(route[y]));

        next[x] = nextY;
        prev[nextY] = x;
        next[y] = nextX;
        prev[nextX] = y;

        int tmp = prev[endRX];
        prev[endRX] = prev[endRY];
        prev[endRY] = tmp;
        next[prev[endRX]] = endRX;
        next[prev[endRY]] = endRY;

        int rX = route[x];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (y,next[x])
    public void performTwoOptMove6(Point x, Point y){
    	if (!checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoOptMove6: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMove6(idx, idy);
    }
    
    private void performTwoOptMove6(int x, int y){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int startRX = getIndex(getStartingPointOfRoute(route[x]));
        int startRY = getIndex(getStartingPointOfRoute(route[y]));
        int endRX = getIndex(getTerminatingPointOfRoute(route[x]));
        int endRY = getIndex(getTerminatingPointOfRoute(route[y]));

        reverse(x, startRX);
        reverse(endRY, nextY);
        next[y] = nextX;
        prev[nextX] = y;
        next[nextY] = x;
        prev[x] = nextY;

        prev[endRY] = prev[endRX];
        next[prev[endRX]] = endRY;
        next[startRX] = next[endRY];
        prev[next[endRY]] = startRX;
        prev[endRX] = prev[startRX];
        next[prev[startRX]] = endRX;
        next[endRY] = prev[startRX] = Constants.NULL_POINT;

        int rX = route[x];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (x,next[y]) and (next[x],y)
    public void performTwoOptMove7(Point x, Point y){
    	if (!checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoOptMove7: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMove7(idx, idy);
    }
    
    private void performTwoOptMove7(int x, int y){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int startRX = getIndex(getStartingPointOfRoute(route[x]));
        int startRY = getIndex(getStartingPointOfRoute(route[y]));
        int endRX = getIndex(getTerminatingPointOfRoute(route[x]));
        int endRY = getIndex(getTerminatingPointOfRoute(route[y]));

        reverse(endRX, nextX);
        reverse(y, startRY);
        next[x] = nextY;
        prev[nextY] = x;
        next[nextX] = y;
        prev[y] = nextX;

        prev[endRX] = prev[endRY];
        next[prev[endRY]] = endRX;
        next[startRY] = next[endRX];
        prev[next[endRX]] = startRY;
        prev[endRY] = prev[startRY];
        next[prev[startRY]] = endRY;
        next[endRX] = prev[startRY] = Constants.NULL_POINT;

        int rX = route[x];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type c [Groer et al., 2010]
    // x and y are on different routes and are not depots
    // remove (x,next[x]) and (y,next[y])
    // insert (next[y],x) and (next[x],y)
    public void performTwoOptMove8(Point x, Point y){
    	if (!checkPerformTwoOptMove(x, y)) {
    		System.out.println(name() + ":: Error performTwoOptMove8: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performTwoOptMove8(idx, idy);
    }
    
    private void performTwoOptMove8(int x, int y){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int startRX = getIndex(getStartingPointOfRoute(route[x]));
        int startRY = getIndex(getStartingPointOfRoute(route[y]));
        int endRX = getIndex(getTerminatingPointOfRoute(route[x]));
        int endRY = getIndex(getTerminatingPointOfRoute(route[y]));

        reverse(endRX, startRX);
        reverse(endRY, startRY);
        next[nextX] = y;
        prev[y] = nextX;
        next[nextY] = x;
        prev[x] = nextY;

        next[startRX] = next[endRY];
        prev[next[endRY]] = startRX;
        next[startRY] = next[endRX];
        prev[next[endRX]] = startRY;
        prev[endRX] = prev[startRX];
        next[prev[startRX]] = endRX;
        prev[endRY] = prev[startRY];
        next[prev[startRY]] = endRY;
        next[endRX] = next[endRY] = Constants.NULL_POINT;
        prev[startRX] = prev[startRY] = Constants.NULL_POINT;

        int rX = route[x];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x1) and (x2, next[y]) and (prev[x1], next[x2])
    public boolean checkPerformOrOptMove(Point x1, Point x2, Point y) {
    	int idx1 = getIndex(x1);
    	int idx2 = getIndex(x2);
    	int idy = getIndex(y);
    	if (!isClientPoint(x1) || route[idx1] == Constants.NULL_POINT) {
    		return false;
    	}
    	if (!isClientPoint(x2) || route[idx2] == Constants.NULL_POINT) {
    		return false;
    	}
    	if (isTerminatingPoint(y) || route[idy] == Constants.NULL_POINT) {
    		return false;
    	}
    	return (route[idx1] == route[idx2] && index[idx1] < index[idx2] && route[idx1] != route[idy]);
    }
    
    public void performOrOptMove1(Point x1, Point x2, Point y){
    	if (!checkPerformOrOptMove(x1, x2, y)) {
    		System.out.println(name() + ":: Error performOrOptMove1: " + x1 + " " + x2 + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx1 = getIndex(x1);
    	int idx2 = getIndex(x2);
    	int idy = getIndex(y);
    	performOrOptMove1(idx1, idx2, idy);
    }
    
    private void performOrOptMove1(int x1, int x2, int y){
    	copySolution();
    	
        int prevX1 = prev[x1];
        int nextX2 = next[x2];
        int nextY = next[y];

        next[prevX1] = nextX2;
        prev[nextX2] = prevX1;
        next[x2] = nextY;
        prev[nextY] = x2;
        next[y] = x1;
        prev[x1] = y;

        int rX = route[x1];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }

    // move of type d [Groer et al., 2010]
    // move the sequence <x1,next[x1],..., prev[x2], x2> of length len to the route containing y
    // remove (prev[x1],x1) and (x2,next[x2]), and (y,next[y])
    // add (y, x2) and (x1, next[y]) and (prev[x1], next[x2])
    public void performOrOptMove2(Point x1, Point x2, Point y){
    	if (!checkPerformOrOptMove(x1, x2, y)) {
    		System.out.println(name() + ":: Error performOrOptMove2: " + x1 + " " + x2 + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx1 = getIndex(x1);
    	int idx2 = getIndex(x2);
    	int idy = getIndex(y);
    	performOrOptMove2(idx1, idx2, idy);
    }
    
    private void performOrOptMove2(int x1, int x2, int y){
    	copySolution();
    	
        int prevX1 = prev[x1];
        int nextX2 = next[x2];
        int nextY = next[y];

        reverse(x2, x1);
        next[prevX1] = nextX2;
        prev[nextX2] = prevX1;
        next[x1] = nextY;
        prev[nextY] = x1;
        next[y] = x2;
        prev[x2] = y;

        int rX = route[x1];
        int rY = route[y];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	update(rX);
    	update(rY);
    }


    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,z) and (next[y], next[x]) and(y, next[z])
    public boolean checkPerformThreeOptMove(Point x, Point y, Point z) {
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	if (!isClientPoint(x)) {
    		return false;
    	}
    	if (!isClientPoint(z)) {
    		return false;
    	}
    	if (!isClientPoint(y)) {
    		return false;
    	}
    	//System.out.println(route[x] + " " + route[y] + " " + route[z] + " " + index[x] + " " + index[y] + " " + index[z]);
    	return route[idx] != Constants.NULL_POINT && route[idx] == route[idy] && route[idx] == route[idz] && index[idx] < index[idy] && index[idy] < index[idz]; 
    }
    
    public void performThreeOptMove1(Point x, Point y, Point z){
    	if (!checkPerformThreeOptMove(x, y, z)) {
    		System.out.println(name() + ":: Error performThreeOptMove1: " + x + " " + y + " " + z + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performThreeOptMove1(idx, idy, idz);
    }
    
    private void performThreeOptMove1(int x, int y, int z){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int nextZ = next[z];

        reverse(z, nextY);
        next[x] = z;
        prev[z] = x;
        next[nextY] = nextX;
        prev[nextX] = nextY;
        next[y] = nextZ;
        prev[nextZ] = y;
    	
        update(route[x]);
    }

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (z,x) and (next[x], next[y]) and(next[z],y)
    public void performThreeOptMove2(Point x, Point y, Point z){
    	if (!checkPerformThreeOptMove(x, y, z)) {
    		System.out.println(name() + ":: Error performThreeOptMove2: " + x + " " + y + " " + z + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performThreeOptMove2(idx, idy, idz);
    }
    
    private void performThreeOptMove2(int x, int y, int z){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int nextZ = next[z];
        int startR = getIndex(getStartingPointOfRoute(route[x]));
        int endR = getIndex(getTerminatingPointOfRoute(route[x]));

        reverse(endR, nextZ);
        reverse(x, startR);
        reverse(y, nextX);

        next[nextZ] = y;
        prev[y] = nextZ;
        next[nextX] = nextY;
        prev[nextY] = nextX;
        next[z] = x;
        prev[x] = z;
        next[startR] = next[endR];
        prev[next[endR]] = startR;
        prev[endR] = prev[startR];
        next[prev[startR]] = endR;
        next[endR] = prev[startR] = Constants.NULL_POINT;
        
        update(route[x]);
    }

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,y) and (next[x], z) and(next[y], next[z])
    public void performThreeOptMove3(Point x, Point y, Point z){
    	if (!checkPerformThreeOptMove(x, y, z)) {
    		System.out.println(name() + ":: Error performThreeOptMove3: " + x + " " + y + " " + z + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performThreeOptMove3(idx, idy, idz);
    }
    
    private void performThreeOptMove3(int x, int y, int z){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int nextZ = next[z];

        reverse(y, nextX);
        reverse(z, nextY);

        next[x] = y;
        prev[y] = x;
        next[nextX] = z;
        prev[z] = nextX;
        next[nextY] = nextZ;
        prev[nextZ] = nextY;
    	
        update(route[x]);
    }

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (y,x) and (z,next[x]) and(next[z], next[y])
    public void performThreeOptMove4(Point x, Point y, Point z){
    	if (!checkPerformThreeOptMove(x, y, z)) {
    		System.out.println(name() + ":: Error performThreeOptMove4: " + x + " " + y + " " + z + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performThreeOptMove4(idx, idy, idz);
    }
    
    private void performThreeOptMove4(int x, int y, int z){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int nextZ = next[z];
        int startR = getIndex(getStartingPointOfRoute(route[x]));
        int endR = getIndex(getTerminatingPointOfRoute(route[x]));

        reverse(endR, nextZ);
        reverse(x, startR);

        next[y] = x;
        prev[x] = y;
        next[z] = nextX;
        prev[nextX] = z;
        next[nextZ] = nextY;
        prev[nextY] = nextZ;
        next[startR] = next[endR];
        prev[next[endR]] = startR;
        prev[endR] = prev[startR];
        next[prev[startR]] = endR;
        next[endR] = prev[startR] = Constants.NULL_POINT;
        
        update(route[x]);
    }

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,next[x]) and(y, next[z])
    public void performThreeOptMove5(Point x, Point y, Point z){
    	if (!checkPerformThreeOptMove(x, y, z)) {
    		System.out.println(name() + ":: Error performThreeOptMove5: " + x + " " + y + " " + z + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performThreeOptMove5(idx, idy, idz);
    }
    
    private void performThreeOptMove5(int x, int y, int z){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int nextZ = next[z];

        next[x] = nextY;
        prev[nextY] = x;
        next[z] = nextX;
        prev[nextX] = z;
        next[y] = nextZ;
        prev[nextZ] = y;
    	
        update(route[x]);
    }

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (next[x],z) and(next[z],y)
    public void performThreeOptMove6(Point x, Point y, Point z){
    	if (!checkPerformThreeOptMove(x, y, z)) {
    		System.out.println(name() + ":: Error performThreeOptMove6: " + x + " " + y + " " + z + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performThreeOptMove6(idx, idy, idz);
    }
    
    private void performThreeOptMove6(int x, int y, int z){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int nextZ = next[z];
        int startR = getIndex(getStartingPointOfRoute(route[x]));
        int endR = getIndex(getTerminatingPointOfRoute(route[x]));

        reverse(endR, nextZ);
        reverse(y, nextX);
        reverse(z, nextY);
        reverse(x, startR);

        next[nextY] = x;
        prev[x] = nextY;
        next[nextX] = z;
        prev[z] = nextX;
        next[nextZ] = y;
        prev[y] = nextZ;
        next[startR] = next[endR];
        prev[next[endR]] = startR;
        prev[endR] = prev[startR];
        next[prev[startR]] = endR;
        next[endR] = prev[startR] = Constants.NULL_POINT;
        
        update(route[x]);
    }

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (x,next[y]) and (z,y) and(next[x], next[z])
    public void performThreeOptMove7(Point x, Point y, Point z){
    	if (!checkPerformThreeOptMove(x, y, z)) {
    		System.out.println(name() + ":: Error performThreeOptMove7: " + x + " " + y + " " + z + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performThreeOptMove7(idx, idy, idz);
    }
    
    private void performThreeOptMove7(int x, int y, int z){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int nextZ = next[z];

        reverse(y, nextX);

        next[x] = nextY;
        prev[nextY] = x;
        next[z] = y;
        prev[y] = z;
        next[nextX] = nextZ;
        prev[nextZ] = nextX;
    	
        update(route[x]);
    }

    // move of type e [Groer et al., 2010]
    // x, y, z are on the same route in that order (x is before y, y is before z)
    // remove (x, next[x]), (y, next[y]), and (z, next[z])
    // insert (next[y],x) and (y,z) and(next[z], next[x])
    public void performThreeOptMove8(Point x, Point y, Point z){
    	if (!checkPerformThreeOptMove(x, y, z)) {
    		System.out.println(name() + ":: Error performThreeOptMove8: " + x + " " + y + " " + z + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performThreeOptMove8(idx, idy, idz);
    }
    
    private void performThreeOptMove8(int x, int y, int z){
    	copySolution();
    	
        int nextX = next[x];
        int nextY = next[y];
        int nextZ = next[z];
        int startR = getIndex(getStartingPointOfRoute(route[x]));
        int endR = getIndex(getTerminatingPointOfRoute(route[x]));

        reverse(endR, nextZ);
        reverse(z, nextY);
        reverse(x, startR);

        next[nextY] = x;
        prev[x] = nextY;
        next[y] = z;
        prev[z] = y;
        next[nextZ] = nextX;
        prev[nextX] = nextZ;
        next[startR] = next[endR];
        prev[next[endR]] = startR;
        prev[endR] = prev[startR];
        next[prev[startR]] = endR;
        next[endR] = prev[startR] = Constants.NULL_POINT;
    	
        update(route[x]);
    }


    // move of type g [Groer et al., 2010]
    // x1 and y1 are on the same route, x1 is before y1
    // x2 and y2 are on the same route, x2 is before y2
    // remove (x1,next[x1]) and (y1, next[y1])
    // remove (x2, next[x2]) and (y2, next[y2])
    // insert (x1, next[x2]) and (y2, next[y1])
    // insert (x2, next[x1]) and (y1, next[y2])
    public boolean checkPerformCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
    	int idx1 = getIndex(x1);
    	int idy1 = getIndex(y1);
    	int idx2 = getIndex(x2);
    	int idy2 = getIndex(y2);
    	if (!isClientPoint(y1)) {
    		return false;
    	}
    	if (!isClientPoint(y2)) {
    		return false;
    	}
    	if (route[idx1] == route[idx2] || route[idx1] == Constants.NULL_POINT || route[idx2] == Constants.NULL_POINT) {
    		return false;
    	}
    	return (route[idx1] == route[idy1] && route[idx2] == route[idy2] && index[idx1] < index[idy1] && index[idx2] < index[idy2]);
    }
    
    public void performCrossExchangeMove(Point x1, Point y1, Point x2, Point y2){
    	if (!checkPerformCrossExchangeMove(x1, y1, x2, y2)) {
    		System.out.println(name() + ":: Error performCrossExchangeMove: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx1 = getIndex(x1);
    	int idy1 = getIndex(y1);
    	int idx2 = getIndex(x2);
    	int idy2 = getIndex(y2);
    	performCrossExchangeMove(idx1, idy1, idx2, idy2);
    }
    
    private void performCrossExchangeMove(int x1, int y1, int x2, int y2){
    	copySolution();
    	
        int nextX1 = next[x1];
        int nextY1 = next[y1];
        int nextX2 = next[x2];
        int nextY2 = next[y2];

        next[x1] = nextX2;
        prev[nextX2] = x1;
        next[x2] = nextX1;
        prev[nextX1] = x2;
        next[y1] = nextY2;
        prev[nextY2] = y1;
        next[y2] = nextY1;
        prev[nextY1] = y2;

        int rX = route[x1];
        int rY = route[x2];
        for (int u = getIndex(getStartingPointOfRoute(rX)); u != getIndex(getTerminatingPointOfRoute(rX)); u = next[u]) {
        	route[u] = rX;
        }
        for (int u = getIndex(getStartingPointOfRoute(rY)); u != getIndex(getTerminatingPointOfRoute(rY)); u = next[u]) {
        	route[u] = rY;
        }
    	
        update(rX);
        update(rY);
    }
    
    public boolean checkPerformTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
    	Point[] x = {x1, x2};
    	Point[] y = {y1, y2};
    	for (int i = 0; i < 2; i++) {
    		for (int j = 0; j < 2; j++) {
    			if (x[i] == y[j]) {
    				return false;
    			}
    		}
    		if (!isClientPoint(x[i]) || route[getIndex(x[i])] == Constants.NULL_POINT) {
    			return false;
    		}
    		if ((!isClientPoint(y[i]) && !isStartingPoint(y[i])) || route[getIndex(y[i])] == Constants.NULL_POINT) {
    			return false;
    		}
    	}
    	return x1 != x2;
    }  
    
    public boolean checkPerformThreePointsMove(Point x1, Point x2, Point x3, Point y1, Point y2, Point y3) {
    	Point[] x = {x1, x2, x3};
    	Point[] y = {y1, y2, y3};
    	for (int i = 0; i < 3; i++) {
    		for (int j = 0; j < 3; j++) {
    			if (x[i] == y[j]) {
    				return false;
    			}
    		}
    		for (int j = i + 1; j < 3; j++) {
    			if (x[i] == x[j]) {
    				return false;
    			}
    		}
    		if (!isClientPoint(x[i]) || route[getIndex(x[i])] == Constants.NULL_POINT) {
    			return false;
    		}
    		if ((!isClientPoint(y[i]) && !isStartingPoint(y[i])) || route[getIndex(y[i])] == Constants.NULL_POINT) {
    			return false;
    		}
    	}
    	return true;
    } 
    
    public boolean checkPerformFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1, Point y2, Point y3, Point y4) {
    	Point[] x = {x1, x2, x3, x4};
    	Point[] y = {y1, y2, y3, y4};
    	for (int i = 0; i < 4; i++) {
    		for (int j = 0; j < 4; j++) {
    			if (x[i] == y[j]) {
    				return false;
    			}
    		}
    		for (int j = i + 1; j < 4; j++) {
    			if (x[i] == x[j]) {
    				return false;
    			}
    		}
    		if (!isClientPoint(x[i]) || route[getIndex(x[i])] == Constants.NULL_POINT) {
    			return false;
    		}
    		if ((!isClientPoint(y[i]) && !isStartingPoint(y[i])) || route[getIndex(y[i])] == Constants.NULL_POINT) {
    			return false;
    		}
    	}
    	return true;
    } 
    
    private void move(int x, int y) {
    	if (route[x] != Constants.NULL_POINT) {
        	next[prev[x]] = next[x];
        	prev[next[x]] = prev[x];
    	}
    	route[x] = route[y];
    	next[x] = next[y];
    	prev[next[y]] = x;
    	prev[x] = y;
    	next[y] = x;
    }

	// remove x1, x2 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]	
    public void performTwoPointsMove(Point x1, Point x2, Point y1, Point y2){
    	if (!checkPerformTwoPointsMove(x1, x2, y1, y2)) {
    		System.out.println(name() + ":: Error performTwoPointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx1 = getIndex(x1);
    	int idy1 = getIndex(y1);
    	int idx2 = getIndex(x2);
    	int idy2 = getIndex(y2);
    	performTwoPointsMove(idx1, idx2, idy1, idy2);
    }
    
    private void performTwoPointsMove(int x1, int x2, int y1, int y2){
    	copySolution();
    	move(x2, y2);
    	move(x1, y1);
    	HashSet<Integer> oldR = new HashSet<Integer>();
    	oldR.add(old_route[x1]); 
    	oldR.add(old_route[y1]);
    	oldR.add(old_route[x2]); 
    	oldR.add(old_route[y2]);
    	for (int r : oldR) {
    		update(r);
    	}
    }
    
 // remove x1, x2, x3 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
    public void performThreePointsMove (Point x1, Point x2, Point x3, Point y1, Point y2, Point y3){
    	if (!checkPerformThreePointsMove(x1, x2, x3, y1, y2, y3)) {
    		System.out.println(name() + ":: Error performThreePointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3 + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx1 = getIndex(x1);
    	int idy1 = getIndex(y1);
    	int idx2 = getIndex(x2);
    	int idy2 = getIndex(y2);
    	int idx3 = getIndex(x3);
    	int idy3 = getIndex(y3);
    	performThreePointsMove(idx1, idx2, idx3, idy1, idy2, idy3);
    }
    
    private void performThreePointsMove(int x1, int x2, int x3, int y1, int y2, int y3){
    	copySolution();
    	move(x3, y3);
    	move(x2, y2);
    	move(x1, y1);
    	HashSet<Integer> oldR = new HashSet<Integer>();
    	oldR.add(old_route[x1]); 
    	oldR.add(old_route[y1]);
    	oldR.add(old_route[x2]); 
    	oldR.add(old_route[y2]);
    	oldR.add(old_route[x3]); 
    	oldR.add(old_route[y3]);
    	for (int r : oldR) {
    		update(r);
    	}
    }

	// remove x1, x2, x3, x4 from their current routes
	// re-insert x1 between y1 and next[y1]
	// re-insert x2 between y2 and next[y2]
	// re-insert x3 between y3 and next[y3]
	// re-insert x4 between y4 and next[y4]
    public void performFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1, Point y2, Point y3, Point y4){
    	if (!checkPerformFourPointsMove(x1, x2, x3, x4, y1, y2, y3, y4)) {
    		System.out.println(name() + ":: Error performFourPointsMove: " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " " + y3 + " " + x4 + " " + y4 + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx1 = getIndex(x1);
    	int idy1 = getIndex(y1);
    	int idx2 = getIndex(x2);
    	int idy2 = getIndex(y2);
    	int idx3 = getIndex(x3);
    	int idy3 = getIndex(y3);
    	int idx4 = getIndex(x4);
    	int idy4 = getIndex(y4);
    	performFourPointsMove(idx1, idx2, idx3, idx4, idy1, idy2, idy3, idy4);
    }
    
    private void performFourPointsMove(int x1, int x2, int x3, int x4, int y1, int y2, int y3, int y4){
    	copySolution();
    	move(x4, y4);
    	move(x3, y3);
    	move(x2, y2);
    	move(x1, y1);
    	HashSet<Integer> oldR = new HashSet<Integer>();
    	oldR.add(old_route[x1]); 
    	oldR.add(old_route[y1]);
    	oldR.add(old_route[x2]); 
    	oldR.add(old_route[y2]);
    	oldR.add(old_route[x3]); 
    	oldR.add(old_route[y3]);
    	oldR.add(old_route[x4]); 
    	oldR.add(old_route[y4]);
    	for (int r : oldR) {
    		update(r);
    	}
    }
    public boolean contains(Point x){
    	boolean ok = getIndex(x) != Constants.NULL_POINT;
    	if(!ok) return ok;
    	return route[getIndex(x)] != Constants.NULL_POINT;
    }
    public boolean checkPerformAddOnePoint(Point x, Point y) {
    	return (route[getIndex(x)] == Constants.NULL_POINT && isClientPoint(x) && (isClientPoint(y) || 
    			isStartingPoint(y)) && route[getIndex(y)] != Constants.NULL_POINT);
    }
    
    public boolean checkPerformAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
    	return (route[getIndex(x1)] == Constants.NULL_POINT && isClientPoint(x1) && (isClientPoint(y1) ||
    			isStartingPoint(y1)) && route[getIndex(y1)] != Constants.NULL_POINT
    			&& route[getIndex(x2)] == Constants.NULL_POINT && isClientPoint(x2) && (isClientPoint(y2) || 
    	    	isStartingPoint(y2)) && route[getIndex(y2)] != Constants.NULL_POINT
    	    	&& route[getIndex(y1)] == route[getIndex(y2)] && index[getIndex(y1)] <= index[getIndex(y2)]);
    }
    
    public void performAddOnePoint(Point x, Point y){
    	// add point x between y and next[y]
    	if (!checkPerformAddOnePoint(x, y)) {
    		System.out.println(name() + ":: Error performAddOneMove: " + x + " " + y + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	performAddOnePoint(idx, idy);
    }
    
    private void performAddOnePoint(int x, int y){
    	copySolution();
    	prev[next[y]] = x;
    	next[x] = next[y];
    	prev[x] = y;
    	next[y] = x;
    	route[x] = route[y];
    	update(route[x]);
    }
    
    private void performAddTwoPoint(int x1, int y1, int x2, int y2){
    	copySolution();
    	if(y1 != y2){
    		prev[next[y1]] = x1;
        	next[x1] = next[y1];
        	prev[x1] = y1;
        	next[y1] = x1;
        	route[x1] = route[y1];
        	
    		prev[next[y2]] = x2;
        	next[x2] = next[y2];
        	prev[x2] = y2;
        	next[y2] = x2;
        	route[x2] = route[y2];
    	}
    	else{
    		prev[next[y1]] = x2;
    		next[x2] = next[y1];
    		prev[x2] = x1;
    		next[x1] = x2;
    		prev[x1] = y1;
    		next[y1] = x1;
    		route[x1] = route[y1];
    		route[x2] = route[y1];
    	}
    	update(route[x1]);
    }
    
    public void performAddTwoPoints(Point x1, Point y1, Point x2, Point y2){
    	if (!checkPerformAddTwoPoints(x1, y1, x2, y2)) {
    		System.out.println(name() + ":: Error performAddTwoPoints: " + x1 + " " + y1 + " " + x2 + " " + y2 + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx1 = getIndex(x1);
    	int idy1 = getIndex(y1);
    	int idx2 = getIndex(x2);
    	int idy2 = getIndex(y2);
    	performAddTwoPoint(idx1, idy1, idx2, idy2);
    }
    
    public boolean checkPerformRemoveOnePoint(Point x) {
    	if(route[getIndex(x)] == Constants.NULL_POINT){
    		System.out.println("Null point");
    	}
    	if(!isClientPoint(x)){
    		System.out.println("not client point");
    	}
    	return (route[getIndex(x)] != Constants.NULL_POINT && isClientPoint(x));
    }
    
    public boolean checkPerformRemoveTwoPoints(Point x1, Point x2) {
    	return (route[getIndex(x1)] != Constants.NULL_POINT && isClientPoint(x1)
    			&& route[getIndex(x2)] != Constants.NULL_POINT && isClientPoint(x2)
    			&& index[getIndex(x1)] < index[getIndex(x2)]);
    }
    
    public void performRemoveOnePoint(Point x){
    	// remove x from its current route
    	if (!checkPerformRemoveOnePoint(x)) {
    		System.out.println(name() + ":: Error performRemoveOneMove: " + x + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	copySolution();
    	next[prev[idx]] = next[idx];
    	prev[next[idx]] = prev[idx];
    	next[idx] = prev[idx] = route[idx] = Constants.NULL_POINT;
    	update(old_route[idx]);
    	index[idx] = Constants.NULL_POINT;
    }
    
    public void performRemoveTwoPoints(Point x1, Point x2){
    	// remove x from its current route
    	if (!checkPerformRemoveTwoPoints(x1, x2)) {
    		System.out.println(name() + ":: Error performRemoveTwoPoints: " + x1 + " " + x2 + "\n" + toString());
    		System.exit(-1);
    	}
    	int idx1 = getIndex(x1);
    	int idx2 = getIndex(x2);
    	copySolution();
    	next[prev[idx1]] = next[idx1];
    	prev[next[idx1]] = prev[idx1];
    	next[idx1] = prev[idx1] = route[idx1] = Constants.NULL_POINT;
    	update(old_route[idx1]);
    	index[idx1] = Constants.NULL_POINT;
    	
    	next[prev[idx2]] = next[idx2];
    	prev[next[idx2]] = prev[idx2];
    	next[idx2] = prev[idx2] = route[idx2] = Constants.NULL_POINT;
    	update(old_route[idx2]);
    	index[idx2] = Constants.NULL_POINT;
    }
    
    public boolean checkPerformAddRemovePoints(Point x, Point y, Point z){
    	// x is not starting, or terminating points
    	// z is not a terminating point
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	if (route[idx] == Constants.NULL_POINT || isStartingPoint(x) || isTerminatingPoint(x)) {
    		return false;
    	}
    	if (route[idy] != Constants.NULL_POINT || route[idz] == Constants.NULL_POINT || isTerminatingPoint(z)) {
    		return false;
    	}
    	return x != z;
    }
    
    public void performAddRemovePoints(Point x, Point y, Point z){
    	//remove x from its current route
    	// add y between z and next[z]
    	//TODO by HoangNT
    	if(!checkPerformAddRemovePoints(x, y, z)){
    		System.out.println(name() + "::performAddRemovePoints(" + x + "," + y + "," + z + ") -> Error, ");
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	int idz = getIndex(z);
    	performAddRemovePoints(idx, idy, idz);
    }
    
    private void performAddRemovePoints(int x, int y, int z){
    	copySolution();
    	if (prev[x] == z) {
    		next[y] = next[x];
    		prev[y] = prev[x];
    		next[z] = y;
    		prev[next[x]] = y;
    		route[y] = route[x];
    		update(route[z]);
    	} else {
    		next[prev[x]] = next[x];
    		prev[next[x]] = prev[x];
    		next[y] = next[z];
    		prev[next[y]] = y;
    		prev[y] = z;
    		next[z] = y;
    		route[y] = route[z];
    		update(route[z]);
    		if (route[x] != route[z]) {
    			update(route[x]);
    		}
    	}
    	index[x] = next[x] = prev[x] = route[x] = Constants.NULL_POINT;
    }
    
    public boolean checkPerformRemoveSequencePoints(Point x, Point y){
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	if (index[idx] == Constants.NULL_POINT || index[idy] == Constants.NULL_POINT) {
    		return false;
    	}
    	return isBefore(x,y);
    }
    
    public void performRemoveSequencePoints(Point x, Point y){
    	// remove points from next[x] to prev[y]
    	// set next[x] = y
    	if(!checkPerformRemoveSequencePoints(x, y)){
    		System.out.println(name() + "::performRemoveSequecePoints(" + x + "," + y + ") -> Error, " + x + " is not before " + y);
    		System.exit(-1);
    	}
    	int idx = getIndex(x);
    	int idy = getIndex(y);
    	for(int v = next[idx]; v != idy; v = next[v]){
    		index[v] = Constants.NULL_POINT;
    	}
    	copySolution();
    	next[idx] = idy;
    	prev[idy] = idx;
    	update(route[idx]);
    }
    
    public boolean checkPerformKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
    	if (x.size() != y.size()) {
    		return false;
    	}
    	//HashSet<Point> s = new HashSet<Point>(x);
    	//if (x.size() != s.size()) {
    		//return false;
    	//}
    	
    	for (Point p : y) {
    		if (p != CBLSVR.NULL_POINT && (route[getIndex(p)] == Constants.NULL_POINT || isTerminatingPoint(p))) {
    			return false;
    		}
    	}
    	HashSet<Point> set = new HashSet<Point>(y);
    	for (Point p : x) {
    		if ((!isClientPoint(p)) || set.contains(p)) {
    			return false;
    		}
    	}
    	for(int i = 0; i < x.size(); i++){
    		Point px = x.get(i);
    		Point py = y.get(i);
    		int ix = getIndex(px);
    		int iy = getIndex(py);
    		if(ix == Constants.NULL_POINT && iy == Constants.NULL_POINT) return false;
    		if(next(py) == px) return false;
    		
    		//if(route[ix] == Constants.NULL_POINT && route[iy] == Constants.NULL_POINT) return false;
    	}
    	return true;
    }
    
    public void performKPointsMove(ArrayList<Point> x, ArrayList<Point> y){
    	// remove x[0...x.size()-1] from current routes
    	// re-insert x[i] right-after y[i], forall i = 0,...,x.size()-1
    	// application: Large Neighborhood Search
    	// if y[i] = CBLSVR.NULL_POINT, then x[i] is removed from current routes
    	// if y[i1] = y[i2] = ...= y[ik], then re-insert x[i1], x[i2], ..., x[ik] in that order right-after y[i1] 
    	if (!checkPerformKPointsMove(x, y)) {
    		System.out.println(name() + "::performKPointsMove -> Error");
    		System.exit(-1);
    	}
    	copySolution();
    	HashSet<Integer> oldR = new HashSet<Integer>();
    	for (int i = x.size() - 1; i >= 0; i--) {
    		Point p = x.get(i);
    		Point q = y.get(i);
    		if (q != CBLSVR.NULL_POINT) {
    			oldR.add(oldRoute(q));
    			oldR.add(oldRoute(p));
    			int idx = getIndex(p);
    			int idy = getIndex(q);
    			move(idx, idy);
    		} else {
    			oldR.add(oldRoute(p));
    			int idx = getIndex(p);
    			if (route[idx] != Constants.NULL_POINT) {
    				prev[next[idx]] = prev[idx];
    				next[prev[idx]] = next[idx];
    			}
    			index[idx] = next[idx] = prev[idx] = route[idx] = Constants.NULL_POINT;
    		}
    	}
    	for (int r : oldR) {
    		if (r != Constants.NULL_POINT) {
    			update(r);
    		}
    	}
    }
    
    
    public ArrayList<Point> collectClientPointsOnRoutes(){
    	ArrayList<Point> L = new ArrayList<Point>();
    	for(Point p: clientPoints){
    		if(contains(p))
    			L.add(p);
    	}
    	return L;
    }
    
    public int getNbClientPointsOnRoutes(){
    	int sz = 0;
    	for(Point p: clientPoints){
    		if(contains(p))
    			sz++;
    	}
    	return sz;
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		VRManager mgr = new VRManager();
		VarRoutesVR XR = new VarRoutesVR(mgr);
		Point s1 = new Point(1);
		Point t1 = new Point(2);
		Point s2 = new Point(3);
		Point t2 = new Point(4);
		
		ArrayList<Point> points = new ArrayList<Point>();
		XR.addRoute(s1, t1);
		XR.addRoute(s2, t2);
		for(int i = 5; i <= 12; i++){
			Point p = new Point(i);
			points.add(p);
			XR.addClientPoint(p);
		}		
		mgr.close();
		Point p5 = points.get(0);
		Point p6 = points.get(1);
		Point p7 = points.get(2);
		Point p8 = points.get(3);
		Point p9 = points.get(4);
		Point p10 = points.get(5);
		Point p11 = points.get(6);
		Point p12 = points.get(7);
		
		System.out.println(XR.toString());
		mgr.performAddOnePoint(points.get(0), s1);
		mgr.performAddOnePoint(points.get(1), points.get(0));
		mgr.performAddOnePoint(points.get(2), points.get(1));
		mgr.performAddOnePoint(points.get(3), s2);
		mgr.performAddOnePoint(points.get(4), points.get(3));
		mgr.performAddOnePoint(points.get(5), points.get(4));
		mgr.performAddOnePoint(p11, p7);
		mgr.performAddOnePoint(p12, p10);
		
		System.out.println(XR.toString());
		
		mgr.performTwoOptMove5(p6, p9);
		System.out.println(XR.toString());
	}

}
