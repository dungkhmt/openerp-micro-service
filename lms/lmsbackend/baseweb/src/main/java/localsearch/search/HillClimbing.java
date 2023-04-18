
package localsearch.search;

import java.util.ArrayList;
import java.util.Random;
import localsearch.model.ConstraintSystem;
import localsearch.model.VarIntLS;

public class HillClimbing {
    class AssignMove {
        int i;
        int v;
        public AssignMove(int i, int v) {
            this.i = i; this.v = v;
        }
    }  
    public void hillClimbing(ConstraintSystem c, int maxIter) {
        VarIntLS[] y = c.getVariables();
        ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
        Random R = new Random();
        int it = 0;
        while(it < maxIter && c.violations() > 0) {
            cand.clear();
            int minDelta = Integer.MAX_VALUE;
            for(int i = 0; i < y.length; i++) {
//                for(int v = y[i].getValue(); v <= y[i].getMaxValue(); v++) {
                for(int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
                    int d = c.getAssignDelta(y[i], v);
                    if(d < minDelta) {
                        cand.clear();
                        cand.add(new AssignMove(i, v));
                        minDelta = d;
                    }else if(d == minDelta) {
                        cand.add(new AssignMove(i, v));
                    }
                }
            }
            int idx = R.nextInt(cand.size());   
            AssignMove m = cand.get(idx);
            y[m.i].setValuePropagate(m.v);
            System.out.println("Step " + it + ", violations = " + c.violations());
            it++;
        }
    }
}
