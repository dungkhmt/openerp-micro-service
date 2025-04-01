package openerp.openerpresourceserver.api.vrp.service;

import openerp.openerpresourceserver.api.vrp.model.cvrp.ModelInputCVRP;
import openerp.openerpresourceserver.api.vrp.model.cvrp.ModelResonseCVRP;
import openerp.openerpresourceserver.api.vrp.model.cvrp.Point;
import openerp.openerpresourceserver.api.vrp.model.cvrp.Vehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVRPGreedySolver {
    ModelInputCVRP I;
    int n; // number points;
    double[] d;// demand
    int m; // number vehicles
    double[] cap; // capacity
    double[][] dis; // distance matrix
    Map<String, Integer> mPointId2Index;
    public ModelResonseCVRP solve(ModelInputCVRP I){
        this.n = I.getPoints().size();
        this.m = I.getVehicles().size();
        d = new double[n];
        cap = new double[m];
        mPointId2Index = new HashMap();
        for(int i = 0; i < n; i++){
            Point p = I.getPoints().get(i);
            mPointId2Index.put(p.getId(),i);
        }
        for(int i = 0;i < I.getVehicles().size(); i++){
            Vehicle v = I.getVehicles().get(i);
            mPointId2Index.put(v.getDepot().getId(), i);
        }
        List<Point> points = I.getPoints();
        return null;
    }
}
