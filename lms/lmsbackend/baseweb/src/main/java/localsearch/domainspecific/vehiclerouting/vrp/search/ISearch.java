
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 27/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp.search;

import localsearch.domainspecific.vehiclerouting.vrp.ValueRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;

public interface ISearch {
	public LexMultiValues getIncumbentValue();
	public int getCurrentIteration();
	public ValueRoutesVR getIncumbent();
	public void search(int maxIter, int timeLimit);
}
