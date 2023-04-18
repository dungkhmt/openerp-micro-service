
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 27/09/2015
 */

package localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration;

import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public interface INeighborhoodExplorer {
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval);
	public void performMove(IVRMove m);
	public String name();
}
